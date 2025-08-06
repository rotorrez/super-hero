package com.tu.paquete.client;

import com.tu.paquete.dto.CoexistenceTaskDTO;
import com.tu.paquete.dto.CaseRelationDTO;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoexistenceRestClientTest {

    @Mock
    CoexistenceRestClient client;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPerformPostCoexistence_success() {
        CoexistenceTaskDTO dto = new CoexistenceTaskDTO();
        dto.setTaskId("T123");
        dto.setCaseId("C456");
        dto.setTaskName("SampleTask");

        Response mockResponse = mock(Response.class);
        when(mockResponse.getStatus()).thenReturn(200);

        when(client.performPostCoexistence(eq(dto), anyString(), anyString()))
            .thenReturn(mockResponse);

        Response response = client.performPostCoexistence(dto, "Bearer xyz", "PRCOS2");

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        verify(client).performPostCoexistence(dto, "Bearer xyz", "PRCOS2");
    }

    @Test
    void testPerformDeleteCoexistence_success() {
        Response mockResponse = mock(Response.class);
        when(mockResponse.getStatus()).thenReturn(204);

        when(client.performDeleteCoexistence(eq("T123"), anyString(), anyString()))
            .thenReturn(mockResponse);

        Response response = client.performDeleteCoexistence("T123", "Bearer xyz", "PRCOS2");

        assertNotNull(response);
        assertEquals(204, response.getStatus());
    }

    @Test
    void testPerformGetCoexistence_success() {
        CaseRelationDTO mockDto = new CaseRelationDTO();
        mockDto.setCaseId("C456");

        when(client.performGetCoexistence(eq("C456"), anyString(), anyString()))
            .thenReturn(mockDto);

        CaseRelationDTO result = client.performGetCoexistence("C456", "Bearer xyz", "PRCOS2");

        assertNotNull(result);
        assertEquals("C456", result.getCaseId());
    }
}
