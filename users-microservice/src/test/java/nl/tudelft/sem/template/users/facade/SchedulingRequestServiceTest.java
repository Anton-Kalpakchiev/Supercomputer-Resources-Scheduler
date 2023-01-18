package nl.tudelft.sem.template.users.facade;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Optional;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.AccountType;
import nl.tudelft.sem.template.users.domain.Employee;
import nl.tudelft.sem.template.users.domain.EmployeeRepository;
import nl.tudelft.sem.template.users.domain.EmployeeService;
import nl.tudelft.sem.template.users.domain.FacultyAccount;
import nl.tudelft.sem.template.users.domain.FacultyAccountRepository;
import nl.tudelft.sem.template.users.domain.FacultyAccountService;
import nl.tudelft.sem.template.users.domain.InnerRequestFailedException;
import nl.tudelft.sem.template.users.domain.RegistrationService;
import nl.tudelft.sem.template.users.domain.Sysadmin;
import nl.tudelft.sem.template.users.domain.SysadminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;


public class SchedulingRequestServiceTest {

    private SysadminRepository sysadminRepository;
    private EmployeeRepository employeeRepository;
    private FacultyAccountRepository facultyAccountRepository;
    private VerificationService verificationService;
    private RestTemplate restTemplate;
    private RegistrationService registrationService;
    private AuthorizationManager authorization;
    private MockRestServiceServer mockRestServiceServer;
    private SchedulingRequestsService sut;
    private FacultyAccountService facultyAccountService;
    private EmployeeService employeeService;
    private Sysadmin admin;
    private Employee employee;
    private FacultyAccount facultyAccount;
    private final String adminNetId = "admin";
    private final String employeeNetId = "ivo";
    private final String facultyNetId = "professor";
    private final String facultyName = "math";
    private final long facultyId = 0L;

    private final String sampleToken = "1234567";
    private final String url = "/Test/url";

    @BeforeEach
    void setup() throws Exception {
        facultyAccountService = mock(FacultyAccountService.class);
        sysadminRepository = mock(SysadminRepository.class);
        employeeRepository = mock(EmployeeRepository.class);
        facultyAccountRepository = mock(FacultyAccountRepository.class);
        facultyAccountService = new FacultyAccountService(facultyAccountRepository);
        restTemplate = new RestTemplate();
        registrationService = mock(RegistrationService.class);
        authorization = mock(AuthorizationManager.class);
        employeeService = mock(EmployeeService.class);
        verificationService = mock(VerificationService.class);
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);

        sut = new SchedulingRequestsService(authorization, restTemplate, verificationService);

        admin = new Sysadmin(adminNetId);
        employee = new Employee(employeeNetId);
        facultyAccount = new FacultyAccount(facultyNetId, facultyId);

        when(authorization.isOfType(adminNetId, AccountType.SYSADMIN)).thenReturn(true);
        when(verificationService.retrieveFacultyId(facultyNetId)).thenReturn(facultyId);
        when(authorization.isOfType(employeeNetId, AccountType.EMPLOYEE)).thenReturn(true);
        when(authorization.isOfType(facultyNetId, AccountType.FAC_ACCOUNT)).thenReturn(true);
    }

    @Test
    public void getScheduleFacultyManagerExceptionTest() {
        mockRestServiceServer.expect(requestTo(url))
                .andRespond(withBadRequest());
        assertThrows(InnerRequestFailedException.class, () -> {
            sut.getScheduleFacultyManager(url, facultyNetId, sampleToken);
        });
    }

    @Test
    public void getScheduleRequestRouterTestUnauthorized() {
        assertThrows(UnauthorizedException.class, () ->
                sut.getScheduleRequestRouter(employeeNetId, sampleToken));
    }

    @Test
    public void getScheduleRequestRouterExceptionFacManager() {
        String testUrl = "http://localhost:8085/getFacultySchedules";
        mockRestServiceServer.expect(requestTo(testUrl))
                .andRespond(withBadRequest());
        assertThrows(InnerRequestFailedException.class, () -> {
            sut.getScheduleRequestRouter(facultyNetId, sampleToken);
        });
    }

    @Test
    public void getScheduleRequestRouterExceptionSysadmin() {
        String testUrl = "http://localhost:8085/getAllSchedules";
        mockRestServiceServer.expect(requestTo(testUrl))
                .andRespond(withBadRequest());
        assertThrows(InnerRequestFailedException.class, () -> {
            sut.getScheduleRequestRouter(adminNetId, sampleToken);
        });
    }

    @Test
    public void getScheduleRouterFacManager() {
        when(facultyAccountRepository.findByNetId(facultyNetId)).thenReturn(Optional.of(facultyAccount));
        String testUrl = "http://localhost:8085/getFacultySchedules";
        mockRestServiceServer.expect(requestTo(testUrl))
                .andRespond(withSuccess());
        try {
            sut.getScheduleRequestRouter(facultyNetId, sampleToken);
        } catch (Exception e) {
            fail("An exception was thrown");
        }
    }

    @Test
    public void getScheduleRouterSysadmin() {
        String testUrl = "http://localhost:8085/getAllSchedules";
        mockRestServiceServer.expect(requestTo(testUrl))
                .andRespond(withSuccess());
        try {
            sut.getScheduleRequestRouter(adminNetId, sampleToken);
        } catch (Exception e) {
            fail("An exception was thrown");
        }
    }

    @Test
    public void getScheduleSysadminTest() {
        mockRestServiceServer.expect(requestTo(url))
                .andRespond(withSuccess());
        try {
            sut.getScheduleSysadmin(url, sampleToken);
        } catch (Exception e) {
            fail("An exception was thrown");
        }
    }

    @Test
    public void getScheduleSysadminException() {
        mockRestServiceServer.expect(requestTo(url))
                .andRespond(withBadRequest());
        assertThrows(InnerRequestFailedException.class, () -> {
            sut.getScheduleSysadmin(url, sampleToken);
        });
    }

    @Test
    public void getScheduleFacultyManagerTest() {
        when(facultyAccountRepository.findByNetId(facultyNetId)).thenReturn(Optional.of(facultyAccount));
        mockRestServiceServer.expect(requestTo(url))
                .andRespond(withSuccess());
        try {
            sut.getScheduleFacultyManager(url, facultyNetId, sampleToken);
        } catch (Exception e) {
            fail("An exception was thrown");
        }
    }
}
