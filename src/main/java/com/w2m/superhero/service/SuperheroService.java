@QuarkusTest
class CoexistenceServiceTest {

    @InjectMock
    CoexistenceRestClient client;

    @InjectMock
    TokenProvider tokenProvider;

    @Inject
    CoexistenceService service;

    @Mock
    UserTaskStateEvent event;

    @BeforeEach
    void setup() {
        // Mock UserTaskInstance
        UserTaskInstance instance = mock(UserTaskInstance.class);
        when(instance.getId()).thenReturn("task-123");
        when(instance.getTaskName()).thenReturn("MyTask");
        when(instance.getInputs()).thenReturn(new HashMap<>() {{
            put("caseId", "case-456");
        }});

        // Mock Event
        when(event.getUserTaskInstance()).thenReturn(instance);
        when(tokenProvider.getBearerToken()).thenReturn("mock-token");
    }

    @Test
    void taskCoexistence_shouldCallSet_whenCreatedToReady() {
        when(event.getOldStatus().getName()).thenReturn("Created");
        when(event.getNewStatus().getName()).thenReturn("Ready");
        when(client.performPostCoexistence(any(), any(), any()))
                .thenReturn(Response.ok().build());

        service.taskCoexistence(event);

        verify(client).performPostCoexistence(any(), eq("mock-token"), eq("PRCOS2"));
    }

    @Test
    void taskCoexistence_shouldCallDelete_whenTransitionToCompleted() {
        when(event.getOldStatus().getName()).thenReturn("Ready");
        when(event.getNewStatus().getName()).thenReturn("Completed");
        when(client.performDeleteCoexistence(any(), any(), any()))
                .thenReturn(Response.ok().build());

        service.taskCoexistence(event);

        verify(client).performDeleteCoexistence(eq("task-123"), eq("mock-token"), eq("PRCOS2"));
    }

    @Test
    void setTaskCoexistence_shouldThrowRuntimeException_whenWebApplicationException() {
        when(event.getOldStatus().getName()).thenReturn("Created");
        when(event.getNewStatus().getName()).thenReturn("Ready");

        Response response = Response.status(400).entity("Bad Request").build();
        when(client.performPostCoexistence(any(), any(), any()))
                .thenThrow(new WebApplicationException(response));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.taskCoexistence(event));
        assertTrue(ex.getMessage().contains("Error registering coexistence task"));
    }

    @Test
    void deleteTaskCoexistence_shouldThrowRuntimeException_whenWebApplicationException() {
        when(event.getOldStatus().getName()).thenReturn("Ready");
        when(event.getNewStatus().getName()).thenReturn("Completed");

        Response response = Response.status(404).entity("Not Found").build();
        when(client.performDeleteCoexistence(any(), any(), any()))
                .thenThrow(new WebApplicationException(response));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.taskCoexistence(event));
        assertTrue(ex.getMessage().contains("Error deleting coexistence task"));
    }
}
