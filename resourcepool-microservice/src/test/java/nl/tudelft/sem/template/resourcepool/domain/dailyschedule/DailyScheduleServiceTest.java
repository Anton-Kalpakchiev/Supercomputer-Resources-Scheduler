package nl.tudelft.sem.template.resourcepool.domain.dailyschedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Optional;
import nl.tudelft.sem.template.resourcepool.domain.RequestService;
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

    private long resourcePoolId;

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

    @Test
    void testReleaseResourcesSuccessful() throws Exception {
        DailySchedule dailySchedule = new DailySchedule(day, resourcePoolId);
        dailySchedule.setTotalResources(new Resources(100, 100, 100));
        dailySchedule.setAvailableResources(new Resources(70, 70, 70));
        DailySchedule fpSchedule = new DailySchedule(day, 1L);

        when(mockScheduleRepository.existsByDayAndResourcePoolId(day, 1)).thenReturn(true);
        when(mockScheduleRepository.existsByDayAndResourcePoolId(day, resourcePoolId)).thenReturn(true);
        when(mockScheduleRepository.findByDayAndResourcePoolId(day, resourcePoolId)).thenReturn(Optional.of(dailySchedule));
        when(mockScheduleRepository.findByDayAndResourcePoolId(day, 1L)).thenReturn(Optional.of(fpSchedule));

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
}
