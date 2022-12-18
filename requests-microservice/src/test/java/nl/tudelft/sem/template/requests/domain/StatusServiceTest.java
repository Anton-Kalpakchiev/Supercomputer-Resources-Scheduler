package nl.tudelft.sem.template.requests.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setup() {
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

        AppRequest appRequest = new AppRequest(description, resources, owner, facultyName, deadline, 0);
        Optional<AppRequest> res = Optional.of(appRequest);
        requestRepository = mock(RequestRepository.class);
        when(requestRepository.findById(0L)).thenReturn(res);
    }

    // TODO: Fix Test
    /*@Test
    public void getStatusOnExistingRequestTest() throws Exception {
        int status = statusService.getStatus(0L);
        assertThat(status == 0);
    }*/

    @Test
    public void getStatusOnNonExistentRequestTest() throws Exception {
        assertThrows(NoSuchElementException.class, () -> {
            int status = statusService.getStatus(1L);
        });
    }

    // TODO: Fix Test
    /* @Test
    public void setStatusOnExistingRequestTest() throws Exception {
        statusService.setStatus(0L, 3);
        int status = statusService.getStatus(0L);
        assertThat(status == 3);
    }
*/
    @Test
    public void setStatusOnNonExistentRequestTest() throws Exception {
        assertThrows(NoSuchElementException.class, () -> {
            statusService.setStatus(1L, 3);
        });
    }
}