@ExtendWith(MockitoExtension.class)
class CoexistenceServiceTest {

    @InjectMocks
    CoexistenceService service;

    @Mock
    TokenProvider tokenProvider;

    @Mock
    CoexistenceRestClient client;

    @Mock
    UserTaskStateEvent event;

    @Mock
    UserTaskStatus oldStatus;

    @Mock
    UserTaskStatus newStatus;

    @BeforeEach
    void setUp() {
        when(event.getOldStatus()).thenReturn(oldStatus);
        when(event.getNewStatus()).thenReturn(newStatus);

        when(oldStatus.getName()).thenReturn("Created");
        when(newStatus.getName()).thenReturn("Ready");

        var userTask = mock(UserTask.class);
        when(event.getUserTaskInstance()).thenReturn(userTask);

        when(userTask.getId()).thenReturn("task-123");
        when(userTask.getTaskName()).thenReturn("MyTask");
        when(userTask.getInputs()).thenReturn(Map.of("caseId", "case-456"));

        when(tokenProvider.getBearerToken()).thenReturn("mock-token");
    }

    @Test
    void taskCoexistence_shouldCallSet_whenCreatedToReady() {
        when(client.performPostCoexistence(any(), any(), any()))
            .thenReturn(Response.ok().build());

        service.taskCoexistence(event);

        verify(client, times(1)).performPostCoexistence(any(), any(), any());
    }

    @Test
    void taskCoexistence_shouldCallDelete_whenStatusIsCompleted() {
        when(oldStatus.getName()).thenReturn("InProgress");
        when(newStatus.getName()).thenReturn("Completed");
        when(client.performDeleteCoexistence(any(), any(), any()))
            .thenReturn(Response.ok().build());

        service.taskCoexistence(event);

        verify(client, times(1)).performDeleteCoexistence(any(), any(), any());
    }

    @Test
    void setTaskCoexistence_shouldHandleWebApplicationException() {
        var userTask = event.getUserTaskInstance();

        when(client.performPostCoexistence(any(), any(), any()))
            .thenThrow(buildWebAppException(400, "Some error"));

        assertThrows(RuntimeException.class, () -> service.setTaskCoexistence(event));
    }

    @Test
    void deleteTaskCoexistence_shouldHandleGenericException() {
        var userTask = event.getUserTaskInstance();

        when(client.performDeleteCoexistence(any(), any(), any()))
            .thenThrow(new RuntimeException("Unexpected error"));

        assertThrows(RuntimeException.class, () -> service.deleteTaskCoexistence(event));
    }

    @Test
    void getCaseRelationByPpaasCaseId_shouldReturnDTO() {
        CaseRelationDTO dto = new CaseRelationDTO();
        when(client.performGetCoexistence("ppaas-001", "mock-token", "PRCOS2"))
            .thenReturn(dto);

        var result = service.getCaseRelationByPpaasCaseId("ppaas-001");

        assertEquals(dto, result);
    }

    @Test
    void getCaseRelationByPpaasCaseId_shouldHandleWebApplicationException() {
        when(client.performGetCoexistence(any(), any(), any()))
            .thenThrow(buildWebAppException(404, "Not found"));

        assertThrows(RuntimeException.class, () -> service.getCaseRelationByPpaasCaseId("ppaas-001"));
    }

    // Utilidad para mockear WebApplicationException con body
    private WebApplicationException buildWebAppException(int status, String message) {
        var response = Response.status(status).entity(message).type(MediaType.TEXT_PLAIN).build();
        return new WebApplicationException(response);
    }
}
