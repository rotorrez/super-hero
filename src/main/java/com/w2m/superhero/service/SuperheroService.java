
package com.santander.san.audobs.sanaudobsbamoeeepplib.integration.service;

import com.santander.san.audobs.sanaudobsbamoeeepplib.integration.model.event.EventDTO;
import com.santander.san.audobs.sanaudobsbamoeeepplib.integration.utils.EventMapperUtils;
import com.santander.sgt.apm1953.sgtapm1953ppectrl.service.impl.EngineControllerServiceImpl;
import io.quarkus.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EventServiceTest {

    @Mock
    EngineControllerServiceImpl engineControllerService;

    @InjectMocks
    EventService eventService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldInvokeChangeStatusOrStage_whenInputIsValid() {
        Map<String, Object> data = new HashMap<>();
        data.put("caseId", "CASE123");
        data.put("processId", "PROC456");
        data.put("userId", "user789");
        data.put("centerId", "center001");
        data.put("stageId", "10");

        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("action", "SOME_ACTION");
        eventMap.put("data", data);
        eventMap.put("accessPointId", "access-001");

        EventDTO dto = EventDTO.builder()
                .action("SOME_ACTION")
                .data(data)
                .accessPointId("access-001")
                .build();

        mockStatic(EventMapperUtils.class).when(() -> EventMapperUtils.mapEventDTO(eventMap)).thenReturn(dto);

        eventService.updateStatusFromEvent(eventMap);

        verify(engineControllerService).changeStatusOrStage("CASE123", "PROC456", 8, 10, "user789", false, "center001", "access-001");
    }

    @Test
    void shouldThrowException_whenMissingRequiredField() {
        Map<String, Object> data = new HashMap<>();
        data.put("processId", "PROC456"); // caseId missing
        data.put("userId", "user789");
        data.put("centerId", "center001");

        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("action", "SOME_ACTION");
        eventMap.put("data", data);
        eventMap.put("accessPointId", "access-001");

        EventDTO dto = EventDTO.builder()
                .action("SOME_ACTION")
                .data(data)
                .accessPointId("access-001")
                .build();

        mockStatic(EventMapperUtils.class).when(() -> EventMapperUtils.mapEventDTO(eventMap)).thenReturn(dto);

        assertThrows(IllegalArgumentException.class, () -> eventService.updateStatusFromEvent(eventMap));
    }

    @Test
    void shouldThrowException_whenStageIdIsInvalid() {
        Map<String, Object> data = new HashMap<>();
        data.put("caseId", "CASE123");
        data.put("processId", "PROC456");
        data.put("userId", "user789");
        data.put("centerId", "center001");
        data.put("stageId", "invalid");

        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("action", "SOME_ACTION");
        eventMap.put("data", data);
        eventMap.put("accessPointId", "access-001");

        EventDTO dto = EventDTO.builder()
                .action("SOME_ACTION")
                .data(data)
                .accessPointId("access-001")
                .build();

        mockStatic(EventMapperUtils.class).when(() -> EventMapperUtils.mapEventDTO(eventMap)).thenReturn(dto);

        assertThrows(IllegalArgumentException.class, () -> eventService.updateStatusFromEvent(eventMap));
    }

    @Test
    void shouldHandleUnexpectedExceptions() {
        Map<String, Object> data = new HashMap<>();
        data.put("caseId", "CASE123");
        data.put("processId", "PROC456");
        data.put("userId", "user789");
        data.put("centerId", "center001");
        data.put("stageId", "10");

        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("action", "SOME_ACTION");
        eventMap.put("data", data);
        eventMap.put("accessPointId", "access-001");

        EventDTO dto = EventDTO.builder()
                .action("SOME_ACTION")
                .data(data)
                .accessPointId("access-001")
                .build();

        mockStatic(EventMapperUtils.class).when(() -> EventMapperUtils.mapEventDTO(eventMap)).thenReturn(dto);
        doThrow(new RuntimeException("DB failure")).when(engineControllerService).changeStatusOrStage(
                any(), any(), any(), any(), any(), anyBoolean(), any(), any()
        );

        assertThrows(RuntimeException.class, () -> eventService.updateStatusFromEvent(eventMap));
    }
}
