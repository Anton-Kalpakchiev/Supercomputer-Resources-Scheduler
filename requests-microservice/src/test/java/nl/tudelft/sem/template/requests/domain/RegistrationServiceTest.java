package nl.tudelft.sem.template.requests.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
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

    @MockBean
    private transient ResourcePoolService mockResourcePoolService;

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
    public void createRequest_withValidData_worksCorrectly() throws InvalidResourcesException {
        // Arrange

        when(mockResourcePoolService.approval(
                any(Calendar.class), any(long.class), any(String.class))).thenReturn(
                ResponseEntity.ok(true));

        // Act
        AppRequest returnedRequest = registrationService.registerRequest(description, resources, owner,
                facultyName, availableResources, deadline, freePoolResources, token);

        // Assert
        AppRequest savedRequest = requestRepository.findById(returnedRequest.getId()).orElseThrow();

        assertThat(savedRequest.getDescription()).isEqualTo(description);
        assertThat(savedRequest.getMem()).isEqualTo(resources.getMemory());
        assertThat(savedRequest.getCpu()).isEqualTo(resources.getCpu());
        assertThat(savedRequest.getGpu()).isEqualTo(resources.getGpu());
        assertThat(savedRequest.getOwner()).isEqualTo(owner);
        assertThat(savedRequest.getFacultyName()).isEqualTo(facultyName);
        assertThat(savedRequest.getDeadline()).isEqualTo(deadline);
    }

    @Test
    public void createRequest_withNegativeMemory_throwsException() {
        // Arrange
        resources = new Resources(50, 50, -50);

        // Act and Assert
        assertThrows(InvalidResourcesException.class, () -> {
            registrationService.registerRequest(description, resources, owner,
                    facultyName, availableResources, deadline, freePoolResources, token);
        });
    }

    @Test
    public void createRequest_withNegativeCpu_throwsException() {
        // Arrange
        resources = new Resources(-50, 50, 50);

        // Act and Assert
        assertThrows(InvalidResourcesException.class, () -> {
            AppRequest returnedRequest = registrationService.registerRequest(description, resources, owner,
                    facultyName, availableResources, deadline, freePoolResources, token);
        });
    }

    @Test
    public void createRequest_withNegativeGpu_throwsException() {
        // Arrange
        final Resources resources = new Resources(50, -50, 50);

        // Act and Assert
        assertThrows(InvalidResourcesException.class, () -> {
            AppRequest returnedRequest = registrationService.registerRequest(description, resources, owner,
                    facultyName, availableResources, deadline, freePoolResources, token);
        });
    }

    @Test
    public void createRequest_withInsufficientCpu_throwsException() {
        // Arrange
        resources = new Resources(49, 50, 50);

        // Act and Assert
        assertThrows(InvalidResourcesException.class, () -> {
            AppRequest returnedRequest = registrationService.registerRequest(description, resources, owner,
                    facultyName, availableResources, deadline, freePoolResources, token);
        });
    }


    @Test
    void getTimePeriodTest() {

        Calendar periodZeroBorderDown = Calendar.getInstance();
        periodZeroBorderDown.set(Calendar.HOUR_OF_DAY, 0);
        periodZeroBorderDown.set(Calendar.MINUTE, 0);
        periodZeroBorderDown.set(Calendar.SECOND, 1);
        assertEquals(0, registrationService.getTimePeriod(periodZeroBorderDown));

        Calendar periodZeroBorderUp = Calendar.getInstance();
        periodZeroBorderUp.set(Calendar.HOUR_OF_DAY, 17);
        periodZeroBorderUp.set(Calendar.MINUTE, 59);
        periodZeroBorderUp.set(Calendar.SECOND, 0);
        assertEquals(0, registrationService.getTimePeriod(periodZeroBorderUp));

        Calendar periodOneBorderDown = Calendar.getInstance();
        periodOneBorderDown.set(Calendar.HOUR_OF_DAY, 18);
        periodOneBorderDown.set(Calendar.MINUTE, 0);
        periodOneBorderDown.set(Calendar.SECOND, 1);
        assertEquals(1, registrationService.getTimePeriod(periodOneBorderDown)); //fails

        Calendar periodOneBorderUp = Calendar.getInstance();
        periodOneBorderUp.set(Calendar.HOUR_OF_DAY, 23);
        periodOneBorderUp.set(Calendar.MINUTE, 54);
        periodOneBorderUp.set(Calendar.SECOND, 0);
        assertEquals(1, registrationService.getTimePeriod(periodOneBorderUp));

        Calendar periodTwoBorderDown = Calendar.getInstance();
        periodTwoBorderDown.set(Calendar.HOUR_OF_DAY, 23);
        periodTwoBorderDown.set(Calendar.MINUTE, 55);
        periodTwoBorderDown.set(Calendar.SECOND, 1);
        assertEquals(2, registrationService.getTimePeriod(periodTwoBorderDown));

        Calendar periodTwoBorderUp = Calendar.getInstance();
        periodTwoBorderUp.set(Calendar.HOUR_OF_DAY, 23);
        periodTwoBorderUp.set(Calendar.MINUTE, 59);
        periodTwoBorderUp.set(Calendar.SECOND, 0);
        assertEquals(2, registrationService.getTimePeriod(periodTwoBorderUp));
    }

    @Test
    void isForTomorrowTest() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.set(Calendar.DAY_OF_MONTH, tomorrow.get(Calendar.DAY_OF_MONTH) + 1);
        assertTrue(registrationService.isForTomorrow(tomorrow));
        tomorrow.set(Calendar.HOUR_OF_DAY, 23);
        tomorrow.set(Calendar.MINUTE, 59);
        tomorrow.set(Calendar.SECOND, 59);

        assertTrue(registrationService.isForTomorrow(tomorrow));

        Calendar beforeTomorrow = Calendar.getInstance();
        beforeTomorrow.set(Calendar.HOUR_OF_DAY, 23);
        beforeTomorrow.set(Calendar.MINUTE, 59);
        beforeTomorrow.set(Calendar.SECOND, 59);
        assertFalse(registrationService.isForTomorrow(beforeTomorrow));

        Calendar afterTomorrow = Calendar.getInstance();

        afterTomorrow.set(Calendar.HOUR_OF_DAY, 0);
        afterTomorrow.set(Calendar.MINUTE, 0);
        afterTomorrow.set(Calendar.SECOND, 0);
        afterTomorrow.set(Calendar.DAY_OF_MONTH, afterTomorrow.get(Calendar.DAY_OF_MONTH) + 2);
        assertFalse(registrationService.isForTomorrow(afterTomorrow));
    }

    @Test
    void decideStatusZero() {
        timePeriod = 0;
        isForTomorrow = new Random().nextInt(2) == 1;
        frpHasEnoughResources = false;
        facHasEnoughResources = true;

        assertEquals(0, registrationService.decideStatusOfRequest(timePeriod, isForTomorrow,
                frpHasEnoughResources, facHasEnoughResources));

    }

    @Test
    void decideStatusOne() {
        timePeriod = 1;
        isForTomorrow = new Random().nextInt(2) == 1;
        frpHasEnoughResources = true;
        facHasEnoughResources = new Random().nextInt(2) == 1;

        assertEquals(1, registrationService.decideStatusOfRequest(timePeriod, isForTomorrow,
                frpHasEnoughResources, facHasEnoughResources));

        timePeriod = 0;
        isForTomorrow = true;
        frpHasEnoughResources = true;
        facHasEnoughResources = false;

        assertEquals(1, registrationService.decideStatusOfRequest(timePeriod, isForTomorrow,
                frpHasEnoughResources, facHasEnoughResources));
    }

    @Test
    void decideStatusTwo() {
        timePeriod = 2;
        isForTomorrow = true;
        frpHasEnoughResources = true;
        facHasEnoughResources = true;

        assertEquals(2, registrationService.decideStatusOfRequest(timePeriod, isForTomorrow,
                frpHasEnoughResources, facHasEnoughResources));

        timePeriod = 1;
        isForTomorrow = true;
        frpHasEnoughResources = false;
        facHasEnoughResources = true;

        assertEquals(2, registrationService.decideStatusOfRequest(timePeriod, isForTomorrow,
                frpHasEnoughResources, facHasEnoughResources));
    }

    @Test
    void decideStatusThree() {
        timePeriod = 0;
        isForTomorrow = new Random().nextInt(2) == 1;
        frpHasEnoughResources = false;
        facHasEnoughResources = false;

        assertEquals(3, registrationService.decideStatusOfRequest(timePeriod, isForTomorrow,
                frpHasEnoughResources, facHasEnoughResources));
    }


}