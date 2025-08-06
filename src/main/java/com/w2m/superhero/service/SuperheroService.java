@ExtendWith(MockitoExtension.class)
class AppianEventServiceTest {

    @InjectMocks
    AppianEventService appianEventService;

    @Mock
    AppianRestClient appianRestClient;

    @Mock
    AccessPointAdapter accessPointAdapter;

    @Mock
    CoexistenceService coexistenceService;

    @Test
    void triggerEvent_shouldReturnResponse_whenClientReturnsSuccess() {
        // Arrange
        AppianEventRequestDTO request = AppianEventRequestDTO.builder()
            .idCaso(1)
            .event("create")
            .processCode("PROC1")
            .customersIdentification(List.of("123"))
            .idAccessPoint(99)
            .specificInformation("data")
            .build();

        when(coexistenceService.getCaseRelationByPpaasCaseId("1"))
            .thenReturn(CaseRelationDTO.builder().fictionalCaseId("CASE-001").build());

        when(accessPointAdapter.getContactPointById(99))
            .thenReturn(AppianContactPointDTO.builder().id(99).name("POINT").build());

        AppianEventResponseDTO mockResponse = new AppianEventResponseDTO();
        mockResponse.setStatus("OK");

        when(appianRestClient.triggerEvent(eq("CASE-001"), eq("create"), any()))
            .thenReturn(mockResponse);

        // Act
        AppianEventResponseDTO response = appianEventService.triggerEvent(request);

        // Assert
        assertEquals("OK", response.getStatus());
        verify(appianRestClient).triggerEvent(eq("CASE-001"), eq("create"), any());
    }
}
