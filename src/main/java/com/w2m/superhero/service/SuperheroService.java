import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CardProcessServiceImplTest {

    @Inject
    CardProcessServiceImpl service;

    @InjectMock
    CardRestClient cardRestClient;

    @Test
    void testCallAbandonCase() {
        // given
        String caseId = "100";
        String endpoint = "http://localhost/cancel/#idcaso#";
        String expectedResponse = "OK";

        // Mock del cliente REST
        when(cardRestClient.callAbandonCase(anyString(), anyString(), anyString()))
                .thenReturn("OK");

        // when
        String result = service.callAbandonCase(caseId, endpoint);

        // then
        assertEquals(expectedResponse, result);
        verify(cardRestClient, times(1))
                .callAbandonCase(anyString(), anyString(), anyString());
    }
}
