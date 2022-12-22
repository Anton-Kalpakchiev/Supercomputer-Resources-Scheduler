package nl.tudelft.sem.template.resourcepool.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.resourcepool.authentication.AuthManager;
import nl.tudelft.sem.template.resourcepool.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.RpFacultyRepository;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.RpManagementService;
import nl.tudelft.sem.template.resourcepool.models.VerifyFacultyRequestModel;
import nl.tudelft.sem.template.resourcepool.models.VerifyFacultyResponseModel;
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
public class ResourcePoolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    private transient RpManagementService rpManagementService;

    @MockBean
    private transient RpFacultyRepository rpFacultyRepository;

    private long facultyId;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        rpManagementService = new RpManagementService(rpFacultyRepository);
        facultyId = 6L;
    }

    @Test
    public void verifyFaculty() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(rpFacultyRepository.existsById(facultyId)).thenReturn(true);

        // Act
        ResultActions result = mockMvc.perform(post("/verifyFaculty")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(objectMapper.writeValueAsString(new VerifyFacultyRequestModel(facultyId))));

        // Assert
        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo(objectMapper.writeValueAsString(new VerifyFacultyResponseModel(true)));
    }

    @Test
    public void verifyFacultyNotFound() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(rpFacultyRepository.existsById(facultyId)).thenReturn(false);

        // Act
        ResultActions result = mockMvc.perform(post("/verifyFaculty")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(objectMapper.writeValueAsString(new VerifyFacultyRequestModel(facultyId))));

        // Assert
        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo(objectMapper.writeValueAsString(new VerifyFacultyResponseModel(false)));
    }
}
