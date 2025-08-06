package com.tu.paquete;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.tu.paquete.dto.*;
import com.tu.paquete.client.CoexistenceRestClient;
import com.tu.paquete.security.TokenProvider;

@ApplicationScoped
public class CoexistenceService {

    @Inject
    TokenProvider tokenProvider;

    @Inject
    CoexistenceRestClient client;

    @ConfigProperty(name = "bamoecoexis.coexistence.client-id", defaultValue = "PRCOS2")
    String clientId;

    public void taskCoexistence(UserTaskStateEvent event) {
        String oldStatusName = event.getOldStatus().getName();
        String newStatusName = event.getNewStatus().getName();

        UserTaskStatusEnum oldStatus = UserTaskStatusEnum.fromString(oldStatusName);
        UserTaskStatusEnum newStatus = UserTaskStatusEnum.fromString(newStatusName);

        String taskId = event.getUserTaskInstance().getId();
        String taskName = event.getUserTaskInstance().getTaskName();

        Log.infof("Evaluating task state change: TaskName=%s, TaskId=%s, Transition=%s -> %s", taskName, taskId, oldStatusName, newStatusName);

        if (oldStatus == UserTaskStatusEnum.Created && newStatus == UserTaskStatusEnum.Ready) {
            Log.infof("Triggering coexistence set for TaskId=%s", taskId);
            setTaskCoexistence(event);
        } else if (newStatus == UserTaskStatusEnum.Completed) {
            Log.infof("Triggering coexistence delete for TaskId=%s", taskId);
            deleteTaskCoexistence(event);
        } else {
            Log.debugf("No action taken for state change: %s -> %s", oldStatusName, newStatusName);
        }
    }

    public void setTaskCoexistence(UserTaskStateEvent event) {
        try {
            String taskName = event.getUserTaskInstance().getTaskName();
            String taskId = event.getUserTaskInstance().getId();
            String caseId = (String) event.getUserTaskInstance().getInputs().get("caseId");

            Log.infof("Registering coexistence task: CaseId=%s, TaskName=%s, TaskId=%s", caseId, taskName, taskId);

            CoexistenceTaskDTO dto = new CoexistenceTaskDTO();
            dto.setCaseId(caseId);
            dto.setTaskName(taskName);
            dto.setTaskId(taskId);

            String token = tokenProvider.getBearerToken();
            var response = client.performPostCoexistence(dto, token, clientId);

            Log.infof("Task registered successfully in coexistence system. TaskId=%s, Status=%s", taskId, response.getStatus());

        } catch (WebApplicationException ex) {
            String body = ex.getResponse().readEntity(String.class);
            Log.errorf("Error from coexistence system (POST). Status=%s, Body=%s", ex.getResponse().getStatus(), body);
            throw new RuntimeException("Error registering coexistence task: " + body, ex);
        } catch (Exception ex) {
            Log.error("Unexpected error in setTaskCoexistence", ex);
            throw new RuntimeException("Unexpected error registering coexistence task", ex);
        }
    }

    public void deleteTaskCoexistence(UserTaskStateEvent event) {
        try {
            String taskId = event.getUserTaskInstance().getId();
            String token = tokenProvider.getBearerToken();

            Log.infof("Deleting coexistence task: TaskId=%s", taskId);

            var response = client.performDeleteCoexistence(taskId, token, clientId);

            Log.infof("Task deleted from coexistence system. TaskId=%s, Status=%s", taskId, response.getStatus());

        } catch (WebApplicationException ex) {
            String body = ex.getResponse().readEntity(String.class);
            Log.errorf("Error from coexistence system (DELETE). Status=%s, Body=%s", ex.getResponse().getStatus(), body);
            throw new RuntimeException("Error deleting coexistence task: " + body, ex);
        } catch (Exception ex) {
            Log.error("Unexpected error in deleteTaskCoexistence", ex);
            throw new RuntimeException("Unexpected error deleting coexistence task", ex);
        }
    }

    public CaseRelationDTO getCaseRelationByPpaasCaseId(String ppaasCaseId) {
        try {
            String token = tokenProvider.getBearerToken();

            Log.infof("Fetching case relation for PPAAS case ID: %s", ppaasCaseId);

            CaseRelationDTO response = client.performGetCoexistence(ppaasCaseId, token, clientId);

            Log.infof("Received case relation for PPAAS case ID: %s -> %s", ppaasCaseId, response);

            return response;

        } catch (WebApplicationException ex) {
            String body = ex.getResponse().readEntity(String.class);
            Log.errorf("Error from coexistence system (GET). Status=%s, Body=%s", ex.getResponse().getStatus(), body);
            throw new RuntimeException("Error fetching case relation: " + body, ex);
        } catch (Exception ex) {
            Log.error("Unexpected error in getCaseRelationByPpaasCaseId", ex);
            throw new RuntimeException("Unexpected error fetching case relation", ex);
        }
    }
}
