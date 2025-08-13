# Dentro del JAR de la librería
quarkus.rest-client.appian-case-and-go-client.url=${bamoecoexis.appian-case-and-go.base-url:https://default.invalid}



@RegisterRestClient(
    configKey = "appian-case-and-go-client",
    baseUri   = "https://default.invalid" // Fallback si no hay config
)


 @ConfigProperty(name = "bamoecoexis.appian-case-and-go.client-id")
    Optional<String> clientId;
