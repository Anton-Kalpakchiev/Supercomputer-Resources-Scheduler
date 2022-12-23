package nl.tudelft.sem.template.resourcepool.domain.dailyschedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

        day = Calendar.getInstance();
        scheduleFaculty1 = new DailySchedule(day, faculty1.getId());
        scheduleFaculty1.addRequest(1);
        scheduleFaculty1.addRequest(2);
        scheduleFaculty1.addRequest(3);
        scheduleFaculty1.setTotalResources(new Resources(120, 120, 120));
        scheduleFaculty1.setAvailableResources(new Resources(0, 0, 0));

        day2 = Calendar.getInstance();
        day2.add(Calendar.DATE, 1);
        day2.setTime(day2.getTime());

        scheduleFaculty2 = new DailySchedule(day, faculty1.getId());
        scheduleFaculty2.addRequest(1);
        scheduleFaculty2.addRequest(2);
        scheduleFaculty2.setTotalResources(new Resources(50, 50, 50));
        scheduleFaculty2.setAvailableResources(new Resources(10, 10, 10));

    }

    //    @Test
    //    void testReleaseResourcesSuccessful() throws Exception {
    //        DailySchedule dailySchedule = new DailySchedule(day, resourcePoolId);
    //        dailySchedule.setTotalResources(new Resources(100, 100, 100));
    //        dailySchedule.setAvailableResources(new Resources(70, 70, 70));
    //        DailySchedule fpSchedule = new DailySchedule(day, 1L);
    //
    //        when(mockScheduleRepository.existsByDayAndResourcePoolId(day, 1)).thenReturn(true);
    //        when(mockScheduleRepository.existsByDayAndResourcePoolId(day, resourcePoolId)).thenReturn(true);
    //        when(mockScheduleRepository
    //        .findByDayAndResourcePoolId(day, resourcePoolId)).thenReturn(Optional.of(dailySchedule));
    //        when(mockScheduleRepository.findByDayAndResourcePoolId(day, 1L)).thenReturn(Optional.of(fpSchedule));
    //
    //        dailyScheduleService.releaseResources(day, resourcePoolId);
    //        verify(mockScheduleRepository, times(2)).save(argumentCaptor.capture());
    //        DailySchedule expectedDailySchedule = argumentCaptor.getAllValues().get(0);
    //        DailySchedule expectedFpSchedule = argumentCaptor.getAllValues().get(1);
    //
    //        assertThat(expectedDailySchedule.getAvailableResources()).isEqualTo(new Resources(0, 0, 0));
    //        assertThat(expectedDailySchedule.getTotalResources()).isEqualTo(new Resources(100, 100, 100));
    //
    //        assertThat(expectedFpSchedule.getAvailableResources()).isEqualTo(new Resources(70, 70, 70));
    //        assertThat(expectedFpSchedule.getTotalResources()).isEqualTo(new Resources(70, 70, 70));
    //    }

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
}
