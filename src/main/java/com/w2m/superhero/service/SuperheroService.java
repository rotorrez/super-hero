# Dentro del JAR de la librería
quarkus.rest-client.appian-case-and-go-client.url=${bamoecoexis.appian-case-and-go.base-url:https://default.invalid}



@RegisterRestClient(
    configKey = "appian-case-and-go-client",
    baseUri   = "https://default.invalid" // Fallback si no hay config
)


 @ConfigProperty(name = "bamoecoexis.appian-case-and-go.client-id")
    Optional<String> clientId;

        String resolvedClientId = clientId.orElseThrow(() -> {
            Log.error("Client ID is not configured. Set 'bamoecoexis.appian-case-and-go.client-id' in the consumer.");
            return new RuntimeException("Client ID is not configured");
        });
