import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.santander.san.audobs.sanaudobsbamoecoexislib.client.CoexistenceRestClient;
import com.santander.san.audobs.sanaudobsbamoecoexislib.integration.security.TokenProvider;
import com.santander.san.audobs.sanaudobsbamoecoexislib.service.CoexistenceService;
import org.kie.kogito.usertask.events.UserTaskStateEvent;

import java.util.Map;

class CoexistenceServiceTest {

    @InjectMocks
    CoexistenceService service;

    @Mock
    CoexistenceRestClient client;

    @Mock
    TokenProvider tokenProvider;

    @Mock
    UserTaskStateEvent event;

    @Mock
    org.kie.kogito.usertask.lifecycle.UserTaskState oldStatus;

    @Mock
    org.kie.kogito.usertask.lifecycle.UserTaskState newStatus;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        when(event.getUserTaskInstance()).thenReturn(mock(org.kie.kogito.task.UserTaskInstance.class));
    }

    @Test
    void taskCoexistence_shouldCallSet_whenCreatedToReady() {
        when(event.getOldStatus()).thenReturn(oldStatus);
        when(event.getNewStatus()).thenReturn(newStatus);
        when(oldStatus.getName()).thenReturn("Created");
        when(newStatus.getName()).thenReturn("Ready");

        when(tokenProvider.getBearerToken()).thenReturn("mock-token");
        when(client.performPostCoexistence(any(), any(), any(), any())).thenReturn(Response.ok().build());

        service.taskCoexistence(event);

        verify(client, times(1)).performPostCoexistence(any(), any(), any(), any());
    }

    @Test
    void taskCoexistence_shouldCallDelete_whenTransitionToCompleted() {
        when(event.getOldStatus()).thenReturn(oldStatus);
        when(event.getNewStatus()).thenReturn(newStatus);
        when(oldStatus.getName()).thenReturn("Ready");
        when(newStatus.getName()).thenReturn("Completed");

        when(tokenProvider.getBearerToken()).thenReturn("mock-token");
        when(client.performDeleteCoexistence(any(), any(), any(), any())).thenReturn(Response.ok().build());

        service.taskCoexistence(event);

        verify(client, times(1)).performDeleteCoexistence(any(), any(), any(), any());
    }

    @Test
    void taskCoexistence_shouldThrowException_onPostError() {
        when(event.getOldStatus()).thenReturn(oldStatus);
        when(event.getNewStatus()).thenReturn(newStatus);
        when(oldStatus.getName()).thenReturn("Created");
        when(newStatus.getName()).thenReturn("Ready");

        WebApplicationException mockException = mock(WebApplicationException.class);
        Response mockResponse = mock(Response.class);

        when(mockResponse.readEntity(String.class)).thenReturn("Bad Request");
        when(mockException.getResponse()).thenReturn(mockResponse);
        when(mockException.getMessage()).thenReturn("wrapped message");

        when(tokenProvider.getBearerToken()).thenReturn("mock-token");
        when(client.performPostCoexistence(any(), any(), any(), any())).thenThrow(mockException);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.taskCoexistence(event));
        assertTrue(ex.getMessage().contains("Error registering coexistence task"));
    }

    @Test
    void taskCoexistence_shouldThrowException_onDeleteError() {
        when(event.getOldStatus()).thenReturn(oldStatus);
        when(event.getNewStatus()).thenReturn(newStatus);
        when(oldStatus.getName()).thenReturn("Ready");
        when(newStatus.getName()).thenReturn("Completed");

        WebApplicationException mockException = mock(WebApplicationException.class);
        Response mockResponse = mock(Response.class);

        when(mockResponse.readEntity(String.class)).thenReturn("Delete Failed");
        when(mockException.getResponse()).thenReturn(mockResponse);
        when(mockException.getMessage()).thenReturn("wrapped delete message");

        when(tokenProvider.getBearerToken()).thenReturn("mock-token");
        when(client.performDeleteCoexistence(any(), any(), any(), any())).thenThrow(mockException);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.taskCoexistence(event));
        assertTrue(ex.getMessage().contains("Error deleting coexistence task"));
    }
}
