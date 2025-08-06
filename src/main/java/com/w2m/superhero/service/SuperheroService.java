import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.santander.san.audobs.sanaudobsbamoecoexislib.dto.AppianEventPayloadDTO;
import com.santander.san.audobs.sanaudobsbamoecoexislib.dto.AppianEventRequestDTO;
import com.santander.san.audobs.sanaudobsbamoecoexislib.dto.AppianEventResponseDTO;
import com.santander.san.audobs.sanaudobsbamoecoexislib.integration.client.AppianRestClient;
import com.santander.san.audobs.sanaudobsbamoecoexislib.service.AppianEventService;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@QuarkusTest
class AppianEventServiceTest {

    @Mock
    AppianRestClient appianRestClient;

    @InjectMocks
    AppianEventService appianEventService;

    private final String sessionId = "test-session";
    private final String clientId = "test-client";
    private final String channel = "test-channel";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Inyectar config properties manualmente si es necesario
        appianEventService.sessionId = sessionId;
        appianEventService.clientId = clientId;
        appianEventService.channel = channel;
    }

    @Test
    void triggerEvent_successfulResponse() {
        // Given
        AppianEventRequestDTO request = new AppianEventRequestDTO();
        request.setIdCaso(1);
        request.setEvent("APPROVED");
        request.setProcessCode("P1");
        request.setCustomersIdentification("12345678A");
        request.setIdAccessPoint(99);

        AppianEventPayloadDTO payload = AppianEventPayloadDTO.builder()
                .processCode("P1")
                .customersIdentification("12345678A")
                .contactPoint(null)
                .specificInformation(null)
                .build();

        AppianEventResponseDTO expectedResponse = new AppianEventResponseDTO();
        expectedResponse.setStatus("OK");

        when(appianRestClient.triggerEvent(
                eq("000001"), // caseNumber simulado desde getCaseNumberByIdCaso()
                eq("APPROVED"),
                any(AppianEventPayloadDTO.class),
                eq(sessionId),
                eq(clientId),
                eq(channel))
        ).thenReturn(expectedResponse);

        // Mock del método interno getCaseNumberByIdCaso()
        AppianEventService spyService = spy(appianEventService);
        doReturn("000001").when(spyService).getCaseNumberByIdCaso(1);
        doReturn(null).when(spyService).getContactPointById(99);

        // When
        AppianEventResponseDTO actual = spyService.triggerEvent(request);

        // Then
        assertNotNull(actual);
        assertEquals("OK", actual.getStatus());
    }

    @Test
    void triggerEvent_clientThrowsException() {
        AppianEventRequestDTO request = new AppianEventRequestDTO();
        request.setIdCaso(1);
        request.setEvent("REJECTED");
        request.setProcessCode("P2");
        request.setCustomersIdentification("99999999Z");
        request.setIdAccessPoint(55);

        when(appianRestClient.triggerEvent(
                any(), any(), any(), any(), any(), any())
        ).thenThrow(new RuntimeException("Unexpected error"));

        AppianEventService spyService = spy(appianEventService);
        doReturn("999999").when(spyService).getCaseNumberByIdCaso(1);
        doReturn(null).when(spyService).getContactPointById(55);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            spyService.triggerEvent(request);
        });

        assertTrue(ex.getMessage().contains("Unexpected error"));
    }
} 
