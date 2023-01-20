package nl.tudelft.sem.template.resourcepool.domain.dailyschedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nl.tudelft.sem.template.resourcepool.domain.RequestService;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.FacultyNotFoundException;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.ResourcePool;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.RpFacultyRepository;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.RpManagementService;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

public class DailyScheduleServiceTest {

    private ScheduleRepository mockScheduleRepository;

    private DailyScheduleService dailyScheduleService;
    private RpManagementService rpManagementService;
    private RequestService requestService;
    private RpFacultyRepository mockResourcePoolRepo;
    private Calendar day;

    private Calendar day2;
    private long resourcePoolId;
    private ResourcePool faculty1;
    private DailySchedule scheduleFaculty1;
    private DailySchedule scheduleFaculty2;

    @Captor
    private ArgumentCaptor<DailySchedule> argumentCaptor;

    @BeforeEach
    void setup() {
        argumentCaptor = ArgumentCaptor.forClass(DailySchedule.class);
        mockScheduleRepository = mock(ScheduleRepository.class);
        mockResourcePoolRepo = mock(RpFacultyRepository.class);
        rpManagementService = new RpManagementService(mockResourcePoolRepo);
        dailyScheduleService = new DailyScheduleService(mockScheduleRepository,
                rpManagementService, requestService, mockResourcePoolRepo);
        day = Calendar.getInstance();
        resourcePoolId = 6L;
    }

    @BeforeEach
    void setupExampleFaculties() {
        faculty1 = new ResourcePool("Resource Pool 0");
        faculty1.setBaseResources(new Resources(100, 100, 100));
        faculty1.setNodeResources(new Resources(20, 20, 20));

        Calendar dayCopy = Calendar.getInstance();
        dayCopy.setTimeInMillis(0);
        dayCopy.set(Calendar.YEAR, day.get(Calendar.YEAR));
        dayCopy.set(Calendar.MONTH, day.get(Calendar.MONTH));
        dayCopy.set(Calendar.DAY_OF_MONTH, day.get(Calendar.DAY_OF_MONTH));
        day = dayCopy;
        scheduleFaculty1 = new DailySchedule(day, faculty1.getId());
        scheduleFaculty1.addRequest(1);
        scheduleFaculty1.addRequest(2);
        scheduleFaculty1.addRequest(3);
        scheduleFaculty1.setTotalResources(new Resources(120, 120, 120));
        scheduleFaculty1.setAvailableResources(new Resources(0, 0, 0));

        Calendar dayCopy2 = Calendar.getInstance();
        dayCopy2.setTimeInMillis(0);
        dayCopy2.set(Calendar.YEAR, day.get(Calendar.YEAR));
        dayCopy2.set(Calendar.MONTH, day.get(Calendar.MONTH));
        dayCopy2.set(Calendar.DAY_OF_MONTH, day.get(Calendar.DAY_OF_MONTH));
        dayCopy2.add(Calendar.DATE, 1);
        day2 = dayCopy2;

        scheduleFaculty2 = new DailySchedule(day, faculty1.getId());
        scheduleFaculty2.addRequest(1);
        scheduleFaculty2.addRequest(2);
        scheduleFaculty2.setTotalResources(new Resources(50, 50, 50));
        scheduleFaculty2.setAvailableResources(new Resources(10, 10, 10));

    }

    @Test
    void testReleaseResourcesSuccessful() throws Exception {
        DailySchedule dailySchedule = new DailySchedule(day, resourcePoolId);
        dailySchedule.setTotalResources(new Resources(100, 100, 100));
        dailySchedule.setAvailableResources(new Resources(70, 70, 70));
        ResourcePool rp = new ResourcePool();
        rp.setNodeResources(new Resources(0, 0, 0));
        rp.setBaseResources(new Resources(0, 0, 0));
        DailySchedule fpSchedule = new DailySchedule(day, 1L);

        when(mockScheduleRepository.existsByDayAndResourcePoolId(day, 1)).thenReturn(true);
        when(mockScheduleRepository.existsByDayAndResourcePoolId(day, resourcePoolId)).thenReturn(true);
        when(mockScheduleRepository.findByDayAndResourcePoolId(day, resourcePoolId)).thenReturn(Optional.of(dailySchedule));
        when(mockScheduleRepository.findByDayAndResourcePoolId(day, 1L)).thenReturn(Optional.of(fpSchedule));
        when(mockResourcePoolRepo.findById(1L)).thenReturn(Optional.of(rp));
        when(mockResourcePoolRepo.findById(resourcePoolId)).thenReturn(Optional.of(rp));

        dailyScheduleService.releaseResources(day, resourcePoolId);
        verify(mockScheduleRepository, times(2)).save(argumentCaptor.capture());
        DailySchedule expectedDailySchedule = argumentCaptor.getAllValues().get(0);
        DailySchedule expectedFpSchedule = argumentCaptor.getAllValues().get(1);

        assertThat(expectedDailySchedule.getAvailableResources()).isEqualTo(new Resources(0, 0, 0));
        assertThat(expectedDailySchedule.getTotalResources()).isEqualTo(new Resources(100, 100, 100));

        assertThat(expectedFpSchedule.getAvailableResources()).isEqualTo(new Resources(70, 70, 70));
        assertThat(expectedFpSchedule.getTotalResources()).isEqualTo(new Resources(70, 70, 70));
    }

