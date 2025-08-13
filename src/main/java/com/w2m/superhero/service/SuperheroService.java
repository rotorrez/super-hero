import org.jboss.resteasy.annotations.jaxrs.PATCH;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface AppianCaseAndGoRestClient {

    @PATCH
    @Path("/{dynamicPart}/start-fast-process")
    StartFastProcessResponseDTO startFastProcess(
        @HeaderParam("Authorization") String bearer,
        @HeaderParam("X-ClientId") String clientId,
        StartFastProcessRequestDTO body,
        @PathParam("dynamicPart") String dynamicPath
    );
}
