package com.santander.san.audobs.sanaudobsbamoecoexislib.integration.andgo.rest;

import com.santander.san.audobs.sanaudobsbamoecoexislib.dto.andgo.*;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class AppianCaseAndGoRestClientTest {

    @Mock
    AppianCaseAndGoRestClient client;

    AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    private StartFastProcessRequestDTO sampleRequest() {
        return StartFastProcessRequestDTO.builder()
                .contactPoint(AndgoContactPointDTO.builder()
                        .userType("GOFT").operativeSystem("WIN").device("PC").codeApp("50080398").build())
                .customersIdentification(List.of("f123456789"))
                .user(AndgoUserDTO.builder().userUid("aplsoporte").branchCode("0000").build())
                .companyCode("0049")
                .creationBranchCode("0000")
                .externalReference("")
                .specificInformation(SpecificInformationDTO.builder()
                        .idMongo("231516").avales(Collections.emptyList()).build())
                .build();
    }

    private StartFastProcessResponseDTO sampleResponseOneTask() {
        return StartFastProcessResponseDTO.builder()
                .status("OK").message("Satisfactory execution.")
                .tasks(List.of(NextTaskDTO.builder()
                        .caseIdentifier("0049...07223")
                        .taskCode("ppD1L_03")
                        .taskDescription("Tarea Formaliza Solicitud.")
                        .url("https://example").build()))
                .build();
    }

    private ArgumentMatcher<StartFastProcessRequestDTO> matchesRequest() {
        return r -> r != null
                && "GOFT".equals(r.getContactPoint().getUserType())
                && "50080398".equals(r.getContactPoint().getCodeApp())
                && r.getCustomersIdentification() != null
                && r.getCustomersIdentification().contains("f123456789")
                && "0049".equals(r.getCompanyCode());
    }

    @Test
    void startFastProcess_success() {
        when(client.startFastProcess(eq("Bearer xyz"), eq("PRCOS2"), eq("IPFO"),
                argThat(matchesRequest())))
                .thenReturn(sampleResponseOneTask());

        StartFastProcessResponseDTO resp =
                client.startFastProcess("Bearer xyz", "PRCOS2", "IPFO", sampleRequest());

        assertNotNull(resp);
        assertEquals("OK", resp.getStatus());
        assertEquals(1, resp.getTasks().size());
        assertEquals("ppD1L_03", resp.getTasks().get(0).getTaskCode());
    }

    @Test
    void startFastProcess_nullTasks_ok() {
        StartFastProcessResponseDTO noTasks = StartFastProcessResponseDTO.builder()
                .status("OK").message("ok").tasks(null).build();

        when(client.startFastProcess(anyString(), anyString(), anyString(), any()))
                .thenReturn(noTasks);

        StartFastProcessResponseDTO resp =
                client.startFastProcess("Bearer xyz", "PRCOS2", "IPFO", sampleRequest());

        assertEquals("OK", resp.getStatus());
        assertNull(resp.getTasks());
    }

    @Test
    void startFastProcess_webApplicationException() {
        Response r = Response.status(400).entity("Bad request").build();
        when(client.startFastProcess(anyString(), anyString(), anyString(), any()))
                .thenThrow(new WebApplicationException(r));

        assertThrows(WebApplicationException.class, () ->
                client.startFastProcess("Bearer xyz", "PRCOS2", "IPFO", sampleRequest()));
    }

    @Test
    void startFastProcess_processingException() {
        when(client.startFastProcess(anyString(), anyString(), anyString(), any()))
                .thenThrow(new ProcessingException("timeout"));

        ProcessingException ex = assertThrows(ProcessingException.class, () ->
                client.startFastProcess("Bearer xyz", "PRCOS2", "IPFO", sampleRequest()));
        assertTrue(ex.getMessage().toLowerCase().contains("timeout"));
    }

    @Test
    void startFastProcess_unexpectedException() {
        when(client.startFastProcess(anyString(), anyString(), anyString(), any()))
                .thenThrow(new IllegalStateException("boom"));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                client.startFastProcess("Bearer xyz", "PRCOS2", "IPFO", sampleRequest()));
        assertTrue(ex.getMessage().contains("boom"));
    }
}
