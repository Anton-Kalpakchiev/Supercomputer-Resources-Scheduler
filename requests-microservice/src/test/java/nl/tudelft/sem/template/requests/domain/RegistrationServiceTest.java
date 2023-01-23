package nl.tudelft.sem.template.requests.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
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

    private transient RegistrationService registrationServiceWithMock;
    private transient RegistrationService registrationServiceWithoutMock;

    @Autowired
    private transient RequestRepository requestRepository;

    @Mock
    private transient RequestRepository mockRequestRepository;

    @MockBean
    private transient ResourcePoolService mockResourcePoolService;

    @Autowired
    private transient RequestHandler requestHandler;

    @Autowired
    private transient RequestChecker requestChecker;

    @Captor
    ArgumentCaptor<Calendar> tomorrowCaptor;

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
        registrationServiceWithMock = new RegistrationService(mockRequestRepository,
                mockResourcePoolService, requestHandler, requestChecker);
        registrationServiceWithoutMock = new RegistrationService(requestRepository,
                mockResourcePoolService, requestHandler, requestChecker);
    }

    @Test
    public void createRequest_withValidData_worksCorrectly() throws InvalidResourcesException {
        // Arrange

        when(mockResourcePoolService.approval(
                any(Calendar.class), any(long.class), eq(false), any(String.class))).thenReturn(
                ResponseEntity.ok(true));

        // Act
        AppRequest returnedRequest = registrationServiceWithoutMock.registerRequest(description, resources, owner,
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
        resources = new Resources(50, 50, -1);

        // Act and Assert
        assertThrows(InvalidResourcesException.class, () -> {
            registrationServiceWithoutMock.registerRequest(description, resources, owner,
                    facultyName, availableResources, deadline, freePoolResources, token);
        });
    }

    @Test
    public void createRequest_withZeroMemory_throwsException() {
        // Arrange
        resources = new Resources(50, 50, 0);

        // Act and Assert
        assertDoesNotThrow(() -> {
            registrationServiceWithoutMock.registerRequest(description, resources, owner,
                    facultyName, availableResources, deadline, freePoolResources, token);
        });
    }

    @Test
    public void createRequest_withNegativeCpu_throwsException() {
        // Arrange
        resources = new Resources(-1, 50, 50);

        // Act and Assert
        assertThrows(InvalidResourcesException.class, () -> {
            AppRequest returnedRequest = registrationServiceWithoutMock.registerRequest(description, resources, owner,
                    facultyName, availableResources, deadline, freePoolResources, token);
        });
    }

    @Test
    public void createRequest_withZeroCpu_throwsException() {
        // Arrange
        resources = new Resources(0, 0, 50);

        // Act and Assert
        assertDoesNotThrow(() -> {
            AppRequest returnedRequest = registrationServiceWithoutMock.registerRequest(description, resources, owner,
                    facultyName, availableResources, deadline, freePoolResources, token);
        });
    }

    @Test
    public void createRequest_withNegativeGpu_throwsException() {
        // Arrange
        final Resources resources = new Resources(50, -1, 50);

        // Act and Assert
        assertThrows(InvalidResourcesException.class, () -> {
            AppRequest returnedRequest = registrationServiceWithoutMock.registerRequest(description, resources, owner,
                    facultyName, availableResources, deadline, freePoolResources, token);
        });
    }

    @Test
    public void createRequest_withZeroGpu_throwsException() {
        // Arrange
        final Resources resources = new Resources(50, 0, 50);

        // Act and Assert
        assertDoesNotThrow(() -> {
            AppRequest returnedRequest = registrationServiceWithoutMock.registerRequest(description, resources, owner,
                    facultyName, availableResources, deadline, freePoolResources, token);
        });
    }

    @Test
    public void createRequest_withInsufficientCpu_throwsException() {
        // Arrange
        resources = new Resources(49, 50, 50);

        // Act and Assert
        assertThrows(InvalidResourcesException.class, () -> {
            AppRequest returnedRequest = registrationServiceWithoutMock.registerRequest(description, resources, owner,
                    facultyName, availableResources, deadline, freePoolResources, token);
        });
    }

    @Test
    public void processRequestInPeriodOneNormalFlow() {
        Calendar deadline = Calendar.getInstance();
        deadline.set(Calendar.HOUR_OF_DAY, 23);
        deadline.set(Calendar.MINUTE, 59);
        deadline.set(Calendar.SECOND, 59);
        deadline.set(Calendar.MILLISECOND, 999);

        AppRequest request4 = new AppRequest("Request4", new Resources(50, 50, 50),
                "me", "math", deadline, 3);
        when(mockResourcePoolService.getFacultyResourcesById(anyLong(), anyString()))
                .thenReturn(new Resources(150, 150, 150));

        assertThat(registrationServiceWithMock.processRequestInPeriodOne(request4, "token")).isEqualTo(request4);


        verify(mockResourcePoolService, times(1)).getFacultyResourcesById(1L, "token");
        verify(mockResourcePoolService, times(1)).approval(tomorrowCaptor.capture(),
                eq(request4.getId()), eq(true), eq("token"));

        Calendar received = tomorrowCaptor.getValue();

        //Check that it is equal to tomorrow.
        assertThat(received.get(Calendar.DAY_OF_MONTH))
                .isEqualTo(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 1);
    }

    @Test
    public void processRequestInPeriodOneNotAccepted() {
        Calendar deadline = Calendar.getInstance();
        deadline.set(Calendar.HOUR_OF_DAY, 23);
        deadline.set(Calendar.MINUTE, 59);
        deadline.set(Calendar.SECOND, 59);
        deadline.set(Calendar.MILLISECOND, 999);

        AppRequest request4 = new AppRequest("Request4", new Resources(50, 50, 50),
                "me", "math", deadline, 3);
        when(mockResourcePoolService.getFacultyResourcesById(anyLong(), anyString()))
                .thenReturn(new Resources(49, 49, 49));

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);

        assertThat(registrationServiceWithMock.processRequestInPeriodOne(request4, "token")).isEqualTo(request4);

        //request is still pending
        assertThat(request4.getId()).isEqualTo(0L);

        verify(mockResourcePoolService, never()).approval(any(),
                eq(request4.getId()), anyBoolean(), eq("token"));
    }

    @Test
    public void processAllPendingNormalFlow() {
        Calendar deadline = Calendar.getInstance();
        deadline.set(Calendar.HOUR_OF_DAY, 23);
        deadline.set(Calendar.MINUTE, 59);
        deadline.set(Calendar.SECOND, 59);
        deadline.set(Calendar.MILLISECOND, 999);

        AppRequest request1 = new AppRequest("Request1", new Resources(1, 1, 1),
                "me", "math", deadline, 1);
        AppRequest request2 = new AppRequest("Request2", new Resources(1, 1, 1),
                "me", "math", deadline, 0);
        AppRequest request3 = new AppRequest("Request3", new Resources(1, 1, 1),
                "me", "math", deadline, 2);
        AppRequest request4 = new AppRequest("Request4", new Resources(50, 50, 50),
                "me", "math", deadline, 3);
        AppRequest request5 = new AppRequest("Request5", new Resources(50, 50, 50),
                "me", "math", deadline, 3);
        AppRequest request6 = new AppRequest("Request6", new Resources(50, 50, 50),
                "me", "math", deadline, 3);

        List<AppRequest> currRequests = new ArrayList<>(List.of(request1, request2, request3, request4, request5, request6));
        when(mockRequestRepository.findAll()).thenReturn(currRequests);
        when(mockResourcePoolService.getFacultyResourcesById(anyLong(), any())).thenReturn(new Resources(150, 150, 150));

        registrationServiceWithMock.processAllPendingRequests();

        //stay the same
        assertEquals(1, request1.getStatus());
        assertEquals(0, request2.getStatus());
        assertEquals(2, request3.getStatus());

        //accepted
        assertEquals(1, request4.getStatus());
        assertEquals(1, request5.getStatus());
        assertEquals(1, request6.getStatus());
    }

    @Test
    public void hasEnoughResourcesNotEnoughCpu() {
        Resources available = new Resources(49, 50, 50);
        assertFalse(registrationServiceWithoutMock.hasEnoughResources(available, resources));
    }

    @Test
    public void hasEnoughResourcesNotEnoughGpu() {
        Resources available = new Resources(50, 49, 50);
        assertFalse(registrationServiceWithoutMock.hasEnoughResources(available, resources));
    }

    @Test
    public void hasEnoughResourcesNotEnoughMem() {
        Resources available = new Resources(50, 50, 49);
        assertFalse(registrationServiceWithoutMock.hasEnoughResources(available, resources));
    }

    @Test
    public void hasEnoughResourcesNormalFlow() {
        Resources available = new Resources(50, 50, 50);
        assertTrue(registrationServiceWithoutMock.hasEnoughResources(available, resources));
    }

    @Test
    void getTimePeriodTest() {

        Calendar periodZeroBorderDown = Calendar.getInstance();
        periodZeroBorderDown.set(Calendar.HOUR_OF_DAY, 0);
        periodZeroBorderDown.set(Calendar.MINUTE, 0);
        periodZeroBorderDown.set(Calendar.SECOND, 1);
        assertEquals(0, registrationServiceWithoutMock.getTimePeriod(periodZeroBorderDown));

        Calendar periodZeroBorderUp = Calendar.getInstance();
        periodZeroBorderUp.set(Calendar.HOUR_OF_DAY, 17);
        periodZeroBorderUp.set(Calendar.MINUTE, 59);
        periodZeroBorderUp.set(Calendar.SECOND, 0);
        assertEquals(0, registrationServiceWithoutMock.getTimePeriod(periodZeroBorderUp));

        Calendar periodOneBorderDown = Calendar.getInstance();
        periodOneBorderDown.set(Calendar.HOUR_OF_DAY, 18);
        periodOneBorderDown.set(Calendar.MINUTE, 0);
        periodOneBorderDown.set(Calendar.SECOND, 1);
        assertEquals(1, registrationServiceWithoutMock.getTimePeriod(periodOneBorderDown)); //fails

        Calendar periodOneBorderUp = Calendar.getInstance();
        periodOneBorderUp.set(Calendar.HOUR_OF_DAY, 23);
        periodOneBorderUp.set(Calendar.MINUTE, 54);
        periodOneBorderUp.set(Calendar.SECOND, 0);
        assertEquals(1, registrationServiceWithoutMock.getTimePeriod(periodOneBorderUp));

        Calendar periodTwoBorderDown = Calendar.getInstance();
        periodTwoBorderDown.set(Calendar.HOUR_OF_DAY, 23);
        periodTwoBorderDown.set(Calendar.MINUTE, 55);
        periodTwoBorderDown.set(Calendar.SECOND, 1);
        assertEquals(2, registrationServiceWithoutMock.getTimePeriod(periodTwoBorderDown));

        Calendar periodTwoBorderUp = Calendar.getInstance();
        periodTwoBorderUp.set(Calendar.HOUR_OF_DAY, 23);
        periodTwoBorderUp.set(Calendar.MINUTE, 59);
        periodTwoBorderUp.set(Calendar.SECOND, 0);
        assertEquals(2, registrationServiceWithoutMock.getTimePeriod(periodTwoBorderUp));
    }

    @Test
    void processPendingNormalFlow() {

    }

    @Test
    void isForTomorrowTrue() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.set(Calendar.DAY_OF_MONTH, tomorrow.get(Calendar.DAY_OF_MONTH) + 1);
        assertTrue(registrationServiceWithoutMock.isForTomorrow(tomorrow));
    }

    @Test
    void isForTomorrowAtStart() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.set(Calendar.DAY_OF_MONTH, tomorrow.get(Calendar.DAY_OF_MONTH) + 1);
        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);
        tomorrow.set(Calendar.MILLISECOND, 0);
        assertTrue(registrationServiceWithoutMock.isForTomorrow(tomorrow));
    }

    @Test
    void isForTomorrowAtDeadline() {
        Calendar tomorrow = Calendar.getInstance();

        tomorrow.set(Calendar.DAY_OF_MONTH, tomorrow.get(Calendar.DAY_OF_MONTH) + 1);
        tomorrow.set(Calendar.HOUR_OF_DAY, 23);
        tomorrow.set(Calendar.MINUTE, 59);
        tomorrow.set(Calendar.SECOND, 59);
        tomorrow.set(Calendar.MILLISECOND, 999);

        assertTrue(registrationServiceWithoutMock.isForTomorrow(tomorrow));
    }

    @Test
    void isNotForTomorrow() {
        Calendar beforeTomorrow = Calendar.getInstance();
        beforeTomorrow.set(Calendar.HOUR_OF_DAY, 23);
        beforeTomorrow.set(Calendar.MINUTE, 59);
        beforeTomorrow.set(Calendar.SECOND, 59);
        beforeTomorrow.set(Calendar.MILLISECOND, 999);
        assertFalse(registrationServiceWithoutMock.isForTomorrow(beforeTomorrow));
    }

    @Test
    void isAfterTomorrow() {
        Calendar afterTomorrow = Calendar.getInstance();

        afterTomorrow.set(Calendar.HOUR_OF_DAY, 0);
        afterTomorrow.set(Calendar.MINUTE, 0);
        afterTomorrow.set(Calendar.SECOND, 0);
        afterTomorrow.set(Calendar.MILLISECOND, 0);
        afterTomorrow.set(Calendar.DAY_OF_MONTH, afterTomorrow.get(Calendar.DAY_OF_MONTH) + 2);
        assertFalse(registrationServiceWithoutMock.isForTomorrow(afterTomorrow));
    }

    @Test
    void getResourcesForId() throws InvalidResourcesException {
        // Act
        AppRequest returnedRequest = registrationServiceWithoutMock.registerRequest(description, resources, owner,
            facultyName, availableResources, deadline, freePoolResources, token);

        // Assert
        assertEquals(resources, requestHandler.getResourcesForId(returnedRequest.getId()));
    }




}