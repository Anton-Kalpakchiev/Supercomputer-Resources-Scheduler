package nl.tudelft.sem.template.users.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.Set;
import nl.tudelft.sem.template.users.authentication.AuthManager;
import nl.tudelft.sem.template.users.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.domain.AccountType;
import nl.tudelft.sem.template.users.domain.Employee;
import nl.tudelft.sem.template.users.domain.EmployeeRepository;
import nl.tudelft.sem.template.users.domain.FacultyAccount;
import nl.tudelft.sem.template.users.domain.FacultyAccountRepository;
import nl.tudelft.sem.template.users.domain.FacultyVerificationService;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
import nl.tudelft.sem.template.users.domain.Sysadmin;
import nl.tudelft.sem.template.users.domain.SysadminRepository;
import nl.tudelft.sem.template.users.models.FacultyAssignmentRequestModel;
import nl.tudelft.sem.template.users.models.PromotionRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private transient AuthManager mockAuthenticationManager;
    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    private ObjectMapper objectMapper;

    @MockBean
    private FacultyVerificationService facultyVerificationService;

    @MockBean
    private AuthorizationManager authorization;

    @MockBean
    private EmployeeRepository employeeRepository;

    @MockBean
    private SysadminRepository sysadminRepository;

    @MockBean
    private FacultyAccountRepository facultyAccountRepository;

    private String employeeNetId;
    private final String adminNetId = "admin";
    private long facultyId;
    private Sysadmin admin;
    private Employee employee;


    @BeforeEach
    void setup() {
        employeeNetId = "mayte";
        facultyId = 6L;

        objectMapper = new ObjectMapper();

        admin = new Sysadmin(adminNetId);
        employee = new Employee(employeeNetId);
    }


    @Test
    public void newAdminTest() throws Exception {
        //Arrange
        when(mockAuthenticationManager.getNetId()).thenReturn(adminNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(adminNetId);
        //Act
        ResultActions result = mockMvc.perform(get("/newUser")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        //Assert
        result.andExpect(status().isOk());

        verify(sysadminRepository).save(admin);
        verifyNoInteractions(employeeRepository);

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("User (admin) was added as a Sysadmin.");
    }

    @Test
    public void newEmployeeTest() throws Exception {
        //Arrange
        when(mockAuthenticationManager.getNetId()).thenReturn(employeeNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(employeeNetId);
        //Act
        ResultActions result = mockMvc.perform(get("/newUser")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        //Assert
        result.andExpect(status().isOk());

        verify(employeeRepository).save(employee);
        verifyNoInteractions(sysadminRepository);

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("User (" + employeeNetId + ") was added as an Employee.");
    }

    @Test
    public void assignFacultyTest() throws Exception {
        //Arrange
        when(mockAuthenticationManager.getNetId()).thenReturn("admin");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("admin");
        when(facultyVerificationService.verifyFaculty(anyLong(), anyString())).thenReturn(true);
        when(authorization.isOfType(anyString(), any())).thenReturn(true);
        when(employeeRepository.findByNetId(employeeNetId)).thenReturn(Optional.of(new Employee(employeeNetId)));

        //Act
        ResultActions result = mockMvc.perform(post("/hireEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(objectMapper.writeValueAsString(
                        new FacultyAssignmentRequestModel(employeeNetId, String.valueOf(facultyId)))));

        //Assert
        result.andExpect(status().isOk());

        verify(employeeRepository).saveAndFlush(new Employee(employeeNetId, Set.of(6L)));
        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("User (mayte) was assigned to faculty: [6]");
    }

    @Test
    public void assignFacultyTestMultiple() throws Exception {
        //Arrange
        when(mockAuthenticationManager.getNetId()).thenReturn("admin");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("admin");
        when(facultyVerificationService.verifyFaculty(anyLong(), anyString())).thenReturn(true);
        when(authorization.isOfType(anyString(), any())).thenReturn(true);
        when(employeeRepository.findByNetId(employeeNetId)).thenReturn(Optional.of(new Employee(employeeNetId)));

        //Act
        ResultActions result = mockMvc.perform(post("/hireEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(objectMapper.writeValueAsString(
                        new FacultyAssignmentRequestModel(employeeNetId, facultyId + ", " + String.valueOf(7L)))));

        //Assert
        result.andExpect(status().isOk());

        verify(employeeRepository, times(2)).saveAndFlush(new Employee(employeeNetId, Set.of(6L, 7L)));
        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("User (mayte) was assigned to the following faculties: [6, 7]");
    }

    @Test
    public void fireEmployeeTest() throws Exception {
        //Arrange
        when(mockAuthenticationManager.getNetId()).thenReturn("admin");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("admin");
        when(facultyVerificationService.verifyFaculty(anyLong(), anyString())).thenReturn(true);
        when(authorization.isOfType(anyString(), any())).thenReturn(true);
        when(employeeRepository.findByNetId(employeeNetId))
                .thenReturn(Optional.of(new Employee(employeeNetId, Set.of(facultyId))));

        //Act
        ResultActions result = mockMvc.perform(post("/terminateEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(objectMapper.writeValueAsString(
                        new FacultyAssignmentRequestModel(employeeNetId, String.valueOf(facultyId)))));

        //Assert
        result.andExpect(status().isOk());

        verify(employeeRepository).saveAndFlush(new Employee(employeeNetId, Set.of()));
        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("User (mayte) was removed from faculty: [6]");
    }

    @Test
    public void fireEmployeeTestMultipleFaculties() throws Exception {
        //Arrange
        when(mockAuthenticationManager.getNetId()).thenReturn("admin");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("admin");
        when(facultyVerificationService.verifyFaculty(anyLong(), anyString())).thenReturn(true);
        when(authorization.isOfType(anyString(), any())).thenReturn(true);

        long secondFaculty = 5L;
        when(employeeRepository.findByNetId(employeeNetId))
                .thenReturn(Optional.of(new Employee(employeeNetId, Set.of(facultyId, secondFaculty))));

        //Act
        ResultActions result = mockMvc.perform(post("/terminateEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(objectMapper.writeValueAsString(
                        new FacultyAssignmentRequestModel(employeeNetId, facultyId + ", " + secondFaculty))));

        //Assert
        result.andExpect(status().isOk());

        verify(employeeRepository, times(2)).saveAndFlush(new Employee(employeeNetId, Set.of()));
        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("User (" + employeeNetId
                + ") was removed from the following faculties: [5, 6]");
    }

    @Test
    public void fireEmployeeUnauthorized() throws Exception {
        //Arrange
        when(mockAuthenticationManager.getNetId()).thenReturn("admin");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("admin");
        when(facultyVerificationService.verifyFaculty(anyLong(), anyString())).thenReturn(true);
        when(authorization.isOfType(anyString(), any())).thenReturn(false);

        long secondFaculty = 5L;
        when(employeeRepository.findByNetId(employeeNetId))
                .thenReturn(Optional.of(new Employee(employeeNetId, Set.of(facultyId, secondFaculty))));

        //Act
        ResultActions result = mockMvc.perform(post("/terminateEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(objectMapper.writeValueAsString(
                        new FacultyAssignmentRequestModel(employeeNetId, facultyId + ", " + secondFaculty))));

        //Assert
        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void checkAccessAdminNormalFlow() throws Exception {
        when(mockAuthenticationManager.getNetId()).thenReturn(adminNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(adminNetId);

        when(sysadminRepository.existsByNetId(adminNetId)).thenReturn(true);
        when(employeeRepository.existsByNetId(adminNetId)).thenReturn(false);
        when(facultyAccountRepository.existsByNetId(adminNetId)).thenReturn(false);

        when(authorization.checkAccess(adminNetId)).thenReturn(AccountType.SYSADMIN);

        ResultActions result = mockMvc.perform(get("/checkAccess")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("{\"access\":\"Sysadmin\"}");
    }

    @Test
    public void checkAccessEmployeeNormalFlow() throws Exception {
        when(mockAuthenticationManager.getNetId()).thenReturn(employeeNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(employeeNetId);

        when(sysadminRepository.existsByNetId(employeeNetId)).thenReturn(false);
        when(employeeRepository.existsByNetId(employeeNetId)).thenReturn(true);
        when(facultyAccountRepository.existsByNetId(employeeNetId)).thenReturn(false);

        when(authorization.checkAccess(employeeNetId)).thenReturn(AccountType.EMPLOYEE);

        ResultActions result = mockMvc.perform(get("/checkAccess")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("{\"access\":\"Employee\"}");
    }

    @Test
    public void checkAccessNonExistent() throws Exception {
        when(mockAuthenticationManager.getNetId()).thenReturn(employeeNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(employeeNetId);

        when(sysadminRepository.existsByNetId(employeeNetId)).thenReturn(false);
        when(employeeRepository.existsByNetId(employeeNetId)).thenReturn(false);
        when(facultyAccountRepository.existsByNetId(employeeNetId)).thenReturn(false);

        when(authorization.checkAccess(employeeNetId)).thenThrow(new NoSuchUserException(""));

        ResultActions result = mockMvc.perform(get("/checkAccess")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        result.andExpect(status().isBadRequest());
    }

    @Test
    public void promoteToSysadminNormalFlow() throws Exception {
        when(mockAuthenticationManager.getNetId()).thenReturn(adminNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(adminNetId);

        when(authorization.checkAccess(adminNetId)).thenReturn(AccountType.SYSADMIN);
        when(authorization.checkAccess(employeeNetId)).thenReturn(AccountType.EMPLOYEE);
        when(authorization.isOfType(anyString(), any(AccountType.class))).thenReturn(true);

        when(employeeRepository.existsByNetId(employeeNetId)).thenReturn(true);

        ResultActions result = mockMvc.perform(post("/promoteToSysadmin")
                .header("Authorization", "Bearer MockedToken")
                .content("{\"netId\":\"" + employeeNetId + "\"}")
                .contentType(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk());

        verify(employeeRepository).deleteByNetId(employeeNetId);
        verify(sysadminRepository).save(new Sysadmin(employeeNetId));
    }

    @Test
    public void promoteToSysadminUnauthorized() throws Exception {
        when(mockAuthenticationManager.getNetId()).thenReturn(adminNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(adminNetId);

        when(authorization.checkAccess(adminNetId)).thenReturn(AccountType.EMPLOYEE);
        when(authorization.checkAccess(employeeNetId)).thenReturn(AccountType.EMPLOYEE);

        ResultActions result = mockMvc.perform(post("/promoteToSysadmin")
                .header("Authorization", "Bearer MockedToken")
                .content("{\"netId\":\"" + employeeNetId + "\"}")
                .contentType(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void promoteToSysadminNoSuchUser() throws Exception {
        when(mockAuthenticationManager.getNetId()).thenReturn(adminNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(adminNetId);

        when(authorization.checkAccess(adminNetId)).thenReturn(AccountType.SYSADMIN);
        when(authorization.checkAccess(employeeNetId)).thenThrow(new NoSuchUserException(""));
        when(authorization.isOfType(adminNetId, AccountType.SYSADMIN)).thenReturn(true);

        ResultActions result = mockMvc.perform(post("/promoteToSysadmin")
                .header("Authorization", "Bearer MockedToken")
                .content("{\"netId\":\"" + employeeNetId + "\"}")
                .contentType(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isBadRequest());
    }

    @Test
    public void getFacultyIdNormalFlow() throws Exception {
        String facultyNetId = "mathAccount";
        long facId = 5L;
        when(facultyAccountRepository.findByNetId(facultyNetId)).thenReturn(
                Optional.of(new FacultyAccount(facultyNetId, facId)));

        when(mockAuthenticationManager.getNetId()).thenReturn(facultyNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(facultyNetId);

        ResultActions result = mockMvc.perform(post("/getFacultyIdForManager")
                .header("Authorization", "Bearer MockedToken")
                .contentType(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(facId)));
    }

    @Test
    public void getFacultyIdNoSuchUser() throws Exception {
        String facultyNetId = "mathAccount";
        when(mockAuthenticationManager.getNetId()).thenReturn(facultyNetId);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(facultyNetId);

        ResultActions result = mockMvc.perform(post("/getFacultyIdForManager")
                .header("Authorization", "Bearer MockedToken")
                .contentType(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isBadRequest());
    }
}
