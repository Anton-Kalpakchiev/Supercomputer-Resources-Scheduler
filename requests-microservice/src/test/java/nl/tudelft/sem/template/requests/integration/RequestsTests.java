package nl.tudelft.sem.template.requests.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.NoSuchElementException;
import nl.tudelft.sem.template.requests.authentication.AuthManager;
import nl.tudelft.sem.template.requests.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.requests.domain.AppRequest;
import nl.tudelft.sem.template.requests.domain.RequestRepository;
import nl.tudelft.sem.template.requests.domain.Resources;
import nl.tudelft.sem.template.requests.integration.utils.JsonUtil;
import nl.tudelft.sem.template.requests.models.RegistrationRequestModel;
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
    @Autowired
    private transient RequestRepository requestRepository;

//    @Test
//    public void register_withValidData_worksCorrectly() throws Exception {
//        // Arrange
//        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
//        final String description = "give me resources";
//        final Resources resources = new Resources(30, 50, 50);
//
//        RegistrationRequestModel model = new RegistrationRequestModel();
//        model.setDescription(description);
//        model.setMem(resources.getMem());
//        model.setCpu(resources.getCpu());
//        model.setGpu(resources.getGpu());
//
//        // Act
//        ResultActions resultActions = mockMvc.perform(post("/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", "Bearer MockedToken")
//                .content(JsonUtil.serialize(model)));
//
//        // Assert
//        resultActions.andExpect(status().isOk());
//
//        AppRequest savedRequest = requestRepository.findById(0L).orElseThrow();
//
//        assertThat(savedRequest.getDescription()).isEqualTo(description);
//        assertThat(savedRequest.getMem()).isEqualTo(resources.getMem());
//        assertThat(savedRequest.getCpu()).isEqualTo(resources.getCpu());
//        assertThat(savedRequest.getGpu()).isEqualTo(resources.getGpu());
//    }

    @Test
    public void register_withNegativeResources_throwsException() throws Exception {
        // Arrange
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        final String description = "give me resources";

        RegistrationRequestModel model = new RegistrationRequestModel();
        model.setDescription(description);
        model.setMemory(-1);
        model.setCpu(-2);
        model.setGpu(-3);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isBadRequest());

        assertThrows(NoSuchElementException.class, () -> {
            AppRequest savedRequest = requestRepository.findById(0L).orElseThrow();
        });
    }

    @Test
    public void register_withNotEnoughCpu_throwsException() throws Exception {
        // Arrange
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        final String description = "give me resources";

        RegistrationRequestModel model = new RegistrationRequestModel();
        model.setDescription(description);
        model.setMemory(60);
        model.setCpu(99);
        model.setGpu(100);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isBadRequest());

        assertThrows(NoSuchElementException.class, () -> {
            AppRequest savedRequest = requestRepository.findById(0L).orElseThrow();
        });
    }
}
