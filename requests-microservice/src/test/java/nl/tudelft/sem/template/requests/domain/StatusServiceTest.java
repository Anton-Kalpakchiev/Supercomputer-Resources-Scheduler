package nl.tudelft.sem.template.requests.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Calendar;
import java.util.NoSuchElementException;
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
class StatusServiceTest {

    @Autowired
    private transient StatusService statusService;
    @Autowired
    private transient RequestRepository requestRepository;

    @Test
    public void getStatusOnExistingRequestTest() throws Exception {
        final String description = "give me resources";
        final Resources resources;

        try {
            resources = new Resources(30, 50, 50);
        } catch (InvalidResourcesException e) {
            throw new RuntimeException(e);
        }

        final String owner = "The Boss";
        final String facultyName = "CSE";
        final Calendar deadline = Calendar.getInstance();
        deadline.set(Calendar.YEAR, 2010);

        AppRequest appRequest = new AppRequest(description, resources, owner, facultyName, deadline, 0);
        AppRequest savedRequest = requestRepository.save(appRequest);
        long requestId = savedRequest.getId();

        int status = statusService.getStatus(requestId);
        assertThat(status == 0);
    }

    @Test
    public void getStatusOnNonExistentRequestTest() throws Exception {
        final String description = "give me resources";
        final Resources resources;

        try {
            resources = new Resources(30, 50, 50);
        } catch (InvalidResourcesException e) {
            throw new RuntimeException(e);
        }

        final String owner = "The Boss";
        final String facultyName = "CSE";
        final Calendar deadline = Calendar.getInstance();
        deadline.set(Calendar.YEAR, 2010);

        AppRequest appRequest = new AppRequest(description, resources, owner, facultyName, deadline, -1);
        AppRequest savedRequest = requestRepository.save(appRequest);
        long requestId = savedRequest.getId();

        assertThrows(NoSuchElementException.class, () -> {
            int status = statusService.getStatus(requestId + 1);
        });
    }

    @Test
    public void setStatusOnExistingRequestTest() throws Exception {
        final String description = "give me resources";
        final Resources resources;

        try {
            resources = new Resources(30, 50, 50);
        } catch (InvalidResourcesException e) {
            throw new RuntimeException(e);
        }

        final String owner = "The Boss";
        final String facultyName = "CSE";
        final Calendar deadline = Calendar.getInstance();
        deadline.set(Calendar.YEAR, 2010);

        AppRequest appRequest = new AppRequest(description, resources, owner, facultyName, deadline, 0);
        AppRequest savedRequest = requestRepository.save(appRequest);
        long requestId = savedRequest.getId();

        statusService.setStatus(requestId, 3);
        int status = statusService.getStatus(requestId);
        assertThat(status == 3);
    }

    @Test
    public void setStatusOnNonExistentRequestTest() throws Exception {
        final String description = "give me resources";
        final Resources resources;

        try {
            resources = new Resources(30, 50, 50);
        } catch (InvalidResourcesException e) {
            throw new RuntimeException(e);
        }

        final String owner = "The Boss";
        final String facultyName = "CSE";
        final Calendar deadline = Calendar.getInstance();
        deadline.set(Calendar.YEAR, 2010);

        AppRequest appRequest = new AppRequest(description, resources, owner, facultyName, deadline, 0);
        AppRequest savedRequest = requestRepository.save(appRequest);
        long requestId = savedRequest.getId();

        assertThrows(NoSuchElementException.class, () -> {
            statusService.setStatus(requestId + 1, 3);
        });
    }
}