@ExtendWith(MockitoExtension.class)
class AppianRestClientTest {

    @Mock
    AppianRestClient appianRestClient;

    @Test
    void shouldCallTriggerEventSuccessfully() {
        // Arrange
        AppianEventRequestDTO request = AppianEventRequestDTO.builder()
            .idCaso(1)
            .event("eventName")
            .processCode("PROC123")
            .customersIdentification(List.of("456"))
            .idAccessPoint(101)
            .specificInformation("info")
            .build();

        AppianEventResponseDTO response = AppianEventResponseDTO.builder()
            .message("Success")
            .status("200 OK")
            .build();

        when(appianRestClient.triggerEvent(eq("client123"), eq("channelABC"), eq(request)))
            .thenReturn(response);

        // Act
        AppianEventResponseDTO result = appianRestClient.triggerEvent("client123", "channelABC", request);

        // Assert
        assertNotNull(result);
        assertEquals("Success", result.getMessage());
        assertEquals("200 OK", result.getStatus());
        verify(appianRestClient).triggerEvent(any(), any(), any());
    }
}
