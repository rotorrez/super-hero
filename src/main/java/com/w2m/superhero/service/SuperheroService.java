package com.santander.san.audobs.sanaudobsbamoecoexislib.integration.andgo.rest;

import com.santander.san.audobs.sanaudobsbamoecoexislib.dto.andgo.StartFastProcessRequestDTO;
import com.santander.san.audobs.sanaudobsbamoecoexislib.dto.andgo.StartFastProcessResponseDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "appian-andgo-client")
@Path("/v1/fast-processes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface AppianAndgoRestClient {

    @POST
    @Path("/{processCode}/cases")
    StartFastProcessResponseDTO startFastProcess(
        @HeaderParam("Authorization") String authorization, // "Bearer xxx"
        @HeaderParam("x-ClientId") String clientId,         // exacta grafía del header
        @PathParam("processCode") String processCode,
        StartFastProcessRequestDTO request
    );
}
