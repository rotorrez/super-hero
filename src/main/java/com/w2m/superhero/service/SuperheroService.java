package com.santander.san.audobs.sanaudobsbamoecoexislib.service;

import com.santander.san.audobs.sanaudobsbamoecoexislib.dto.CoexistenceTaskDTO;
import com.santander.san.audobs.sanaudobsbamoecoexislib.dto.CaseRelationDTO;
import com.santander.san.audobs.sanaudobsbamoecoexislib.integration.client.CoexistenceRestClient;
import com.santander.san.audobs.sanaudobsbamoecoexislib.integration.security.TokenProvider;
import com.santander.san.audobs.sanaudobsbamoecoexislib.model.UserTaskStateEvent;
import com.santander.san.audobs.sanaudobsbamoecoexislib.model.enums.UserTaskStatusEnum;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class CoexistenceServiceTest {

    @Mock
    TokenProvider tokenProvider;

    @Mock
    CoexistenceRestClient client;

    @InjectMocks
    CoexistenceService service;

    UserTaskStateEvent event;

    @BeforeEach
    void init() {
        event = mock(UserTaskStateEvent.class);
        var userTaskInstance = mock(UserTaskStateEvent.UserTaskInstance.class);

        when(event.getUserTaskInstance()).thenReturn(userTaskInstance);
        when(userTaskInstance.getId()).thenReturn("task-123");
        when(userTaskInstance.getTaskName()).thenReturn("MyTask");
        when(userTaskInstance.getInputs()).thenReturn(new HashMap<>() {{
            put("caseId", "case-456");
        }});
        when(tokenProvider.getBearerToken()).thenReturn("mock-token");
    }

    @Test
    void taskCoexistence_shouldCallSet_whenCreatedToReady() {
        when(event.getOldStatus()).thenReturn(UserTaskStatusEnum.Created);
        when(event.getNewStatus()).thenReturn(UserTaskStatusEnum.Ready);
        when(client.performPostCoexistence(any(CoexistenceTaskDTO.class), eq("mock-token"), anyString()))
                .thenReturn(Response.ok().build());

        service.taskCoexistence(event);

        verify(client, times(1)).performPostCoexistence(any(), any(), any());
    }

    @Test
    void taskCoexistence_shouldCallDelete_whenCompleted() {
        when(event.getOldStatus()).thenReturn(UserTaskStatusEnum.Ready);
        when(event.getNewStatus()).thenReturn(UserTaskStatusEnum.Completed);
        when(client.performDeleteCoexistence(eq("task-123"), eq("mock-token"), anyString()))
                .thenReturn(Response.ok().build());

        service.taskCoexistence(event);

        verify(client, times(1)).performDeleteCoexistence(any(), any(), any());
    }

    @Test
    void taskCoexistence_shouldNotCall_whenUnrelatedTransition() {
        when(event.getOldStatus()).thenReturn(UserTaskStatusEnum.Created);
        when(event.getNewStatus()).thenReturn(UserTaskStatusEnum.InProgress);

        service.taskCoexistence(event);

        verifyNoInteractions(client);
    }

    @Test
    void setTaskCoexistence_shouldHandleWebAppException() {
        when(client.performPostCoexistence(any(), any(), any())).thenThrow(mockWebAppException());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.setTaskCoexistence(event));
        assertTrue(ex.getMessage().contains("Error registering coexistence task"));
    }

    @Test
    void deleteTaskCoexistence_shouldHandleWebAppException() {
        when(client.performDeleteCoexistence(any(), any(), any())).thenThrow(mockWebAppException());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deleteTaskCoexistence(event));
        assertTrue(ex.getMessage().contains("Error deleting coexistence task"));
    }

    @Test
    void getCaseRelationByPpaasCaseId_shouldReturnResponse() {
        CaseRelationDTO dto = new CaseRelationDTO();
        when(client.performGetCoexistence(eq("ppaas-001"), eq("mock-token"), anyString())).thenReturn(dto);

        CaseRelationDTO result = service.getCaseRelationByPpaasCaseId("ppaas-001");
        assertEquals(dto, result);
    }

    @Test
    void getCaseRelationByPpaasCaseId_shouldHandleWebAppException() {
        when(client.performGetCoexistence(any(), any(), any())).thenThrow(mockWebAppException());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getCaseRelationByPpaasCaseId("any"));
        assertTrue(ex.getMessage().contains("Error fetching case relation"));
    }

    private WebApplicationException mockWebAppException() {
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(400);
        when(response.readEntity(String.class)).thenReturn("Bad Request");
        WebApplicationException ex = new WebApplicationException(response);
        return ex;
    }
}