    @Test
    void testReleaseResourcesFp() {
        assertThrows(ReleaseResourcesException.class, () -> dailyScheduleService.releaseResources(day, 1L));
    }

    @Test
    void generateScheduleResponseContentOneFaculty() throws FacultyNotFoundException {
        Set<Long> faculties = Set.of(faculty1.getId());
        when(mockResourcePoolRepo.findById(faculty1.getId())).thenReturn(Optional.of(faculty1));
        when(mockScheduleRepository.findAllByResourcePoolId(faculty1.getId())).thenReturn(List.of(scheduleFaculty1));

        Map<String, List<String>> result = dailyScheduleService.generateScheduleResponseContent(faculties);
        assertThat(result.keySet()).isEqualTo(Set.of("Resource Pool 0"));
        assertThat(result.get(faculty1.getName())).isEqualTo(List.of(scheduleFaculty1.toPrettyString()));
    }

    @Test
    void generateScheduleResponseExceptionTest() {
        Set<Long> faculties = Set.of(faculty1.getId());
        when(mockResourcePoolRepo.findById(faculty1.getId())).thenReturn(Optional.empty());

        assertThrows(FacultyNotFoundException.class, () -> dailyScheduleService.generateScheduleResponseContent(faculties));
    }

    @Test
    void getAllSchedulesPerFacultyIdTest() {
        when(mockScheduleRepository.findAllByResourcePoolId(faculty1.getId())).thenReturn(List.of(scheduleFaculty1));
        List<DailySchedule> result = dailyScheduleService.getAllSchedulesPerFacultyId(faculty1.getId());
        assertThat(List.of(scheduleFaculty2)).isEqualTo(result);
    }

    @Test
    void getSchedulesPerFacultyTest() throws FacultyNotFoundException {
        when(mockResourcePoolRepo.findById(faculty1.getId())).thenReturn(Optional.of(faculty1));
        when(mockScheduleRepository.findAllByResourcePoolId(faculty1.getId())).thenReturn(List.of(scheduleFaculty1));

        Map<String, List<String>> expected = new HashMap<>();
        expected.put(faculty1.getName(), List.of(scheduleFaculty1.toPrettyString()));
        Map<String, List<String>> result = dailyScheduleService.getSchedulesPerFaculty(faculty1.getId());
        assertThat(expected).isEqualTo(result);
    }

    @Test
    void getAllSchedulesTest() throws FacultyNotFoundException {
        when(mockResourcePoolRepo.findAll()).thenReturn(List.of(faculty1));
        when(mockResourcePoolRepo.findById(faculty1.getId())).thenReturn(Optional.of(faculty1));
        when(mockScheduleRepository.findAllByResourcePoolId(faculty1.getId())).thenReturn(List.of(scheduleFaculty1));

        Map<String, List<String>> expected = new HashMap<>();
        expected.put(faculty1.getName(), List.of(scheduleFaculty1.toPrettyString()));
        Map<String, List<String>> result = dailyScheduleService.getAllSchedules();
        assertThat(expected).isEqualTo(result);
    }

    @Test
    void getAvailableResourcesTomorrowTest() throws Exception {
        when(mockResourcePoolRepo.findAll()).thenReturn(List.of(faculty1));
        when(mockResourcePoolRepo.findById(faculty1.getId())).thenReturn(Optional.of(faculty1));
        when(mockScheduleRepository.existsByDayAndResourcePoolId(day2, faculty1.getId()))
                .thenReturn(true);
        when(mockScheduleRepository.findByDayAndResourcePoolId(day2, faculty1.getId()))
                .thenReturn(Optional.of(scheduleFaculty2));


        assertThat(dailyScheduleService.getAvailableResourcesById(faculty1.getId(), day2)).isEqualTo(
                new Resources(10, 10, 10));
    }

