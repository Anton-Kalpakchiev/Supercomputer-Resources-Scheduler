package nl.tudelft.sem.template.users.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.Set;
import nl.tudelft.sem.template.users.authentication.AuthManager;
import nl.tudelft.sem.template.users.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.domain.Employee;
import nl.tudelft.sem.template.users.domain.EmployeeRepository;
import nl.tudelft.sem.template.users.domain.FacultyVerificationService;
import nl.tudelft.sem.template.users.models.FacultyAssignmentRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private AuthorizationManager mockAuthorizationManager;

    @MockBean
    private EmployeeRepository employeeRepository;

    private String netId;

    private long facultyId;

    @BeforeEach
    void setup() {
        netId = "mayte";
        facultyId = 6L;
        objectMapper = new ObjectMapper();
    }


    @Test
    public void newAdminTest() throws Exception {
        //Arrange
        when(mockAuthenticationManager.getNetId()).thenReturn("admin");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("admin");
        //Act
        ResultActions result = mockMvc.perform(get("/newUser")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        //Assert
        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("User (admin) was added as a Sysadmin.");
    }

    @Test
    public void newEmployeeTest() throws Exception {
        //Arrange
        when(mockAuthenticationManager.getNetId()).thenReturn("employee");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("employee");
        //Act
        ResultActions result = mockMvc.perform(get("/newUser")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        //Assert
        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("User (employee) was added as an Employee.");
    }

    @Test
    public void assignFacultyTest() throws Exception {
        //Arrange
        when(mockAuthenticationManager.getNetId()).thenReturn("admin");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("admin");
        when(facultyVerificationService.verifyFaculty(anyLong(), anyString())).thenReturn(true);
        when(mockAuthorizationManager.isOfType(anyString(), any())).thenReturn(true);
        when(employeeRepository.findByNetId(netId)).thenReturn(Optional.of(new Employee(netId)));

        //Act
        ResultActions result = mockMvc.perform(post("/hireEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(objectMapper.writeValueAsString(
                        new FacultyAssignmentRequestModel(netId, String.valueOf(facultyId)))));

        //Assert
        result.andExpect(status().isOk());

        verify(employeeRepository).saveAndFlush(new Employee(netId, Set.of(6L)));
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
        when(mockAuthorizationManager.isOfType(anyString(), any())).thenReturn(true);
        when(employeeRepository.findByNetId(netId)).thenReturn(Optional.of(new Employee(netId)));

        //Act
        ResultActions result = mockMvc.perform(post("/hireEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(objectMapper.writeValueAsString(
                        new FacultyAssignmentRequestModel(netId, facultyId + ", " + String.valueOf(7L)))));

        //Assert
        result.andExpect(status().isOk());

        verify(employeeRepository, times(2)).saveAndFlush(new Employee(netId, Set.of(6L, 7L)));
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
        when(mockAuthorizationManager.isOfType(anyString(), any())).thenReturn(true);
        when(employeeRepository.findByNetId(netId)).thenReturn(Optional.of(new Employee(netId, Set.of(facultyId))));

        //Act
        ResultActions result = mockMvc.perform(post("/terminateEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(objectMapper.writeValueAsString(
                        new FacultyAssignmentRequestModel(netId, String.valueOf(facultyId)))));

        //Assert
        result.andExpect(status().isOk());

        verify(employeeRepository).saveAndFlush(new Employee(netId, Set.of()));
        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("User (mayte) was removed from faculty: [6]");
    }
}
