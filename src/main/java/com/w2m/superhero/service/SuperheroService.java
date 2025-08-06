@ExtendWith(MockitoExtension.class)
class AppianEventServiceTest {

    @InjectMocks
    AppianEventService service;

    @Mock
    AppianRestClient restClient;

    @Mock
    TokenProvider tokenProvider;

    @Mock
    @ConfigProperty(name = "appian-api.client-id")
    String clientId = "dummy-client-id";

    @Mock
    @ConfigProperty(name = "appian-api.channel")
    String channel = "dummy-channel";

    @Test
    void testTriggerEvent_Successful() {
        // Arrange
        AppianEventRequestDTO request = AppianEventRequestDTO.builder()
            .idCaso(123)
            .event("start")
            .processCode("P-001")
            .customersIdentification(List.of("ABC123"))
            .idAccessPoint(9)
            .specificInformation(null)
            .build();

        String bearerToken = "Bearer mock-token";

        AppianEventResponseDTO expectedResponse = AppianEventResponseDTO.builder()
            .status("OK")
            .message("Triggered")
            .build();

        when(tokenProvider.getBearerToken()).thenReturn(bearerToken);
        when(restClient.triggerEvent(
                eq(request.getIdCaso().toString()),
                eq(request.getEvent()),
                any(AppianEventPayloadDTO.class),
                eq(bearerToken),
                eq(clientId),
                eq(channel)
        )).thenReturn(expectedResponse);

        // Act
        AppianEventResponseDTO result = service.triggerEvent(request);

        // Assert
        assertNotNull(result);
        assertEquals("OK", result.getStatus());
        assertEquals("Triggered", result.getMessage());
    }
}
