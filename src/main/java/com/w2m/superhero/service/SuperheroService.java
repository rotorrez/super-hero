@ExtendWith(MockitoExtension.class)
class AppianRestClientTest {

    @Mock
    AppianRestClient appianRestClient;

    @Test
    void shouldCallTriggerEventSuccessfully() {
        // Arrange
        String caseNumber = "CASE123";
        String event = "START";
        String sessionId = UUID.randomUUID().toString();
        String clientId = "clientIdTest";
        String channel = "INT";

        AppianEventPayloadDTO payload = AppianEventPayloadDTO.builder()
            .processCode("PROC001")
            .customersIdentification(List.of("123456"))
            .contactPoint(new AppianContactPointDTO("POINT001"))
            .specificInformation("Some info")
            .build();

        AppianEventResponseDTO expectedResponse = AppianEventResponseDTO.builder()
            .status("200 OK")
            .message("Triggered")
            .build();

        when(appianRestClient.triggerEvent(
                eq(caseNumber), eq(event), eq(payload),
                eq(sessionId), eq(clientId), eq(channel)
        )).thenReturn(expectedResponse);

        // Act
        AppianEventResponseDTO actualResponse = appianRestClient.triggerEvent(
            caseNumber, event, payload, sessionId, clientId, channel
        );

        // Assert
        assertNotNull(actualResponse);
        assertEquals("200 OK", actualResponse.getStatus());
        assertEquals("Triggered", actualResponse.getMessage());

        verify(appianRestClient).triggerEvent(any(), any(), any(), any(), any(), any());
    }
}
