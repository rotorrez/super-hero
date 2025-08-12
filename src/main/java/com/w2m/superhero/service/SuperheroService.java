package com.santander.san.audobs.sanaudobsbamoecoexislib.service.andgo;

import com.santander.san.audobs.sanaudobsbamoecoexislib.dto.andgo.*;
import com.santander.san.audobs.sanaudobsbamoecoexislib.integration.andgo.rest.AppianCaseAndGoRestClient;
import com.santander.san.audobs.sanaudobsbamoecoexislib.security.TokenProvider;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppianCaseAndGoServiceTest {

    @Mock
    AppianCaseAndGoRestClient client;

    @Mock
    TokenProvider tokenProvider;

    @InjectMocks
    AppianCaseAndGoService service;

    @BeforeEach
    void injectConfig() throws Exception {
        setPrivateField(service, "clientId", "PRCOS2");
        when(tokenProvider.getBearerToken()).thenReturn("Bearer ABC");
    }

    // util simple para setear @ConfigProperty en tests unitarios
    private static void setPrivateField(Object target, String field, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(target, value);
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
                .specificInformation(SpecificInformationDTO.builder().idMongo("231516").avales(Collections.emptyList()).build())
                .build();
    }

    private StartFastProcessResponseDTO sampleResponseWithOneTask() {
        return StartFastProcessResponseDTO.builder()
                .status("OK").message("Satisfactory execution.")
                .tasks(List.of(NextTaskDTO.builder().taskCode("ppD1L_03").taskDescription("desc").caseIdentifier("001").url("https://u").build()))
                .build();
    }

    @Test
    void startFastProcess_success() {
        when(client.startFastProcess("Bearer ABC", "PRCOS2", "IPFO", sampleRequest()))
                .thenReturn(sampleResponseWithOneTask());

        StartFastProcessResponseDTO resp = service.startFastProcess("IPFO", sampleRequest());

        assertEquals("OK", resp.getStatus());
        verify(tokenProvider).getBearerToken();
        verify(client).startFastProcess("Bearer ABC", "PRCOS2", "IPFO", sampleRequest());
    }

    @Test
    void startFastProcess_handlesNullTasks() {
        StartFastProcessResponseDTO noTasks = StartFastProcessResponseDTO.builder()
                .status("OK").message("ok").tasks(null).build();
        when(client.startFastProcess(anyString(), anyString(), anyString(), ArgumentMatchers.any()))
                .thenReturn(noTasks);

        StartFastProcessResponseDTO resp = service.startFastProcess("IPFO", sampleRequest());
        assertNull(resp.getTasks()); // solo verificamos que no rompe (log interno contabiliza 0)
    }

    @Test
    void startFastProcess_webApplicationException_wrappedWithStatusAndBody() {
        Response r = Response.status(400).entity("Bad request").build();
        when(client.startFastProcess(anyString(), anyString(), anyString(), ArgumentMatchers.any()))
                .thenThrow(new WebApplicationException(r));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.startFastProcess("IPFO", sampleRequest()));

        assertTrue(ex.getMessage().contains("HTTP 400"));
        assertTrue(ex.getMessage().toLowerCase().contains("bad request"));
    }

    @Test
    void startFastProcess_processingException_wrapped() {
        when(client.startFastProcess(anyString(), anyString(), anyString(), ArgumentMatchers.any()))
                .thenThrow(new ProcessingException("timeout"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.startFastProcess("IPFO", sampleRequest()));

        assertTrue(ex.getMessage().toLowerCase().contains("connection"));
    }

    @Test
    void startFastProcess_unexpected_wrapped() {
        when(client.startFastProcess(anyString(), anyString(), anyString(), ArgumentMatchers.any()))
                .thenThrow(new IllegalStateException("boom"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.startFastProcess("IPFO", sampleRequest()));

        assertTrue(ex.getMessage().toLowerCase().contains("unexpected"));
    }
}
