import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

// Ajusta estos imports a tus paquetes reales
// import es.tu.paquete.CardProcessServiceImpl;
// import es.tu.paquete.CardRestClient;
// import es.tu.paquete.TokenProvider;
// import es.tu.paquete.CoexistenceService;
// import es.tu.paquete.dto.CaseRelationDTO;

@ExtendWith(MockitoExtension.class)
class CardProcessServiceImplUnitTest {

    @Mock TokenProvider tokenProvider;
    @Mock CoexistenceService coexistenceService;
    @Mock CardRestClient cardRestClient;

    // Si el constructor del service no existe, Mockito injectará por field.
    @InjectMocks CardProcessServiceImpl service;

    @Test
    void callAbandonCase_returnsOk_whenClientSucceeds() throws Exception {
        // Arrange
        String caseId = "100";
        String endpoint = "http://host/cancel/#idcaso#";

        // Stub de dependencias usadas dentro del método
        CaseRelationDTO relation = mock(CaseRelationDTO.class);
        when(relation.getFictionalCaseId()).thenReturn("ABC-123");
        when(coexistenceService.getCaseRelationByPpaasCaseId(anyString()))
                .thenReturn(relation);

        when(tokenProvider.getBearerToken()).thenReturn("mock-token");

        // Mockear RestClientBuilder.newBuilder() -> builder -> build(CardRestClient.class)
        try (MockedStatic<RestClientBuilder> staticMock = mockStatic(RestClientBuilder.class)) {
            RestClientBuilder builder = mock(RestClientBuilder.class);

            staticMock.when(RestClientBuilder::newBuilder).thenReturn(builder);
            when(builder.baseUri(any(URI.class))).thenReturn(builder);
            when(builder.build(CardRestClient.class)).thenReturn(cardRestClient);

            when(cardRestClient.callAbandonCase(anyString(), anyString(), anyString()))
                    .thenReturn("OK");

            // Act
            String result = service.callAbandonCase(caseId, endpoint);

            // Assert
            assertEquals("OK", result);
            verify(cardRestClient, times(1))
                    .callAbandonCase(anyString(), anyString(), anyString());
        }
    }
}
