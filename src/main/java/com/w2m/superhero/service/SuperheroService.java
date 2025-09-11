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

        ProcessConfigurationDTO dto = new ProcessConfigurati
