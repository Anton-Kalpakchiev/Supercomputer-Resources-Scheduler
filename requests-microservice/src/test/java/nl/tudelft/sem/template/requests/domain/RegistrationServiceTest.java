package nl.tudelft.sem.template.requests.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
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

//    @Test
//    public void createRequest_withValidData_worksCorrectly() throws Exception {
//        // Arrange
//        final String description = "give me resources";
//        final Resources resources = new Resources(30, 50, 50);
//        final String owner = "The Boss";
//        final String facultyName = "CSE";
//        final Resources availableResources = new Resources(100, 100, 100);
//        final Calendar deadline = Calendar.getInstance();
//        final Resources freePoolResources = new Resources(75, 75, 75);
//
//        // Act
//        AppRequest returnedRequest = registrationService.registerRequest(description, resources, owner,
//                facultyName, availableResources, deadline, freePoolResources);
//
//        // Assert
//        AppRequest savedRequest = requestRepository.findById(returnedRequest.getId()).orElseThrow();
//
//        assertThat(savedRequest.getDescription()).isEqualTo(description);
//        assertThat(savedRequest.getMem()).isEqualTo(resources.getMemory());
//        assertThat(savedRequest.getCpu()).isEqualTo(resources.getCpu());
//        assertThat(savedRequest.getGpu()).isEqualTo(resources.getGpu());
//        assertThat(savedRequest.getOwner()).isEqualTo(owner);
//        assertThat(savedRequest.getFacultyName()).isEqualTo(facultyName);
//        assertThat(savedRequest.getDeadline()).isEqualTo(deadline);
//    }
}