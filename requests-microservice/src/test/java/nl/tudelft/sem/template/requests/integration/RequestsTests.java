package nl.tudelft.sem.template.requests.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Calendar;
import java.util.Optional;
import nl.tudelft.sem.template.requests.authentication.AuthManager;
import nl.tudelft.sem.template.requests.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.requests.controllers.RequestController;
import nl.tudelft.sem.template.requests.domain.AppRequest;
import nl.tudelft.sem.template.requests.domain.InvalidResourcesException;
import nl.tudelft.sem.template.requests.domain.RegistrationService;
import nl.tudelft.sem.template.requests.domain.RequestHandler;
import nl.tudelft.sem.template.requests.domain.RequestRepository;
import nl.tudelft.sem.template.requests.domain.Resources;
import nl.tudelft.sem.template.requests.integration.utils.JsonUtil;
import nl.tudelft.sem.template.requests.models.RegistrationRequestModel;
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
import org.springframework.web.util.NestedServletException;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class RequestsTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Mock
    private transient RequestRepository requestRepository;

    @Mock
    private transient RegistrationService mockRegistrationService;

    @Mock
    private transient RequestHandler mockRequestHandler;

    @MockBean
    private transient RequestController mockRequestController;

    @Test
    public void register_withValidData_worksCorrectly() throws Exception {
        // Arrange
        final String description = "give me resources";
        final Resources resources = new Resources(50, 30, 50);
        final String owner = "User";
        final String facultyName = "CSE";
        final String deadline = "01-01-2023";
        final Resources facultyResources = new Resources(100, 100, 100);

        AppRequest expected = new AppRequest(description, resources, owner,
                facultyName, Calendar.getInstance(), -1);

        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockRequestHandler.getResourcesForId(anyLong())).thenReturn(facultyResources);
        when(requestRepository.findById(0L)).thenReturn(Optional.of(expected));

        RegistrationRequestModel model = new RegistrationRequestModel();
        model.setDescription(description);
        model.setMemory(resources.getMemory());
        model.setCpu(resources.getCpu());
        model.setGpu(resources.getGpu());
        model.setFacultyName(facultyName);
        model.setDeadline(deadline);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isOk());

        AppRequest savedRequest = requestRepository.findById(0L).orElseThrow();

        assertThat(savedRequest.getDescription()).isEqualTo(description);
        assertThat(savedRequest.getMem()).isEqualTo(resources.getMemory());
        assertThat(savedRequest.getCpu()).isEqualTo(resources.getCpu());
        assertThat(savedRequest.getGpu()).isEqualTo(resources.getGpu());
        assertThat(savedRequest.getOwner()).isEqualTo(owner);
        assertThat(savedRequest.getFacultyName()).isEqualTo(facultyName);
    }

    @Test
    public void register_withNegativeResources_throwsException() throws Exception {
        // Arrange
        final String description = "give me resources";
        final Resources resources = new Resources(-50, -50, -50);
        final String facultyName = "CSE";
        final String deadline = "01-01-2023";

        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockRequestController.register(any(), any())).thenThrow(new InvalidResourcesException(
                "Resource object cannot be created with negative inputs"));


        RegistrationRequestModel model = new RegistrationRequestModel();
        model.setDescription(description);
        model.setMemory(resources.getMemory());
        model.setCpu(resources.getCpu());
        model.setGpu(resources.getGpu());
        model.setFacultyName(facultyName);
        model.setDeadline(deadline);

        // Act and Assert
        assertThrows(NestedServletException.class, () -> {
            ResultActions resultActions = mockMvc.perform(post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(model)));
        });
    }

    @Test
    public void register_withInsufficientCpu_throwsException() throws Exception {
        // Arrange
        final String description = "give me resources";
        final Resources resources = new Resources(49, 50, 50);
        final String facultyName = "CSE";
        final String deadline = "01-01-2023";

        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockRequestController.register(any(), any())).thenThrow(new InvalidResourcesException(
                "Resource object must provide at least the same amount of CPU as GPU"));

        RegistrationRequestModel model = new RegistrationRequestModel();
        model.setDescription(description);
        model.setMemory(resources.getMemory());
        model.setCpu(resources.getCpu());
        model.setGpu(resources.getGpu());
        model.setFacultyName(facultyName);
        model.setDeadline(deadline);

        // Act and Assert
        assertThrows(NestedServletException.class, () -> {
            ResultActions resultActions = mockMvc.perform(post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(model)));
        });
    }
}
