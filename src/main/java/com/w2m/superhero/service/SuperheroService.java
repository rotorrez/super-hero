package com.santander.san.audobs.sanaudobsbamoecoexislib.service.andgo;

import com.santander.san.audobs.sanaudobsbamoecoexislib.dto.andgo.StartFastProcessRequestDTO;
import com.santander.san.audobs.sanaudobsbamoecoexislib.dto.andgo.StartFastProcessResponseDTO;
import com.santander.san.audobs.sanaudobsbamoecoexislib.integration.andgo.rest.AppianAndgoRestClient;
import com.santander.san.audobs.sanaudobsbamoecoexislib.security.TokenProvider;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class AppianAndgoCaseService {

    @Inject @RestClient
    AppianAndgoRestClient client;

    @Inject
    TokenProvider tokenProvider;

    @ConfigProperty(name = "bamoecoexis.appian-andgo.client-id")
    String clientId;

    public StartFastProcessResponseDTO startFastProcess(String processCode,
                                                        StartFastProcessRequestDTO request) {
        final String bearer = tokenProvider.getBearerToken(); // ya incluye "Bearer "
        try {
            Log.debugf("[ANDGO] Starting fast-process code=%s, customers=%s",
                    processCode, safeCustomers(request));

            StartFastProcessResponseDTO resp =
                    client.startFastProcess(bearer, clientId, processCode, request);

            int tasks = resp.getTasks() == null ? 0 : resp.getTasks().size();
            Log.debugf("[ANDGO] Response status=%s, tasks=%d", resp.getStatus(), tasks);
            return resp;

        } catch (WebApplicationException wae) {
            String body = readBody(wae);
            int status = wae.getResponse().getStatus();
            Log.errorf("[ANDGO] HTTP %d on startFastProcess(%s). Body: %s", status, processCode, body);
            throw new RuntimeException("ANDGO start fast-process failed: HTTP " + status + " - " + body, wae);
        } catch (Exception ex) {
            Log.errorf(ex, "[ANDGO] Unexpected error on startFastProcess(%s)", processCode);
            throw new RuntimeException("ANDGO start fast-process unexpected error", ex);
        }
    }

    private String safeCustomers(StartFastProcessRequestDTO req) {
        try { return String.valueOf(req.getCustomersIdentification()); }
        catch (Exception ignore) { return "<n/a>"; }
    }

    private String readBody(WebApplicationException wae) {
        try { return wae.getResponse().readEntity(String.class); }
        catch (Exception ignore) { return "<no-body>"; }
    }
}
