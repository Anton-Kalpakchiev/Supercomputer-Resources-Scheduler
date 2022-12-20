package nl.tudelft.sem.template.users.domain.promotion;

import static net.bytebuddy.matcher.ElementMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.AccountType;
import nl.tudelft.sem.template.users.domain.Employee;
import nl.tudelft.sem.template.users.domain.EmployeeRepository;
import nl.tudelft.sem.template.users.domain.FacultyAccount;
import nl.tudelft.sem.template.users.domain.FacultyAccountRepository;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
import nl.tudelft.sem.template.users.domain.PromotionAndEmploymentService;
import nl.tudelft.sem.template.users.domain.RegistrationService;
import nl.tudelft.sem.template.users.domain.Sysadmin;
import nl.tudelft.sem.template.users.domain.SysadminRepository;
import nl.tudelft.sem.template.users.models.FacultyCreationResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

public class PromotionAndEmploymentUnitTest {
    private SysadminRepository sysadminRepository;
    private EmployeeRepository employeeRepository;
    private FacultyAccountRepository facultyAccountRepository;
    private RestTemplate restTemplate;
    private RegistrationService registrationService;
    private AuthorizationManager authorization;
    private MockRestServiceServer mockRestServiceServer;
    private PromotionAndEmploymentService sut;

    private Sysadmin admin;
    private Employee employee;
    private FacultyAccount facultyAccount;
    private final String adminNetId = "admin";
    private final String employeeNetId = "ivo";
    private final String facultyNetId = "professor";
    private final String facultyName = "math";
    private final int facultyNumber = 0;

    private final String sampleToken = "1234567";

    @BeforeEach
    void setup() throws Exception {
        sysadminRepository = mock(SysadminRepository.class);
        employeeRepository = mock(EmployeeRepository.class);
        facultyAccountRepository = mock(FacultyAccountRepository.class);
        restTemplate = new RestTemplate();
        registrationService = mock(RegistrationService.class);
        authorization = mock(AuthorizationManager.class);
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);

        sut = new PromotionAndEmploymentService(sysadminRepository, employeeRepository,
                facultyAccountRepository, registrationService, authorization, restTemplate);

        admin = new Sysadmin(adminNetId);
        employee = new Employee(employeeNetId);
        facultyAccount = new FacultyAccount(facultyNetId, facultyNumber);

        when(authorization.isOfType(adminNetId, AccountType.SYSADMIN)).thenReturn(true);
        when(authorization.isOfType(employeeNetId, AccountType.EMPLOYEE)).thenReturn(true);
        when(authorization.isOfType(facultyNetId, AccountType.FAC_ACCOUNT)).thenReturn(true);
    }

    @Test
    public void promoteEmployeeToSysadminNormalFlow() {
        try {
            sut.promoteEmployeeToSysadmin(adminNetId, employeeNetId);
            verify(registrationService).dropEmployee(employeeNetId);
            verify(registrationService).addSysadmin(employeeNetId);
        } catch (Exception e) {
            fail("An exception was thrown.");
        }
    }

    @Test
    public void promoteEmployeeToSysadminExceptions() {
        UnauthorizedException result3 = assertThrows(UnauthorizedException.class,
                () -> sut.promoteEmployeeToSysadmin(facultyNetId, employeeNetId));
        assertEquals("User (" + facultyNetId + ") is not a Sysadmin => can not promote", result3.getMessage());
        UnauthorizedException result4 = assertThrows(UnauthorizedException.class,
                () -> sut.promoteEmployeeToSysadmin(employeeNetId, employeeNetId));
        assertEquals("User (" + employeeNetId + ") is not a Sysadmin => can not promote", result4.getMessage());


        NoSuchUserException result1 = assertThrows(NoSuchUserException.class,
                () -> sut.promoteEmployeeToSysadmin(adminNetId, adminNetId));
        assertEquals("No such employee: " + adminNetId, result1.getMessage());

        NoSuchUserException result2 = assertThrows(NoSuchUserException.class,
                () -> sut.promoteEmployeeToSysadmin(adminNetId, facultyNetId));
        assertEquals("No such employee: " + facultyNetId, result2.getMessage());
    }

    @Test
    public void createFacultyNormalFlow() {
        try {
            mockRestServiceServer.expect(requestTo("http://localhost:8085/createFaculty"))
                    .andRespond(withSuccess("{\"facultyId\": \"" + facultyNumber + "\"}", MediaType.APPLICATION_JSON));

            long expected = sut.createFaculty(adminNetId, employeeNetId, facultyName, sampleToken);
            assertThat(expected).isEqualTo(facultyNumber);

            verify(registrationService).dropEmployee(employeeNetId);
            verify(registrationService).addFacultyAccount(employeeNetId, facultyNumber);
        } catch (Exception e) {
            fail("An exception was thrown.");
        }
    }

    @Test
    public void createFacultyExceptionsUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> {
            sut.createFaculty(facultyNetId, employeeNetId, facultyName, sampleToken);
        });
    }

    @Test
    public void createFacultyExceptionsNoSuchUser() {
        assertThrows(NoSuchUserException.class, () -> {
            sut.createFaculty(adminNetId, facultyNetId, facultyName, sampleToken);
        });
    }

    @Test
    public void createFacultyExceptionsCanNotSendRequest() {
        mockRestServiceServer.expect(requestTo("http://localhost:8085/createFaculty"))
                .andRespond(withBadRequest());
        assertThrows(Exception.class, () -> {
            sut.createFaculty(adminNetId, employeeNetId, facultyName, sampleToken);
        });
    }
}
