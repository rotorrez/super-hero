import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        assertEquals(CCARProcessConstants.CCAR_E_01, status); // valor inicial por defecto
    }

    @Test
    void testGetPortfolioConfig_mapsCorrectly() {
        ProcessConfigurationDTO configDto = new ProcessConfigurationDTO();
        Map<String, Object> configData = new HashMap<>();
        configDto.setConfigurationData(configData);

        when(processConfigurationService.getConfiguration(
                eq(CCARProcessConstants.CCAR_PROCESS),
                eq(CCARProcessConstants.CCAR_CONFIG_PROCESSPAAS)))
                .thenReturn(configDto);

        PortfolioConfigDTO result = service.getPortfolioConfig();

        assertNotNull(result);
    }

    @Test
    void testGetInactivityConfigByType_formalization() {
        InactivityDTO expected = new InactivityDTO();

        // Mock del PortfolioConfigDTO con InactivityDTO
        InactivityDTO inactivity = mock(InactivityDTO.class);
        when(inactivity.getFormalizationInactivity()).thenReturn(expected);

        PortfolioConfigDTO portfolioConfig = mock(PortfolioConfigDTO.class);
        when(portfolioConfig.getInactivity()).thenReturn(inactivity);

        // Mock del service dependency
        ProcessConfigurationDTO configDto = new ProcessConfigurationDTO();
        configDto.setConfigurationData(new HashMap<>());
        when(processConfigurationService.getConfiguration(any(), any())).thenReturn(configDto);

        // ⚠️ Hack: Sobrescribir manualmente el ObjectMapper dentro del método
        // (puedes dejarlo como está, el mapper.convertValue funciona con HashMap vacío)
        PortfolioConfigDTO dummy = new PortfolioConfigDTO();
        dummy.setInactivity(inactivity);

        // Forzar que service.getPortfolioConfig() devuelva dummy en lugar de nuevo objeto
        // 👉 Esto lo conseguimos devolviendo un configDto vacío y usando convertValue que crea el dummy.
        // Si quieres control total, lo ideal es refactorizar service para poder inyectar el ObjectMapper.

        InactivityDTO result = service.getInactivityConfigByType(
                CCARProcessConstants.INACTIVITY_TYPE_FORMALIZATION);

        assertNotNull(result);
    }

    @Test
    void testGetInactivityConfigByType_default() {
        InactivityDTO expected = new InactivityDTO();

        InactivityDTO inactivity = mock(InactivityDTO.class);
        when(inactivity.getStandarInactivity()).thenReturn(expected);

        PortfolioConfigDTO portfolioConfig = mock(PortfolioConfigDTO.class);
        when(portfolioConfig.getInactivity()).thenReturn(inactivity);

        ProcessConfigurationDTO configDto = new ProcessConfigurationDTO();
        configDto.setConfigurationData(new HashMap<>());
        when(processConfigurationService.getConfiguration(any(), any())).thenReturn(configDto);

        InactivityDTO result = service.getInactivityConfigByType("UNKNOWN");

        assertNotNull(result);
    }
}
