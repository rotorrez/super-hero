@ApplicationScoped
public class AppianEventService {

    @Inject
    AppianRestClient appianRestClient;

    @Inject
    AccessPointAdapter accessPointAdapter;

    @Inject
    CoexistenceService coexistenceService;

    @ConfigProperty(name = "bamoecoexis.appian-api.client-id")
    String clientId;

    @ConfigProperty(name = "bamoecoexis.appian-api.channel", defaultValue = "INT")
    String channel;

    public AppianEventResponseDTO triggerEvent(AppianEventRequestDTO request) {
        Log.debugf("Starting event management for idCaso=%d, event=%s",
            request.getIdCaso(), request.getEvent());

        String caseNumber = getCaseNumberByIdCaso(request.getIdCaso());
        Log.debugf("Resolved caseNumber for idCaso=%d → %s", request.getIdCaso(), caseNumber);

        AppianContactPointDTO contactPoint =
            accessPointAdapter.getContactPointById(request.getIdAccessPoint());
        Log.debugf("Resolved contactPoint for idAccessPoint=%d → %s",
            request.getIdAccessPoint(), contactPoint);

        AppianEventPayloadDTO payload = AppianEventPayloadDTO.builder()
            .processCode(request.getProcessCode())
            .customersIdentification(request.getCustomersIdentification())
            .contactPoint(contactPoint)
            .specificInformation(request.getSpecificInformation())
            .build();

        String sessionId = UUID.randomUUID().toString();
        try {
            AppianEventResponseDTO response = appianRestClient.triggerEvent(
                caseNumber,
                request.getEvent(),
                payload,
                sessionId,
                clientId,
                channel
            );

            Log.infof("Event %s successfully triggered for caseNumber %s with status %s",
                request.getEvent(), caseNumber, response.getStatus());

            return response;

        } catch (WebApplicationException wae) {
            Response response = wae.getResponse();
            String body = response.hasEntity() ? response.readEntity(String.class) : "No body";
            int status = response.getStatus();
            Log.errorf("Failed to trigger event. HTTP %d - %s", status, body);
            throw new RuntimeException("Appian service error: " + body, wae);

        } catch (ProcessingException pe) {
            Log.errorf("Connection error while triggering event: %s", pe.getMessage());
            throw new RuntimeException("Connection error while calling Appian service", pe);

        } catch (Exception e) {
            Log.errorf("Unexpected error while triggering event: %s", e.getMessage());
            throw new RuntimeException("Unexpected error while triggering event", e);
        }
    }

    private String getCaseNumberByIdCaso(Integer idCaso) {
        CaseRelationDTO dto = coexistenceService.getCaseRelationByPpaasCaseId(String.valueOf(idCaso));
        if (dto == null || dto.getFictionalCaseId() == null) {
            throw new NoSuchElementException("Case number not found for idCaso=" + idCaso);
        }
        return dto.getFictionalCaseId();
    }
}






package com.santander.san.audobs.sanaudobsbamoecoexislib.integration.client;

import com.santander.san.audobs.sanaudobsbamoecoexislib.model.dto.AppianEventPayloadDTO;
import com.santander.san.audobs.sanaudobsbamoecoexislib.model.dto.AppianEventResponseDTO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "appian-api")
@Path("/v2/cases")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface AppianRestClient {

    @POST
    @Path("/{caseNumber}/events/{event}")
    AppianEventResponseDTO triggerEvent(
        @PathParam("caseNumber") String caseNumber,
        @PathParam("event") String event,
        AppianEventPayloadDTO payload,
        @HeaderParam("Session-Id") String sessionId,
        @HeaderParam("X-ClientID") String clientId,
        @HeaderParam("X-Santander-Channel") String channel
    );
}
