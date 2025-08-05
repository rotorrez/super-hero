import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProcessConfigurationServiceTest {

    @InjectMocks
    ProcessConfigurationService service;

    @Mock
    TokenProvider tokenProvider;

    @Mock
    ProcessConfigurationRestClient restClient;

    @BeforeEach
    void setUp() throws Exception {
        // Inyección manual del valor de clientId (privado y con @ConfigProperty)
        Field field = ProcessConfigurationService.class.getDeclaredField("clientId");
        field.setAccessible(true);
        field.set(service, "test-client-id");
    }

    @Test
    void getConfiguration_shouldReturnDTO_whenCallSucceeds() {
        // Arrange
        String processId = "PROC123";
        String configId = "CONF456";
        String token = "fake-token";
        String bearer = "Bearer " + token;

        ProcessConfigurationDTO expected = new ProcessConfigurationDTO();
        expected.setConfigurationId(configId);

        when(tokenProvider.getBearerToken()).thenReturn(token);
        when(restClient.callGetConfiguration(processId, configId, bearer, "test-client-id")).thenReturn(expected);

        // Act
        ProcessConfigurationDTO result = service.getConfiguration(processId, configId);

        // Assert
        assertNotNull(result);
        assertEquals(configId, result.getConfigurationId());
        verify(tokenProvider).getBearerToken();
        verify(restClient).callGetConfiguration(processId, configId, bearer, "test-client-id");
    }

    @Test
    void getConfiguration_shouldThrow_whenClientFails() {
        // Arrange
        when(tokenProvider.getBearerToken()).thenReturn("token");
        WebApplicationException exception = new WebApplicationException(Response.status(500).entity("Internal error").build());
        when(restClient.callGetConfiguration(any(), any(), any(), any())).thenThrow(exception);

        // Act & Assert
        WebApplicationException thrown = assertThrows(WebApplicationException.class, () ->
                service.getConfiguration("PROC123", "CONF123")
        );
        assertEquals(500, thrown.getResponse().getStatus());
    }
}
