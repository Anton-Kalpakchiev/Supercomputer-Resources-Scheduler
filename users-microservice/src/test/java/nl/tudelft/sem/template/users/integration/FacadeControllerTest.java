package nl.tudelft.sem.template.users.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.users.authentication.AuthManager;
import nl.tudelft.sem.template.users.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.domain.AccountType;
import nl.tudelft.sem.template.users.domain.Employee;
import nl.tudelft.sem.template.users.domain.EmployeeRepository;
import nl.tudelft.sem.template.users.domain.FacultyAccountRepository;
import nl.tudelft.sem.template.users.domain.FacultyVerificationService;
import nl.tudelft.sem.template.users.domain.RegistrationService;
import nl.tudelft.sem.template.users.domain.Sysadmin;
import nl.tudelft.sem.template.users.domain.SysadminRepository;
import nl.tudelft.sem.template.users.facade.NodesRequestService;
import nl.tudelft.sem.template.users.facade.RequestSenderService;
import nl.tudelft.sem.template.users.facade.RequestsRequestService;
import nl.tudelft.sem.template.users.facade.ResourcePoolRequestService;
import nl.tudelft.sem.template.users.facade.SchedulingRequestsService;
import nl.tudelft.sem.template.users.facade.VerificationService;
import nl.tudelft.sem.template.users.models.FacultyCreationRequestModel;
import nl.tudelft.sem.template.users.models.FacultyCreationResponseModel;
import nl.tudelft.sem.template.users.models.facade.DistributionModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class FacadeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private transient AuthManager mockAuthenticationManager;
    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RequestSenderService requestSenderService;
    @Autowired
    private VerificationService verificationService;
    @Autowired
    private SchedulingRequestsService schedulingRequestsService;
    @Autowired
    private RequestsRequestService rqSender;
    @Autowired
    private NodesRequestService nodesSender;
    @Autowired
    private ResourcePoolRequestService rpSender;

    @MockBean
    private FacultyVerificationService facultyVerificationService;
    @MockBean
    private EmployeeRepository employeeRepository;

    @MockBean
    private SysadminRepository sysadminRepository;

    @MockBean
    private FacultyAccountRepository facultyAccountRepository;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    private AuthorizationManager authorization;

    private MockRestServiceServer mockRestServiceServer;

    private String employeeNetId;
    private final String adminNetId = "admin";
    private long facultyId;
    private Sysadmin admin;
    private Employee employee;


    @BeforeEach
    void setup() {
        employeeNetId = "mayte";
        facultyId = 6L;
        admin = new Sysadmin(adminNetId);
        employee = new Employee(employeeNetId);

        sysadminRepository = mock(SysadminRepository.class);
        employeeRepository = mock(EmployeeRepository.class);
        facultyAccountRepository = mock(FacultyAccountRepository.class);
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void getDistributionNormalFlow() throws Exception {
        when(mockAuthenticationManager.getNetId()).thenReturn(adminNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(adminNetId);

        when(authorization.checkAccess(adminNetId)).thenReturn(AccountType.SYSADMIN);
        when(authorization.isOfType(adminNetId, AccountType.SYSADMIN)).thenReturn(true);

        String testResponse = "mock response body";

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(testResponse, HttpStatus.OK));

        ResultActions result = mockMvc.perform(get("/distribution/current")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk())
                .andExpect(content().string(testResponse));

    }

    @Test
    public void getDistributionUnauthorized() throws Exception {
        when(mockAuthenticationManager.getNetId()).thenReturn(adminNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(adminNetId);

        when(authorization.checkAccess(adminNetId)).thenReturn(AccountType.EMPLOYEE);
        when(authorization.isOfType(adminNetId, AccountType.SYSADMIN)).thenReturn(false);

        String testResponse = "mock response body";

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(testResponse, HttpStatus.OK));

        ResultActions result = mockMvc.perform(get("/distribution/current")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isUnauthorized());

    }

    @Test
    public void addDistributionNormalFlow() throws Exception {
        when(mockAuthenticationManager.getNetId()).thenReturn(adminNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(adminNetId);

        when(authorization.checkAccess(adminNetId)).thenReturn(AccountType.SYSADMIN);
        when(authorization.isOfType(adminNetId, AccountType.SYSADMIN)).thenReturn(true);

        String testResponse = "Distribution was added.";

        when(restTemplate.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResultActions result = mockMvc.perform(post("/distribution/add")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(objectMapper.writeValueAsString(new DistributionModel("math", 20, 20, 20))));

        result.andExpect(status().isOk())
                .andExpect(content().string(testResponse));

    }

    @Test
    public void addDistributionUnauthorized() throws Exception {
        when(mockAuthenticationManager.getNetId()).thenReturn(adminNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(adminNetId);

        when(authorization.checkAccess(adminNetId)).thenReturn(AccountType.EMPLOYEE);
        when(authorization.isOfType(adminNetId, AccountType.SYSADMIN)).thenReturn(false);

        String testResponse = "Distribution was added.";

        when(restTemplate.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResultActions result = mockMvc.perform(post("/distribution/add")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(objectMapper.writeValueAsString(new DistributionModel("math", 20, 20, 20))));

        result.andExpect(status().isUnauthorized());

    }

    @Test
    public void statusDistributionNormalFlow() throws Exception {
        when(mockAuthenticationManager.getNetId()).thenReturn(adminNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(adminNetId);

        when(authorization.checkAccess(adminNetId)).thenReturn(AccountType.SYSADMIN);
        when(authorization.isOfType(adminNetId, AccountType.SYSADMIN)).thenReturn(true);

        String testResponse = "test status";

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(testResponse, HttpStatus.OK));

        ResultActions result = mockMvc.perform(get("/distribution/status")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk())
                .andExpect(content().string(testResponse));
    }

    @Test
    public void statusDistributionUnauthorized() throws Exception {
        when(mockAuthenticationManager.getNetId()).thenReturn(adminNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(adminNetId);

        when(authorization.checkAccess(adminNetId)).thenReturn(AccountType.EMPLOYEE);
        when(authorization.isOfType(adminNetId, AccountType.SYSADMIN)).thenReturn(false);

        String testResponse = "test status";

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(testResponse, HttpStatus.OK));

        ResultActions result = mockMvc.perform(get("/distribution/status")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void saveDistributionNormalFlow() throws Exception {
        when(mockAuthenticationManager.getNetId()).thenReturn(adminNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(adminNetId);

        when(authorization.checkAccess(adminNetId)).thenReturn(AccountType.SYSADMIN);
        when(authorization.isOfType(adminNetId, AccountType.SYSADMIN)).thenReturn(true);

        String testResponse = "Distribution was saved.";

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResultActions result = mockMvc.perform(post("/distribution/save")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk())
                .andExpect(content().string(testResponse));
    }

    @Test
    public void saveDistributionUnauthorized() throws Exception {
        when(mockAuthenticationManager.getNetId()).thenReturn(adminNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(adminNetId);

        when(authorization.checkAccess(adminNetId)).thenReturn(AccountType.EMPLOYEE);
        when(authorization.isOfType(adminNetId, AccountType.SYSADMIN)).thenReturn(false);

        String testResponse = "Distribution was saved.";

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResultActions result = mockMvc.perform(post("/distribution/save")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void clearDistributionNormalFlow() throws Exception {
        when(mockAuthenticationManager.getNetId()).thenReturn(adminNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(adminNetId);

        when(authorization.checkAccess(adminNetId)).thenReturn(AccountType.SYSADMIN);
        when(authorization.isOfType(adminNetId, AccountType.SYSADMIN)).thenReturn(true);

        String testResponse = "Distribution was cleared.";

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResultActions result = mockMvc.perform(post("/distribution/clear")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk())
                .andExpect(content().string(testResponse));
    }

    @Test
    public void clearDistributionUnauthorized() throws Exception {
        when(mockAuthenticationManager.getNetId()).thenReturn(adminNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(adminNetId);

        when(authorization.checkAccess(adminNetId)).thenReturn(AccountType.EMPLOYEE);
        when(authorization.isOfType(adminNetId, AccountType.SYSADMIN)).thenReturn(false);

        String testResponse = "Distribution was cleared.";

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResultActions result = mockMvc.perform(post("/distribution/clear")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void createFacultyNormalFlow() throws Exception {
        when(mockAuthenticationManager.getNetId()).thenReturn(adminNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(adminNetId);

        when(authorization.checkAccess(adminNetId)).thenReturn(AccountType.SYSADMIN);
        when(authorization.isOfType(adminNetId, AccountType.SYSADMIN)).thenReturn(true);

        String facultyName = "math";
        String managerNetId = "ivo";
        long facultyId = 5;

        when(authorization.checkAccess(managerNetId)).thenReturn(AccountType.EMPLOYEE);
        when(authorization.isOfType(managerNetId, AccountType.EMPLOYEE)).thenReturn(true);

        String testResponse = "Faculty \"" + facultyName
                + "\", managed by (" + managerNetId + "), was created.";

        when(restTemplate.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(FacultyCreationResponseModel.class)
                )).thenReturn(new ResponseEntity<>(new FacultyCreationResponseModel(facultyId), HttpStatus.OK));

        ResultActions result = mockMvc.perform(post("/createFaculty")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(objectMapper.writeValueAsString(new FacultyCreationRequestModel(facultyName, managerNetId))));

        result.andExpect(status().isOk())
                .andExpect(content().string(testResponse));

        verify(registrationService).dropEmployee(managerNetId);
        verify(registrationService).addFacultyAccount(managerNetId, facultyId);
    }

    @Test
    public void createFacultyUnauthorized() throws Exception {
        when(mockAuthenticationManager.getNetId()).thenReturn(adminNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(adminNetId);

        when(authorization.checkAccess(adminNetId)).thenReturn(AccountType.EMPLOYEE);
        when(authorization.isOfType(adminNetId, AccountType.SYSADMIN)).thenReturn(false);

        String facultyName = "math";
        String managerNetId = "ivo";
        long facultyId = 5;

        when(authorization.checkAccess(managerNetId)).thenReturn(AccountType.EMPLOYEE);
        when(authorization.isOfType(managerNetId, AccountType.EMPLOYEE)).thenReturn(true);

        String testResponse = "Faculty \"" + facultyName
                + "\", managed by (" + managerNetId + "), was created.";

        when(restTemplate.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(FacultyCreationResponseModel.class)
        )).thenReturn(new ResponseEntity<>(new FacultyCreationResponseModel(facultyId), HttpStatus.OK));

        ResultActions result = mockMvc.perform(post("/createFaculty")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(objectMapper.writeValueAsString(new FacultyCreationRequestModel(facultyName, managerNetId))));

        result.andExpect(status().isUnauthorized());
    }
}
