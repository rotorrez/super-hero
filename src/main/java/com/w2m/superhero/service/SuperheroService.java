public void updateStatusFromEvent(Map<String, Object> eventMap) {
    EventDTO dto = eventMapper.parse(eventMap);
    validateRequiredFields(dto);
    changeStatusAndStage(
        dto.getCaseId(),
        dto.getProcessId(),
        CTFO_E_08,
        dto.getStageId(),
        dto.getUserId()
    );
}
