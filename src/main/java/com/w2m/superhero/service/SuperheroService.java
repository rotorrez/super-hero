@ExtendWith(MockitoExtension.class)
class AppianEventServiceTest {

    @InjectMocks
    AppianEventService service;

    @Mock
    AppianRestClient appianRestClient;

    @Mock
    TokenProvider tokenProvider;

    @Mock
    CoexistenceService coexistenceService;

    @Mock
    AccessPointAdapter accessPointAdapter;

    @Test
    void testTriggerEvent_Successful() {
        // Arrange: entrada simulada
        AppianEventRequestDTO request = AppianEventRequestDTO.builder()
                .idCaso(123)
                .event("NEW_CASE")
                .processCode("PROC_XYZ")
                .customersIdentification(List.of("123456789A"))
                .idAccessPoint(456)
                .specificInformation(JsonNodeFactory.instance.objectNode().put("extra", "info"))
                .build();

        // Simulaciones
        when(tokenProvider.getToken()).thenReturn("Bearer xyz");
        when(coexistenceService.getCaseRelationByPpaasCaseId("123"))
                .thenReturn(new CaseRelationDTO("INT-999", "EXT-888"));

        AppianContactPointDTO contactPoint = AppianContactPointDTO.builder()
                .contactEmail("test@email.com")
                .contactPhone("123456789")
                .build();
        when(accessPointAdapter.getContactPointById(456)).thenReturn(contactPoint);

        AppianEventResponseDTO expectedResponse = AppianEventResponseDTO.builder()
                .status("OK")
                .message("Event triggered successfully")
                .build();
        when(appianRestClient.triggerEvent(any(), anyString()))
                .thenReturn(Response.ok(expectedResponse).build());

        // Act
        AppianEventResponseDTO result = service.triggerEvent(request);

        // Assert
        assertNotNull(result);
        assertEquals("OK", result.getStatus());
        assertEquals("Event triggered successfully", result.getMessage());
    }
}
