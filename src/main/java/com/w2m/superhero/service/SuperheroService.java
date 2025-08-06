catch (WebApplicationException e) {
    int status = e.getResponse().getStatus();
    String errorBody = e.getResponse().readEntity(String.class);
    Log.errorf("Failed to retrieve configuration for process='%s', config='%s'. HTTP %d - %s",
            processId, configurationId, status, errorBody);
    throw new RuntimeException("Failed to retrieve configuration from remote service", e);
}

catch (Exception e) {
    Log.errorf("Unexpected error while retrieving configuration for process='%s', config='%s': %s",
            processId, configurationId, e.getMessage());
    throw new RuntimeException("Unexpected error while retrieving configuration", e);
}
