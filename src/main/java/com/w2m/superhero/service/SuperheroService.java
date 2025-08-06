package com.tuempresa.tuproyecto.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.tuempresa.tuproyecto.dto.CoexistenceTaskDTO;
import com.tuempresa.tuproyecto.dto.CaseRelationDTO;

import java.util.Map;

@RegisterRestClient(configKey = "coexistence-api-client")
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CoexistenceRestClient {

    @POST
    @Path("task")
    Response performPostCoexistence(
        CoexistenceTaskDTO dto,
        @HeaderParam("Authorization") String bearerToken,
        @HeaderParam("X-ClientId") String clientId
    );

    @DELETE
    @Path("tasks/{taskId}")
    Response performDeleteCoexistence(
        @PathParam("taskId") String taskId,
        @HeaderParam("Authorization") String bearerToken,
        @HeaderParam("X-ClientId") String clientId
    );

    @GET
    @Path("caseRelations/ppaasCase/{caseId}")
    CaseRelationDTO performGetCoexistence(
        @PathParam("caseId") String caseId,
        @HeaderParam("Authorization") String bearerToken,
        @HeaderParam("X-ClientId") String clientId
    );
}
