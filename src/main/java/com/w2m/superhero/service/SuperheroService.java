import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class PortfolioProcessServiceTest {

    @Mock
    ProcessConfigurationService processConfigurationService;

    @InjectMocks
    PortfolioProcessService service;

    private ProcessConfigurationDTO configDto;

    @BeforeEach
    void setup() {
        configDto = new ProcessConfigurationDTO();
        Map<String, Object> configData = new HashMap<>();
        // puedes añadir datos falsos si quieres testear el mapeo
        configDto.setConfigurationData(configData);
    }

    @Test
    void testGetInitialStatus_homebanking() {
        Integer status = PortfolioProcessService.getInitialStatus(CCARProcessConstants.CCAR_CLBO_PC_WIN);
        assertEquals(CCARProcessConstants.CCAR_E_01, status);
    }

    @Test
    void testGetInitialStatus_mobileIos() {
        Integer status = PortfolioProcessService.getInitialStatus(CCARProcessConstants.CCAR_CLBO_MOV_IOS);
        assertEquals(CCARProcessConstants.CCAR_E_02, status);
    }

    @Test
    void testGetInitialStatus_default() {
        Integer status = PortfolioProcessService.getInitialStatus("UNKNOWN");
        assertEquals(CCARProcessConstants.CCAR_E_01, status);
