package nl.tudelft.sem.template.requests.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.Random;
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
public class RequestCheckerTest {
    @Autowired
    private transient RequestChecker requestChecker;

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
    void decideStatusZero() {
        timePeriod = 0;
        isForTomorrow = new Random().nextInt(2) == 1;
        frpHasEnoughResources = false;
        facHasEnoughResources = true;

        assertEquals(0, requestChecker.decideStatusOfRequest(timePeriod, isForTomorrow,
                frpHasEnoughResources, facHasEnoughResources));

    }

    @Test
    void decideStatusOne() {
        timePeriod = 1;
        isForTomorrow = new Random().nextInt(2) == 1;
        frpHasEnoughResources = true;
        facHasEnoughResources = new Random().nextInt(2) == 1;

        assertEquals(1, requestChecker.decideStatusOfRequest(timePeriod, isForTomorrow,
                frpHasEnoughResources, facHasEnoughResources));

        timePeriod = 0;
        isForTomorrow = true;
        frpHasEnoughResources = true;
        facHasEnoughResources = false;

        assertEquals(1, requestChecker.decideStatusOfRequest(timePeriod, isForTomorrow,
                frpHasEnoughResources, facHasEnoughResources));
    }

    @Test
    void decideStatusTwo() {
        timePeriod = 2;
        isForTomorrow = true;
        frpHasEnoughResources = true;
        facHasEnoughResources = true;

        assertEquals(2, requestChecker.decideStatusOfRequest(timePeriod, isForTomorrow,
                frpHasEnoughResources, facHasEnoughResources));

        timePeriod = 1;
        isForTomorrow = true;
        frpHasEnoughResources = false;
        facHasEnoughResources = true;

        assertEquals(2, requestChecker.decideStatusOfRequest(timePeriod, isForTomorrow,
                frpHasEnoughResources, facHasEnoughResources));
    }

    @Test
    void decideStatusThree() {
        timePeriod = 0;
        isForTomorrow = new Random().nextInt(2) == 1;
        frpHasEnoughResources = false;
        facHasEnoughResources = false;

        assertEquals(3, requestChecker.decideStatusOfRequest(timePeriod, isForTomorrow,
                frpHasEnoughResources, facHasEnoughResources));
    }
}
