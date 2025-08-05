@ApplicationScoped
public class ProcessConfigurationService {

    @Inject
    TokenProvider tokenProvider;

    @Inject
    @RestClient
    ProcessConfigurationRestClient restClient;

    @ConfigProperty(name = "bamoeepp.processconfig.client-id")
    String clientId;

    public ProcessConfigurationDTO getConfiguration(String processId, String configurationId) {
        validateInputs(processId, configurationId);

        Log.infof("Requesting configuration for processId='%s' and configurationId='%s'", processId, configurationId);

        String bearerToken = tokenProvider.getBearerToken();

        try {
            ProcessConfigurationDTO config = restClient.callGetConfiguration(
                processId,
                configurationId,
                "Bearer " + bearerToken,
                clientId
            );

            Log.infof("Successfully retrieved configurationId='%s' for process='%s'",
                    config.getConfigurationId(), processId);

            return config;

        } catch (WebApplicationException e) {
            int status = e.getResponse().getStatus();
            String errorBody = e.getResponse().readEntity(String.class);
            Log.errorf("Failed to retrieve configuration for process='%s', config='%s'. HTTP %d - %s",
                    processId, configurationId, status, errorBody);
            throw e;

        } catch (Exception e) {
            Log.errorf("Unexpected error while retrieving configuration for process='%s', config='%s': %s",
                    processId, configurationId, e.getMessage());
            throw e;
        }
    }

    private void validateInputs(String processId, String configurationId) {
        if (processId == null || processId.isBlank() || configurationId == null || configurationId.isBlank()) {
            throw new IllegalArgumentException("ProcessId and configurationId must be provided");
        }
    }
}




import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import es.san.audobs.bamoecoexislib.dto.ProcessConfigurationDTO;

@RegisterRestClient(configKey = "process-configuration-client")
@Path("/process_configurations")
public interface ProcessConfigurationRestClient {

    @GET
    @Path("/{processId}/configuration/{configurationId}")
    @Produces(MediaType.APPLICATION_JSON)
    ProcessConfigurationDTO callGetConfiguration(
        @PathParam("processId") String processId,
        @PathParam("configurationId") String configurationId,
        @HeaderParam("Authorization") String authorization,
        @HeaderParam("X-ClientId") String clientId
    );
}

quarkus.rest-client.process-configuration-client.url=${bamoeepp.processconfig.base-url}

