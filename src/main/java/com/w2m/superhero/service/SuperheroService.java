import com.santander.san.audobs.sanaudobsbamoecoexislib.adapter.AccessPointAdapter;
import com.santander.san.audobs.sanaudobsbamoecoexislib.dto.AppianEventPayloadDTO;
import com.santander.san.audobs.sanaudobsbamoecoexislib.dto.AppianEventResponseDTO;
import com.santander.san.audobs.sanaudobsbamoecoexislib.integration.client.AppianRestClient;
import com.santander.san.audobs.sanaudobsbamoecoexislib.service.AppianEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppianEventServiceTest {

    @Mock
    AppianRestClient appianRestClient;

    @Mock
    AccessPointAdapter accessPointAdapter;

    @InjectMocks
    AppianEventService appianEventService;

    private static final String CASE_NUMBER = "123456";
    private static final String EVENT = "startProcess";
    private static final String SESSION_ID = "abc123";
    private static final String CLIENT_ID = "client001";
    private static final String CHANNEL = "channelX";

    private AppianEventPayloadDTO payload;
    private AppianEventResponseDTO expectedResponse;

    @BeforeEach
    void setup() {
        payload = AppianEventPayloadDTO.builder()
                .caseNumber(CASE_NUMBER)
                .event(EVENT)
                .sessionId(SESSION_ID)
                .clientId(CLIENT_ID)
                .channel(CHANNEL)
                .build();

        expectedResponse = AppianEventResponseDTO.builder()
                .result("OK")
                .build();
    }

    @Test
    void triggerEvent_shouldInvokeClientWithCorrectParams() {
        // Arrange
        when(appianRestClient.triggerEvent(
                eq(CASE_NUMBER),
                eq(EVENT),
                any(AppianEventPayloadDTO.class),
                eq(SESSION_ID),
                eq(CLIENT_ID),
                eq(CHANNEL)
        )).thenReturn(expectedResponse);

        // Act
        AppianEventResponseDTO actual = appianEventService.triggerEvent(payload);

        // Assert
        assertNotNull(actual);
        assertEquals("OK", actual.getResult());

        ArgumentCaptor<AppianEventPayloadDTO> payloadCaptor = ArgumentCaptor.forClass(AppianEventPayloadDTO.class);
        verify(appianRestClient, times(1)).triggerEvent(
                eq(CASE_NUMBER),
                eq(EVENT),
                payloadCaptor.capture(),
                eq(SESSION_ID),
                eq(CLIENT_ID),
                eq(CHANNEL)
        );

        AppianEventPayloadDTO sentPayload = payloadCaptor.getValue();
        assertEquals(CASE_NUMBER, sentPayload.getCaseNumber());
        assertEquals(EVENT, sentPayload.getEvent());
    }
}
