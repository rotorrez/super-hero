package com.santander.san.audobs.sanaudobsbamoecoexislib.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.santander.san.audobs.sanaudobsbamoecoexislib.client.AppianRestClient;
import com.santander.san.audobs.sanaudobsbamoecoexislib.dto.*;
import com.santander.san.audobs.sanaudobsbamoecoexislib.integration.security.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppianEventServiceTest {

    @Mock
    AppianRestClient appianRestClient;

    @Mock
    TokenProvider tokenProvider;

    @Mock
    CoexistenceService coexistenceService;

    @InjectMocks
    AppianEventService service;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTriggerEvent_Successful() {
        // Arrange
        AppianEventRequestDTO request = AppianEventRequestDTO.builder()
                .idCaso(123)
                .event("trigger_case")
                .processCode("P-001")
                .customersIdentification(List.of("99999999Z"))
                .idAccessPoint(456)
                .specificInformation(mapper.createObjectNode().put("test", "value"))
                .build();

        AppianEventResponseDTO expectedResponse = AppianEventResponseDTO.builder()
                .status("OK")
                .message("Triggered successfully")
                .build();

        CaseRelationDTO caseRelation = CaseRelationDTO.builder()
                .caseNumber("CASE-999")
                .build();

        when(tokenProvider.getBearerToken()).thenReturn("Bearer dummy-token");
        when(tokenProvider.getClientId()).thenReturn("client-id");
        when(tokenProvider.getChannel()).thenReturn("channel-id");

        when(coexistenceService.getCaseRelationByPpaasCaseId("123")).thenReturn(caseRelation);
        when(appianRestClient.triggerEvent(
                eq("CASE-999"),
                eq("trigger_case"),
                any(AppianEventPayloadDTO.class),
                eq("dummy-token"),
                eq("client-id"),
                eq("channel-id")
        )).thenReturn(expectedResponse);

        // Act
        AppianEventResponseDTO response = service.triggerEvent(request);

        // Assert
        assertNotNull(response);
        assertEquals("OK", response.getStatus());
        assertEquals("Triggered successfully", response.getMessage());

        // Verify payload was built as expected
        verify(appianRestClient).triggerEvent(
                eq("CASE-999"),
                eq("trigger_case"),
                argThat(payload ->
                        payload.getProcessCode().equals("P-001") &&
                        payload.getCustomersIdentification().contains("99999999Z") &&
                        payload.getSpecificInformation().has("test")
                ),
                eq("dummy-token"),
                eq("client-id"),
                eq("channel-id")
        );
    }
}
