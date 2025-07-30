format: "%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c{2.}] (%t) %s%e%n"



log:
    level: INFO
    category:
      "org.jbpm.workflow.instance.impl":
        level: DEBUG
      "org.jbpm.workflow.instance.node":
        level: DEBUG
      "org.jbpm.workflow.core":
        level: DEBUG
      "org.jbpm.process.instance":
        level: DEBUG
      "org.kie.kogito.process.impl":
        level: DEBUG
      "org.kie.kogito.process":
        level: DEBUG
      "org.kie.kogito.quarkus":
        level: DEBUG
