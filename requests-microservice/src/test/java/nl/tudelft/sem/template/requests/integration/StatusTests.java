package nl.tudelft.sem.template.requests.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Calendar;
import java.util.NoSuchElementException;

import nl.tudelft.sem.template.requests.authentication.AuthManager;
import nl.tudelft.sem.template.requests.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.requests.domain.AppRequest;
import nl.tudelft.sem.template.requests.domain.RequestRepository;
import nl.tudelft.sem.template.requests.domain.Resources;
import nl.tudelft.sem.template.requests.integration.utils.JsonUtil;
import nl.tudelft.sem.template.requests.models.SetStatusModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
public class StatusTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient RequestRepository requestRepository;

    @Test
    public void getStatus_withValidData_worksCorrectly() throws Exception {
        // Arrange
        final String description = "give me resources";
        final Resources resources = new Resources(50, 30, 50);
        final String owner = "User";
        final String facultyName = "CSE";
        final Calendar deadline = Calendar.getInstance();

        AppRequest appRequest = new AppRequest(description, resources, owner, facultyName, deadline, 1);
        AppRequest savedRequest = requestRepository.save(appRequest);
        final long requestId = savedRequest.getId();

        doReturn(true).when(mockJwtTokenVerifier).validateToken(anyString());
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/getStatus")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestId)));

        // Assert
        resultActions.andExpect(status().isOk());
        assertThat(requestRepository.findById(requestId).get().getStatus() == 0);
    }

    @Test
    public void getStatus_withInvalidData_throwsException() throws Exception {
        // Arrange
        final String description = "give me resources";
        final Resources resources = new Resources(50, 30, 50);
        final String owner = "User";
        final String facultyName = "CSE";
        final Calendar deadline = Calendar.getInstance();

        AppRequest appRequest = new AppRequest(description, resources, owner, facultyName, deadline, 1);
        AppRequest savedRequest = requestRepository.save(appRequest);
        final long requestId = savedRequest.getId();

        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);

        // Act and Assert

        assertThrows(NestedServletException.class, () -> {
            ResultActions resultActions = mockMvc.perform(post("/getStatus")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(requestId + 1)));
        });
    }

    @Test
    public void setStatus_withValidData_worksCorrectly() throws Exception {
        // Arrange
        final String description = "give me resources";
        final Resources resources = new Resources(50, 30, 50);
        final String owner = "User";
        final String facultyName = "CSE";
        final Calendar deadline = Calendar.getInstance();

        AppRequest appRequest = new AppRequest(description, resources, owner, facultyName, deadline, 1);
        AppRequest savedRequest = requestRepository.save(appRequest);
        final long requestId = savedRequest.getId();

        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);

        SetStatusModel model = new SetStatusModel();
        model.setId(requestId);
        model.setStatus(3);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/status")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isOk());
        assertThat(requestRepository.findById(requestId).get().getStatus() == 3);
    }

    @Test
    public void setStatus_withInvalidData_worksCorrectly() {
        // Arrange
        final String description = "give me resources";
        final Resources resources = new Resources(50, 30, 50);
        final String owner = "User";
        final String facultyName = "CSE";
        final Calendar deadline = Calendar.getInstance();

        AppRequest appRequest = new AppRequest(description, resources, owner, facultyName, deadline, 1);
        AppRequest savedRequest = requestRepository.save(appRequest);
        final long requestId = savedRequest.getId();

        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);

        SetStatusModel model = new SetStatusModel();
        model.setId(requestId + 1);
        model.setStatus(3);

        // Act and Assert

        assertThrows(NoSuchElementException.class, () -> {
            ResultActions resultActions = mockMvc.perform(post("/status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(requestId + 1)));
            int status = requestRepository.findById(requestId + 1).get().getStatus();
        });
    }
}
