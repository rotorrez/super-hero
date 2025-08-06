@ExtendWith(MockitoExtension.class)
class AppianEventServiceTest {

    @InjectMocks
    AppianEventService service;

    @Mock
    AppianRestClient restClient;

    @Mock
    @ConfigProperty(name = "appian-api.client-id")
    String clientId = "dummy-client-id";

    @Mock
    @ConfigProperty(name = "appian-api.channel")
    String channel = "dummy-channel";

    @Mock
    TokenProvider tokenProvider;

    @Test
    void testTriggerEvent_Successful() {
        // Arrange
        String caseNumber = "123";
        String event = "start";
        String bearerToken = "Bearer abc";

        AppianEventPayloadDTO payload = AppianEventPayloadDTO.builder()
            .processCode("PROC-01")
            .customersIdentification(List.of("CUST-01"))
            .contactPoint(new AppianContactPointDTO())
            .specificInformation(null)
            .build();

        AppianEventResponseDTO expectedResponse = AppianEventResponseDTO.builder()
            .status("OK")
            .message("Event triggered")
            .build();

        when(tokenProvider.getBearerToken()).thenReturn(bearerToken);
        when(restClient.triggerEvent(
                eq(caseNumber),
                eq(event),
                eq(payload),
                eq("Bearer abc"),
                eq(clientId),
                eq(channel)
        )).thenReturn(expectedResponse);

        // Act
        AppianEventResponseDTO result = service.triggerEvent(caseNumber, event, payload);

        // Assert
        assertNotNull(result);
        assertEquals("OK", result.getStatus());
        assertEquals("Event triggered", result.getMessage());
    }
}
