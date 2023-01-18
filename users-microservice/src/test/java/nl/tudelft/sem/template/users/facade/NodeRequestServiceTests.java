package nl.tudelft.sem.template.users.facade;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.domain.AccountType;
import nl.tudelft.sem.template.users.domain.Employee;
import nl.tudelft.sem.template.users.domain.EmployeeRepository;
import nl.tudelft.sem.template.users.domain.EmployeeService;
import nl.tudelft.sem.template.users.domain.FacultyAccount;
import nl.tudelft.sem.template.users.domain.FacultyAccountRepository;
import nl.tudelft.sem.template.users.domain.FacultyAccountService;
import nl.tudelft.sem.template.users.domain.FacultyVerificationService;
import nl.tudelft.sem.template.users.domain.RegistrationService;
import nl.tudelft.sem.template.users.domain.Sysadmin;
import nl.tudelft.sem.template.users.domain.SysadminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

public class NodeRequestServiceTests {

    private SysadminRepository sysadminRepository;
    private EmployeeRepository employeeRepository;
    private FacultyAccountRepository facultyAccountRepository;

    private FacultyVerificationService facultyVerificationService;
    private RestTemplate restTemplate;
    private RegistrationService registrationService;
    private AuthorizationManager authorization;
    private MockRestServiceServer mockRestServiceServer;
    private RequestSenderService sut;
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
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);

        sut = new RequestSenderService(authorization, restTemplate);

        admin = new Sysadmin(adminNetId);
        employee = new Employee(employeeNetId);
        facultyAccount = new FacultyAccount(facultyNetId, facultyId);

        when(authorization.isOfType(adminNetId, AccountType.SYSADMIN)).thenReturn(true);
        when(authorization.isOfType(employeeNetId, AccountType.EMPLOYEE)).thenReturn(true);
        when(authorization.isOfType(facultyNetId, AccountType.FAC_ACCOUNT)).thenReturn(true);
    }
}
