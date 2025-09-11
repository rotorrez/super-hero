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
    PortfolioProcessService service; // CDI no hace falta; es unit test puro

    private ProcessConfigurationDTO buildConfigDto(
            int fDays, int fHours, int fMinutes, int fSeconds,
            int sDays, int sHours, int sMinutes, int sSeconds) {

        // inactivity.formalizationInactivity / inactivity.standarInactivity
        Map<String, Object> formal = new HashMap<>();
        formal.put("days", fDays);
        formal.put("hours", fHours);
        formal.put("minutes", fMinutes);
        formal.put("seconds", fSeconds);

        Map<String, Object> standar = new HashMap<>();
        standar.put("days", sDays);
        standar.put("hours", sHours);
        standar.put("minutes", sMinutes);
        standar.put("seconds", sSeconds);

        Map<String, Object> inactivity = new HashMap<>();
        inactivity.put("formalizationInactivity", formal);
        inactivity.put("standarInactivity", standar); // ojo: “standar” tal como en tu código

        Map<String, Object> root = new HashMap<>();
        root.put("inactivity", inactivity);

        ProcessConfigurationDTO dto = new ProcessConfigurationDTO();
        dto.setConfigurationData(root);
        return dto;
    }

    @Test
    void getInactivityConfigByType_returnsFormalization_whenTypeIsFormalization() {
        // given
        ProcessConfigurationDTO cfg =
            buildConfigDto(1, 2, 3, 4,   // formalization
                           9, 8, 7, 6);  // standar (default)
        when(processConfigurationService.getConfiguration(
                eq(CCARProcessConstants.CCAR_PROCESS),
                eq(CCARProcessConstants.CCAR_CONFIG_PROCESSPAAS)))
            .thenReturn(cfg);

        // when
        InactivityDTO res =
            service.getInactivityConfigByType(CCARProcessConstants.INACTIVITY_TYPE_FORMALIZATION);

        // then
        assertNotNull(res);
        assertEquals(1, res.getDays());
        assertEquals(2, res.getHours());
        assertEquals(3, res.getMinutes());
        assertEquals(4, res.getSeconds());
    }

    @Test
    void getInactivityConfigByType_returnsStandar_whenTypeIsUnknown() {
        // given
        ProcessConfigurationDTO cfg =
            buildConfigDto(1, 2, 3, 4,   // formalization
                           5, 6, 7, 8);  // standar (default)
        when(processConfigurationService.getConfiguration(
                eq(CCARProcessConstants.CCAR_PROCESS),
                eq(CCARProcessConstants.CCAR_CONFIG_PROCESSPAAS)))
            .thenReturn(cfg);

        // when
        InactivityDTO res = service.getInactivityConfigByType("UNKNOWN");

        // then
        assertNotNull(res);
        assertEquals(5, res.getDays());
        assertEquals(6, res.getHours());
        assertEquals(7, res.getMinutes());
        assertEquals(8, res.getSeconds());
    }

    @Test
    void getInitialStatus_static_cases() {
        assertEquals(CCARProcessConstants.CCAR_E_01,
                PortfolioProcessService.getInitialStatus(CCARProcessConstants.CCAR_CLBO_PC_WIN));
        assertEquals(CCARProcessConstants.CCAR_E_02,
                PortfolioProcessService.getInitialStatus(CCARProcessConstants.CCAR_CLBO_MOV_IOS));
        assertEquals(CCARProcessConstants.CCAR_E_02,
                PortfolioProcessService.getInitialStatus(CCARProcessConstants.CCAR_CLBO_MOV_AND));
        assertEquals(CCARProcessConstants.CCAR_E_03,
                PortfolioProcessService.getInitialStatus(CCARProcessConstants.CCAR_GOFI_PC_WIN));
        // default branch
        assertEquals(CCARProcessConstants.CCAR_E_01,
                PortfolioProcessService.getInitialStatus("ANY_OTHER"));
    }
}
