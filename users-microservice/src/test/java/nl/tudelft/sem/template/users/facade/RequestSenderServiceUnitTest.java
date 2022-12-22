package nl.tudelft.sem.template.users.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.*;
import nl.tudelft.sem.template.users.models.facade.DistributionModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class RequestSenderServiceUnitTest {
    private SysadminRepository sysadminRepository;
    private EmployeeRepository employeeRepository;
    private FacultyAccountRepository facultyAccountRepository;
    private RestTemplate restTemplate;
    private RegistrationService registrationService;
    private AuthorizationManager authorization;
    private MockRestServiceServer mockRestServiceServer;
    private RequestSenderService sut;

    private FacultyAccountService facultyAccountService;

    private Sysadmin admin;
    private Employee employee;
    private FacultyAccount facultyAccount;
    private final String adminNetId = "admin";
    private final String employeeNetId = "ivo";
    private final String facultyNetId = "professor";
    private final String facultyName = "math";
    private final long facultyID = 0L;

    private final String sampleToken = "1234567";
    private final String url = "/Test/url";

    @BeforeEach
    void setup() throws Exception {
        sysadminRepository = mock(SysadminRepository.class);
        employeeRepository = mock(EmployeeRepository.class);
        facultyAccountRepository = mock(FacultyAccountRepository.class);
        facultyAccountService = new FacultyAccountService(facultyAccountRepository);
        restTemplate = new RestTemplate();
        registrationService = mock(RegistrationService.class);
        authorization = mock(AuthorizationManager.class);
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);

        sut = new RequestSenderService(facultyAccountService,
                authorization, restTemplate);

        admin = new Sysadmin(adminNetId);
        employee = new Employee(employeeNetId);
        facultyAccount = new FacultyAccount(facultyNetId, facultyID);

        when(authorization.isOfType(adminNetId, AccountType.SYSADMIN)).thenReturn(true);
        when(authorization.isOfType(employeeNetId, AccountType.EMPLOYEE)).thenReturn(true);
        when(authorization.isOfType(facultyNetId, AccountType.FAC_ACCOUNT)).thenReturn(true);
    }

    @Test
    public void addDistributionNormalFlow() {
        DistributionModel model = new DistributionModel("math", 20, 20, 5);
        mockRestServiceServer.expect(requestTo(url))
                .andRespond(withSuccess());
        try {
            sut.addDistributionRequest(url, adminNetId, sampleToken, model);

        } catch (Exception e) {
            fail("An exception was thrown");
        }
    }

    @Test
    public void addDistributionUnauthorized() {
        DistributionModel model = new DistributionModel("math", 20, 20, 5);
        assertThrows(UnauthorizedException.class, () -> {
            sut.addDistributionRequest(url, employeeNetId, sampleToken, model);
        });
    }

    @Test
    public void addDistributionInnerRequestFails() {
        DistributionModel model = new DistributionModel("math", 20, 20, 5);
        mockRestServiceServer.expect(requestTo(url))
                .andRespond(withBadRequest());

        assertThrows(InnerRequestFailedException.class, () -> {
            sut.addDistributionRequest(url, adminNetId, sampleToken, model);
        });
    }

    @Test
    public void postRequestFromSysadminNormalFlow() {
        mockRestServiceServer.expect(requestTo(url))
                .andRespond(withSuccess());
        try {
            sut.postRequestFromSysadmin(url, adminNetId, sampleToken);

        } catch (Exception e) {
            fail("An exception was thrown");
        }
    }

    @Test
    public void postRequestFromSysadminUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> {
            sut.postRequestFromSysadmin(url, employeeNetId, sampleToken);
        });
    }

    @Test
    public void postRequestInnerException() {
        mockRestServiceServer.expect(requestTo(url))
                .andRespond(withBadRequest());

        assertThrows(InnerRequestFailedException.class, () -> {
            sut.postRequestFromSysadmin(url, adminNetId, sampleToken);
        });
    }

    @Test
    public void getRequestNormalFlow() {
        String body = "SUCCESS";
        mockRestServiceServer.expect(requestTo(url))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        try {
            String result = sut.getRequestFromSysadmin(url, adminNetId, sampleToken);
            assertThat(result).isEqualTo(body);
        } catch (Exception e) {
            fail("An exception was thrown");
        }
    }

    @Test
    public void getRequestUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> {
            sut.getRequestFromSysadmin(url, employeeNetId, sampleToken);
        });
    }

    @Test
    public void getRequestInnerException() {
        mockRestServiceServer.expect(requestTo(url))
                .andRespond(withBadRequest());

        assertThrows(InnerRequestFailedException.class, () -> {
            sut.getRequestFromSysadmin(url, adminNetId, sampleToken);
        });
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
        assertThrows(UnauthorizedException.class, () -> {
            sut.getScheduleRequestRouter(employeeNetId, sampleToken);
        });
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
}