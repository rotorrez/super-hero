package com.santander.san.audobs.sanaudobsbamoeeepplib.integration.service;

import com.santander.san.audobs.sanaudobsbamoeeepplib.integration.model.event.EventDTO;
import com.santander.san.audobs.sanaudobsbamoeeepplib.integration.utils.EventMapperUtils;
import com.santander.sgt.apm1953.sgtapm1953ppectrl.service.impl.EngineControllerServiceImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.quarkus.logging.Log;

import java.util.Map;

@ApplicationScoped
public class EventService {

    private static final Integer DEFAULT_STATUS_ID = 8; // CTFO_E_08 si aplica constante
    private static final Boolean DEFAULT_IS_FINAL = false;

    @Inject
    EngineControllerServiceImpl engineControllerService;

    public void updateStatusFromEvent(Map<String, Object> eventMap) {
        Log.debugf("Received event map: %s", eventMap);

        try {
            EventDTO dto = EventMapperUtils.mapEventDTO(eventMap);
            Log.debugf("Mapped EventDTO: %s", dto);

            Map<String, Object> data = dto.getData();

            String caseId = getRequiredString(data, "caseId");
            String processId = getRequiredString(data, "processId");
            String userId = getRequiredString(data, "userId");
            String centerId = getRequiredString(data, "centerId");
            String accessPointId = dto.getAccessPointId();

            Integer stageId = null;
            if (data.get("stageId") != null) {
                try {
                    stageId = Integer.parseInt(data.get("stageId").toString());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid stageId format: " + data.get("stageId"), e);
                }
            }

            Log.infof(
                "Calling changeStatusOrStage with caseId=%s, processId=%s, statusId=%d, stageId=%s, userId=%s, centerId=%s, accessPointId=%s",
                caseId, processId, DEFAULT_STATUS_ID, String.valueOf(stageId), userId, centerId, accessPointId
            );

            engineControllerService.changeStatusOrStage(
                caseId,
                processId,
                DEFAULT_STATUS_ID,
                stageId,
                userId,
                DEFAULT_IS_FINAL,
                centerId,
                accessPointId
            );

            Log.infof("Successfully updated status for caseId=%s in processId=%s", caseId, processId);

        } catch (IllegalArgumentException e) {
            Log.errorf(e, "Validation error while processing event: %s", e.getMessage());
            throw e;
        } catch (Exception e) {
            Log.errorf(e, "Unexpected error while processing event: %s", e.getMessage());
            throw new RuntimeException("Error while processing event", e);
        }
    }

    private String getRequiredString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null || value.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("Missing required field in event data: " + key);
        }
        return value.toString();
    }
}
