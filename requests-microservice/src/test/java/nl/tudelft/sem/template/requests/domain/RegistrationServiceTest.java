package nl.tudelft.sem.template.requests.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RegistrationServiceTest {

    @Autowired
    private transient RegistrationService registrationService;

    @Autowired
    private transient RequestRepository requestRepository;

    @Test
    public void createRequest_withValidData_worksCorrectly() throws Exception {
        // Arrange
        final String description = "give me resources";
        final Resources resources = new Resources(30, 50, 50);
        final String owner = "The Boss";

        // Act
        registrationService.registerRequest(description, resources, owner);

        // Assert
        AppRequest savedRequest = requestRepository.findById(0L).orElseThrow();

        assertThat(savedRequest.getDescription()).isEqualTo(description);
        assertThat(savedRequest.getMem()).isEqualTo(resources.getMem());
        assertThat(savedRequest.getCpu()).isEqualTo(resources.getCpu());
        assertThat(savedRequest.getGpu()).isEqualTo(resources.getGpu());
        assertThat(savedRequest.getOwner()).isEqualTo(owner);
    }
}