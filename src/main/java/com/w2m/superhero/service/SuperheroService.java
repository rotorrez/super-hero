package com.santander.san.audobs.sanaudobsbamoecoexislib.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.santander.san.audobs.sanaudobsbamoecoexislib.dto.AppianEventPayloadDTO;
import com.santander.san.audobs.sanaudobsbamoecoexislib.dto.AppianEventRequestDTO;
import com.santander.san.audobs.sanaudobsbamoecoexislib.dto.AppianEventResponseDTO;
import com.santander.san.audobs.sanaudobsbamoecoexislib.dto.AppianContactPointDTO;
import com.santander.san.audobs.sanaudobsbamoecoexislib.dto.CaseRelationDTO;
import com.santander.san.audobs.sanaudobsbamoecoexislib.integration.adapter.AccessPointAdapter;
import com.santander.san.audobs.sanaudobsbamoecoexislib.integration.client.AppianRestClient;
import com.santander.san.audobs.sanaudobsbamoecoexislib.integration.service.CoexistenceService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AppianEventServiceTest {

    private AppianEventService appianEventService;

    private AppianRestClient appianRestClient;
    private AccessPointAdapter accessPointAdapter;
    private CoexistenceService coexistenceService;

    @BeforeEach
    void setUp() {
        appianRestClient = mock(AppianRestClient.class);
        accessPointAdapter = mock(AccessPointAdapter.class);
        coexistenceService = mock(CoexistenceService.class);

        appianEventService = new AppianEventService();
        appianEventService.appianRestClient = appianRestClient;
        appianEventService.accessPointAdapter = accessPointAdapter;
        appianEventService.coexistenceService = coexistenceService;

        // Simular valores de configuración
        appianEventService.clientId = "test-client-id";
        appianEventService.channel = "test-channel";
    }

    @Test
    void testTriggerEvent_Successful() throws Exception {
        // Arrange
        AppianEventRequestDTO request = AppianEventRequestDTO.builder()
                .idCaso(123)
                .event("EVENT_TYPE")
                .processCode("PROC-01")
                .customersIdentification(List.of("CUST-01"))
                .idAccessPoint(456)
                .specificInformation(new ObjectMapper().readTree("{\"field\":\"value\"}"))
                .build();

        CaseRelationDTO caseRelationDTO = new CaseRelationDTO();
        caseRelationDTO.setFictionalCaseId("CASE-456");

        AppianContactPointDTO contactPointDTO = AppianContactPointDTO.builder()
                .email("test@example.com")
                .build();

        AppianEventResponseDTO expectedResponse = AppianEventResponseDTO.builder()
                .status("SUCCESS")
                .message("Triggered successfully")
                .build();

        when(coexistenceService.getCaseRelationByPpaasCaseId("123")).thenReturn(caseRelationDTO);
        when(accessPointAdapter.getContactPointById(456)).thenReturn(contactPointDTO);
        when(appianRestClient.triggerEvent(
                eq("CASE-456"),
                eq("EVENT_TYPE"),
                any(AppianEventPayloadDTO.class),
                anyString(), // sessionId
                eq("test-client-id"),
                eq("test-channel")
        )).thenReturn(expectedResponse);

        // Act
        AppianEventResponseDTO actualResponse = appianEventService.triggerEvent(request);

        // Assert
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());

        verify(coexistenceService).getCaseRelationByPpaasCaseId("123");
        verify(accessPointAdapter).getContactPointById(456);
        verify(appianRestClient).triggerEvent(
                eq("CASE-456"),
                eq("EVENT_TYPE"),
                any(AppianEventPayloadDTO.class),
                anyString(),
                eq("test-client-id"),
                eq("test-channel")
        );
    }
}
