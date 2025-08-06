import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;

@ExtendWith(MockitoExtension.class)
class CoexistenceServiceTest {

    @Mock
    CoexistenceRestClient client;

    @Mock
    TokenProvider tokenProvider;

    @InjectMocks
    CoexistenceService service;

    @Mock
    UserTaskStateEvent event;

    @BeforeEach
    void setup() {
        // Simular la instancia de la tarea
        UserTaskInstance instance = mock(UserTaskInstance.class);
        when(instance.getId()).thenReturn("task-123");
        when(instance.getTaskName()).thenReturn("MyTask");
        when(instance.getInputs()).thenReturn(new HashMap<>() {{
            put("caseId", "case-456");
        }});

        when(event.getUserTaskInstance()).thenReturn(instance);
        when(tokenProvider.getBearerToken()).thenReturn("mock-token");
    }

    @Test
    void taskCoexistence_shouldCallSet_whenCreatedToReady() {
        when(event.getOldStatus().getName()).thenReturn("Created");
        when(event.getNewStatus().getName()).thenReturn("Ready");
        when(client.performPostCoexistence(any(), any(), any())).thenReturn(Response.ok().build());

        service.taskCoexistence(event);

        verify(client).performPostCoexistence(any(), eq("mock-token"), eq("PRCOS2"));
    }

    @Test
    void taskCoexistence_shouldCallDelete_whenTransitionToCompleted() {
        when(event.getOldStatus().getName()).thenReturn("Ready");
        when(event.getNewStatus().getName()).thenReturn("Completed");
        when(client.performDeleteCoexistence(any(), any(), any())).thenReturn(Response.ok().build());

        service.taskCoexistence(event);

        verify(client).performDeleteCoexistence(eq("task-123"), eq("mock-token"), eq("PRCOS2"));
    }

    @Test
    void taskCoexistence_shouldThrowException_onPostError() {
        when(event.getOldStatus().getName()).thenReturn("Created");
        when(event.getNewStatus().getName()).thenReturn("Ready");

        Response errorResponse = Response.status(400).entity("Bad Request").build();
        when(client.performPostCoexistence(any(), any(), any()))
                .thenThrow(new WebApplicationException(errorResponse));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.taskCoexistence(event));
        assertTrue(ex.getMessage().contains("Error registering coexistence task"));
    }

    @Test
    void taskCoexistence_shouldThrowException_onDeleteError() {
        when(event.getOldStatus().getName()).thenReturn("Ready");
        when(event.getNewStatus().getName()).thenReturn("Completed");

        Response errorResponse = Response.status(500).entity("Internal Server Error").build();
        when(client.performDeleteCoexistence(any(), any(), any()))
                .thenThrow(new WebApplicationException(errorResponse));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.taskCoexistence(event));
        assertTrue(ex.getMessage().contains("Error deleting coexistence task"));
    }
}
