quarkus:
  rest-client:
    appian-andgo-client:
      url: ${bamoecoexis.appian-andgo.base-url}
      connect-timeout: 5S
      read-timeout: 30S

bamoecoexis:
  appian-andgo:
    base-url: https://sancoreproc.dev.bsch/bpm-appian-tareas-andgo
    client-id: PRCOS2
