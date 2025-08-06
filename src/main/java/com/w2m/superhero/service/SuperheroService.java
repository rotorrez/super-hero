package com.tu.paquete.security;

import com.tu.paquete.client.TokenRestClient;
import com.tu.paquete.dto.IdAttributesDTO;
import com.tu.paquete.dto.RequestTokenDTO;
import com.tu.paquete.dto.ResponseTokenDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@ApplicationScoped
public class TokenProvider {

    private static final Logger Log = Logger.getLogger(TokenProvider.class);

    @ConfigProperty(name = "bamoeepp.tokenprovider.uid")
    String uid;

    @ConfigProperty(name = "bamoeepp.tokenprovider.password")
    String password;

    @ConfigProperty(name = "bamoeepp.tokenprovider.realm")
    String realm;

    private final AtomicReference<ResponseTokenDTO> cachedTokenRef = new AtomicReference<>();

    @Inject
    @RestClient
    TokenRestClient tokenRestClient;

    public synchronized String getBearerToken() {
        ResponseTokenDTO token = getToken();
        return "Bearer " + token.getJwt();
    }

    private ResponseTokenDTO getToken() {
        ResponseTokenDTO cachedToken = cachedTokenRef.get();

        if (cachedToken == null || isTokenExpired(cachedToken.getJwt())) {
            Log.debug("Token is null or expired. Requesting new token...");

            RequestTokenDTO request = buildRequest();
            try {
                ResponseTokenDTO newToken = tokenRestClient.getToken(request);
                cachedTokenRef.set(newToken);
                Log.info("New token obtained and cached.");
                return newToken;
            } catch (WebApplicationException e) {
                int status = e.getResponse().getStatus();
                String errorBody = e.getResponse().readEntity(String.class);
                Log.errorf("Token service error. HTTP %d - %s", status, errorBody);
                throw new RuntimeException("Failed to obtain token. HTTP " + status + " - " + errorBody, e);
            } catch (Exception e) {
                Log.error("Unexpected error during token retrieval", e);
                throw new RuntimeException("Unexpected error while getting token", e);
            }
        }

        Log.debug("Using cached token.");
        return cachedToken;
    }

    private boolean isTokenExpired(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) return true;

            String payload = new String(Base64.getDecoder().decode(parts[1]));
            long exp = new com.fasterxml.jackson.databind.ObjectMapper()
                    .readTree(payload).get("exp").asLong();

            return Instant.now().getEpochSecond() > exp;
        } catch (Exception e) {
            Log.error("Failed to parse JWT expiration", e);
            return true;
        }
    }

    private RequestTokenDTO buildRequest() {
        IdAttributesDTO idAttributes = new IdAttributesDTO();
        idAttributes.setUid(uid);

        RequestTokenDTO request = new RequestTokenDTO();
        request.setCredentialType(List.of("JWT"));
        request.setIdAttributes(idAttributes);
        request.setPassword(password);
        request.setRealm(realm);

        return request;
    }
}
