import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

// Ajusta el import de CustomerCase a tu paquete real
import com.santander.sgt.apm1953.sgtapm1953ppcasedm.model.paas.main.CustomerCase;

class FormatterUtilsTest {

    @Test
    void testSetActorIdList_emptyApplicants_returnsUserId() {
        List<CustomerCase> applicants = new ArrayList<>();
        String userId = "U1";

        String result = FormatterUtils.setActorIdList(applicants, userId);

        assertEquals("U1", result);
    }

    @Test
    void testSetActorIdList_firstApplicantFillsActorIdList() {
        List<CustomerCase> applicants = new ArrayList<>();
        CustomerCase applicant = new CustomerCase();
        applicant.setCustomerId("A1");
        applicants.add(applicant);

        String result = FormatterUtils.setActorIdList(applicants, "U1");

        // Como estaba vacío, el primero entra directo
        assertEquals("A1", result);
    }

    @Test
    void testSetActorIdList_skipUserId() {
        List<CustomerCase> applicants = new ArrayList<>();
        CustomerCase a1 = new CustomerCase();
        a1.setCustomerId("U1"); // mismo que userId -> se debe saltar
        CustomerCase a2 = new CustomerCase();
        a2.setCustomerId("A2");

        applicants.add(a1);
        applicants.add(a2);

        String result = FormatterUtils.setActorIdList(applicants, "U1");

        // Se ignora U1, se concatena solo A2
        assertEquals("U1,A2", result);
    }

    @Test
    void testSetActorIdList_multipleApplicants() {
        List<CustomerCase> applicants = new ArrayList<>();
        CustomerCase a1 = new CustomerCase();
        a1.setCustomerId("A1");
        CustomerCase a2 = new CustomerCase();
        a2.setCustomerId("A2");
        CustomerCase a3 = new CustomerCase();
        a3.setCustomerId("A3");

        applicants.add(a1);
        applicants.add(a2);
        applicants.add(a3);

        String result = FormatterUtils.setActorIdList(applicants, "U1");

        assertEquals("A1,A2,A3", result);
    }

    @Test
    void testSetAccessPoint_returnsExpectedConstants() {
        String taskId = "dummy";
        String result = FormatterUtils.setAccessPoint(taskId);

        String expected = 
            CCARProcessConstants.CCAR_GOFI_PC_WIN + "," +
            CCARProcessConstants.CCAR_CLBO_PC_WIN + "," +
            CCARProcessConstants.CCAR_CLBO_MOV_IOS + "," +
            CCARProcessConstants.CCAR_CLBO_MOV_AND; // ajusta según tu clase

        assertEquals(expected, result);
    }
}
