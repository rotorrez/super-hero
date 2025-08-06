package com.santander.san.audobs.sanaudobsbamoecoexislib.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.santander.san.audobs.sanaudobsbamoecoexislib.dto.*;
import com.santander.san.audobs.sanaudobsbamoecoexislib.integration.adapter.AccessPointAdapter;
import com.santander.san.audobs.sanaudobsbamoecoexislib.integration.client.AppianRestClient;
import com.santander.san.audobs.sanaudobsbamoecoexislib.integration.client.dto.AppianContactPointDTO;
import com.santander.san.audobs.sanaudobsbamoecoexislib.integration.client.dto.AppianEventPayloadDTO;
import com.santander.san.audobs.sanaudobsbamoecoexislib.integration.client.dto.AppianEventResponseDTO;
import com.santander.san.audobs.sanaudobsbamoecoexislib.integration.client.dto.CaseRelationDTO;
import com.santander.san.audobs.sanaudobsbamoecoexislib.service.CoexistenceService;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppianEventServiceTest {

    @Mock
    AppianRestClient appianRestClient;

    @Mock
    AccessPointAdapter accessPointAdapter;

    @Mock
    CoexistenceService coexistenceService;

    @InjectMocks
    AppianEventService appianEventService;

    @BeforeEach
    void setUp() {
        appianEventService.clientId = "test-client";
        appianEventService.channel = "test-channel";
    }

    @Test
    void testTriggerEvent_Successful() {
        AppianEventRequestDTO request = buildRequest();

        when(coexistenceService.getCaseRelationByPpaasCaseId("123")).thenReturn(CaseRelationDTO.builder().fictionalCaseId("FC123").build());
        when(accessPointAdapter.getContactPointById(10)).thenReturn(AppianContactPointDTO.builder().email("test@email.com").build());

        AppianEventResponseDTO expectedResponse = AppianEventResponseDTO.builder().status("SUCCESS").message("OK").build();
        when(appianRestClient.triggerEvent(any(), any(), any(), any(), any(), any()))
                .thenReturn(expectedResponse);

        AppianEventResponseDTO result = appianEventService.triggerEvent(request);

        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
    }

    @Test
    void testTriggerEvent_WebApplicationException() {
        AppianEventRequestDTO request = buildRequest();

        when(coexistenceService.getCaseRelationByPpaasCaseId("123")).thenReturn(CaseRelationDTO.builder().fictionalCaseId("FC123").build());
        when(accessPointAdapter.getContactPointById(10)).thenReturn(AppianContactPointDTO.builder().email("test@email.com").build());

        Response mockResponse = mock(Response.class);
        when(mockResponse.hasEntity()).thenReturn(true);
        when(mockResponse.readEntity(String.class)).thenReturn("Appian Error");
        when(mockResponse.getStatus()).thenReturn(500);

        when(appianRestClient.triggerEvent(any(), any(), any(), any(), any(), any()))
                .thenThrow(new WebApplicationException(mockResponse));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appianEventService.triggerEvent(request);
        });

        assertTrue(exception.getMessage().contains("Appian service error"));
    }

    @Test
    void testTriggerEvent_ProcessingException() {
        AppianEventRequestDTO request = buildRequest();

        when(coexistenceService.getCaseRelationByPpaasCaseId("123")).thenReturn(CaseRelationDTO.builder().fictionalCaseId("FC123").build());
        when(accessPointAdapter.getContactPointById(10)).thenReturn(AppianContactPointDTO.builder().email("test@email.com").build());

        when(appianRestClient.triggerEvent(any(), any(), any(), any(), any(), any()))
                .thenThrow(new ProcessingException("Connection failed"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appianEventService.triggerEvent(request);
        });

        assertTrue(exception.getMessage().contains("Connection error"));
    }

    @Test
    void testTriggerEvent_GenericException() {
        AppianEventRequestDTO request = buildRequest();

        when(coexistenceService.getCaseRelationByPpaasCaseId("123")).thenReturn(CaseRelationDTO.builder().fictionalCaseId("FC123").build());
        when(accessPointAdapter.getContactPointById(10)).thenReturn(AppianContactPointDTO.builder().email("test@email.com").build());

        when(appianRestClient.triggerEvent(any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appianEventService.triggerEvent(request);
        });

        assertTrue(exception.getMessage().contains("Unexpected error"));
    }

    @Test
    void testGetCaseNumberByIdCaso_Success() {
        when(coexistenceService.getCaseRelationByPpaasCaseId("123"))
                .thenReturn(CaseRelationDTO.builder().fictionalCaseId("FC123").build());

        String result = appianEventService.getCaseNumberByIdCaso(123);
        assertEquals("FC123", result);
    }

    @Test
    void testGetCaseNumberByIdCaso_NullDto() {
        when(coexistenceService.getCaseRelationByPpaasCaseId("123")).thenReturn(null);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                appianEventService.getCaseNumberByIdCaso(123)
        );

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void testGetCaseNumberByIdCaso_NullFictionalId() {
        when(coexistenceService.getCaseRelationByPpaasCaseId("123"))
                .thenReturn(CaseRelationDTO.builder().fictionalCaseId(null).build());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                appianEventService.getCaseNumberByIdCaso(123)
        );

        assertTrue(exception.getMessage().contains("not found"));
    }

    private AppianEventRequestDTO buildRequest() {
        return AppianEventRequestDTO.builder()
                .idCaso(123)
                .event("case.created")
                .processCode("CODE")
                .customersIdentification(List.of("ABC123"))
                .idAccessPoint(10)
                .specificInformation(new ObjectMapper().createObjectNode())
                .build();
    }
}