    @Test
    void getAvailableResourcesTomorrowException() {
        when(mockResourcePoolRepo.findAll()).thenReturn(List.of(faculty1));
        when(mockResourcePoolRepo.findById(faculty1.getId())).thenReturn(Optional.of(faculty1));
        when(mockScheduleRepository.findByDayAndResourcePoolId(day2, faculty1.getId()))
                .thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> dailyScheduleService.getAvailableResourcesById(faculty1.getId(), day2));
    }

    @Test
    void releaseAllResourcesToFreePoolTest() {
        DailySchedule dailySchedule6 = new DailySchedule(day, resourcePoolId);
        dailySchedule6.setTotalResources(new Resources(100, 100, 100));
        dailySchedule6.setAvailableResources(new Resources(70, 70, 70));
        DailySchedule dailySchedule7 = new DailySchedule(day, 7L);
        dailySchedule7.setTotalResources(new Resources(100, 100, 100));
        dailySchedule7.setAvailableResources(new Resources(70, 70, 70));

        ResourcePool rp1 = new ResourcePool();
        rp1.setId(1L);
        rp1.setNodeResources(new Resources(0, 0, 0));
        rp1.setBaseResources(new Resources(0, 0, 0));
        ResourcePool rp6 = new ResourcePool();
        rp6.setId(6L);
        rp6.setNodeResources(new Resources(0, 0, 0));
        rp6.setBaseResources(new Resources(0, 0, 0));
        ResourcePool rp7 = new ResourcePool();
        rp7.setNodeResources(new Resources(0, 0, 0));
        rp7.setBaseResources(new Resources(0, 0, 0));
        rp7.setId(7L);
        DailySchedule fpSchedule = new DailySchedule(day, 1L);

        when(mockScheduleRepository.existsByDayAndResourcePoolId(day, 1L)).thenReturn(true);
        when(mockScheduleRepository.existsByDayAndResourcePoolId(day, resourcePoolId)).thenReturn(true);
        when(mockScheduleRepository.existsByDayAndResourcePoolId(day, 7L)).thenReturn(true);
        when(mockScheduleRepository.findByDayAndResourcePoolId(day, 1L)).thenReturn(Optional.of(fpSchedule));
        when(mockScheduleRepository.findByDayAndResourcePoolId(day, resourcePoolId)).thenReturn(Optional.of(dailySchedule6));
        when(mockScheduleRepository.findByDayAndResourcePoolId(day, 7L)).thenReturn(Optional.of(dailySchedule7));
        when(mockResourcePoolRepo.findById(1L)).thenReturn(Optional.of(rp1));
        when(mockResourcePoolRepo.findById(resourcePoolId)).thenReturn(Optional.of(rp6));
        when(mockResourcePoolRepo.findById(7L)).thenReturn(Optional.of(rp7));
        when(rpManagementService.findById(dailySchedule6.getResourcePoolId())).thenReturn(Optional.of(rp6));
        when(rpManagementService.findById(dailySchedule6.getResourcePoolId())).thenReturn(Optional.of(rp7));
        when(mockResourcePoolRepo.findAll()).thenReturn(List.of(rp1, rp6, rp7));

        dailyScheduleService.releaseAllResourcesToFreePool();

        verify(mockScheduleRepository, times(4)).save(argumentCaptor.capture());
        DailySchedule expectedFpSchedule = argumentCaptor.getAllValues().get(1);
        DailySchedule expectedDailySchedule6 = argumentCaptor.getAllValues().get(0);
        DailySchedule expectedDailySchedule7 = argumentCaptor.getAllValues().get(2);

        assertThat(expectedFpSchedule.getAvailableResources()).isEqualTo(new Resources(140, 140, 140));
        assertThat(expectedFpSchedule.getTotalResources()).isEqualTo(new Resources(140, 140, 140));

        assertThat(expectedDailySchedule6.getAvailableResources()).isEqualTo(new Resources(0, 0, 0));
        assertThat(expectedDailySchedule6.getTotalResources()).isEqualTo(new Resources(100, 100, 100));

        assertThat(expectedDailySchedule7.getAvailableResources()).isEqualTo(new Resources(0, 0, 0));
        assertThat(expectedDailySchedule7.getTotalResources()).isEqualTo(new Resources(100, 100, 100));

        ReleaseResourcesException exc = assertThrows(ReleaseResourcesException.class,
            () -> dailyScheduleService.releaseAllResourcesToFreePoolMutated());
        assertThat(exc.getMessage()).isEqualTo("The free resource pool cannot release resources!");

    }
}
