package com.santander.san.audobs.sanaudobsbamoeeepplib.integration.service;

import com.santander.san.audobs.sanaudobsbamoeeepplib.integration.model.event.EventDTO;
import com.santander.san.audobs.sanaudobsbamoeeepplib.integration.utils.EventMapperUtils;
import com.santander.sgt.apm1953.sgtapm1953ppectrl.service.impl.EngineControllerServiceImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@ApplicationScoped
@Slf4j
public class EventService {

    private static final Integer DEFAULT_STATUS_ID = 8; // CTFO_E_08 (puedes sustituir por constante si la tienes)

    @Inject
    EngineControllerServiceImpl engineControllerService;

    /**
     * Transforma un evento recibido como mapa en el DTO correspondiente y
     * ejecuta el cambio de estado del caso asociado.
     *
     * @param eventMap mapa de información del evento (action, data, accessPointId, etc)
     */
    public void updateStatusFromEvent(Map<String, Object> eventMap) {
        log.debug("Received event map: {}", eventMap);

        try {
            EventDTO dto = EventMapperUtils.mapEventDTO(eventMap);
            log.debug("Mapped EventDTO: {}", dto);

            Map<String, Object> data = dto.getData();

            String caseId = getRequiredString(data, "caseId");
            String processId = getRequiredString(data, "processId");
            String userId = getRequiredString(data, "userId");

            Integer stageId = null;
            if (data.get("stageId") != null) {
                try {
                    stageId = Integer.parseInt(data.get("stageId").toString());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid stageId format: " + data.get("stageId"), e);
                }
            }

            log.info("Calling changeStatusAndStage with caseId={}, processId={}, statusId={}, stageId={}, userId={}",
                    caseId, processId, DEFAULT_STATUS_ID, stageId, userId);

            engineControllerService.changeStatusAndStage(caseId, processId, DEFAULT_STATUS_ID, stageId, userId);

            log.info("Successfully updated status for caseId={} in processId={}", caseId, processId);

        } catch (IllegalArgumentException e) {
            log.error("Validation error while processing event: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while processing event: {}", e.getMessage(), e);
            throw new RuntimeException("Error while processing event", e);
        }
    }

    /**
     * Extrae y valida un campo obligatorio de tipo String desde el mapa de datos.
     */
    private String getRequiredString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null || value.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("Missing required field in event data: " + key);
        }
        return value.toString();
    }
}

