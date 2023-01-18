package nl.tudelft.sem.template.requests.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.Calendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RequestHandlerTest {
    @Autowired
    private transient RegistrationService registrationService;

    @Autowired
    private transient RequestRepository requestRepository;

    @MockBean
    private transient ResourcePoolService mockResourcePoolService;

    @Autowired
    private transient RequestHandler requestHandler;

    String description;
    Resources resources;
    String owner;
    String facultyName;
    Resources availableResources;
    Calendar deadline;
    Resources freePoolResources;
    String token;

    int timePeriod;
    boolean isForTomorrow;
    boolean frpHasEnoughResources;
    boolean facHasEnoughResources;

    @BeforeEach
    void setupRegister() {
        description = "give me resources";
        resources = new Resources(50, 50, 50);
        owner = "The Boss";
        facultyName = "CSE";
        availableResources = new Resources(100, 100, 100);
        deadline = Calendar.getInstance();
        freePoolResources = new Resources(75, 75, 75);
        token = "token";
    }

    @Test
    void getPendingResources() throws InvalidResourcesException {
        // Act
        AppRequest returnedRequest = registrationService.registerRequest(description, resources, owner,
                facultyName, availableResources, deadline, freePoolResources, token);
        returnedRequest.setStatus(0);
        requestRepository.save(returnedRequest);
        AppRequest notReturnedRequest1 = registrationService.registerRequest("don't return because of status",
                resources, owner, facultyName, availableResources, deadline, freePoolResources, token);
        notReturnedRequest1.setStatus(1);
        requestRepository.save(notReturnedRequest1);
        AppRequest notReturnedRequest2 = registrationService.registerRequest("don't return because of facultyName",
                resources, owner, "other", availableResources, deadline, freePoolResources, token);
        notReturnedRequest2.setStatus(0);
        requestRepository.save(notReturnedRequest2);
        ArrayList<AppRequest> expected = new ArrayList<>();
        expected.add(returnedRequest);

        // Assert
        assertEquals(expected, requestHandler.getPendingRequestsForFacultyName(facultyName));
        assertFalse(requestHandler.getPendingRequestsForFacultyName(facultyName).contains(notReturnedRequest1));
        assertFalse(requestHandler.getPendingRequestsForFacultyName(facultyName).contains(notReturnedRequest2));
    }
}
