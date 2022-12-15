package nl.tudelft.sem.template.resourcepool.domain.dailyschedule;

import nl.tudelft.sem.template.resourcepool.controllers.RequestService;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.RpFacultyRepository;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.RpManagementService;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import org.springframework.stereotype.Service;

import java.util.Calendar;

/**
 * A DDD service for managing the resource pools.
 */
@Service
public class DailyScheduleService {
    private final transient ScheduleRepository repo;
    private final transient RpManagementService rpManagementService;
    private final transient RequestService requestService;

    /**
     * Instantiates a new DailyScheduleService.
     *
     * @param repo the ScheduleRepository repository
     */
    public DailyScheduleService(ScheduleRepository repo, RpManagementService rpManagementService, RequestService requestService) {
        this.repo = repo;
        this.rpManagementService = rpManagementService;
        this.requestService = requestService;
    }

    public void scheduleFP(Calendar day, long requestId, String token) throws Exception {
        DailyScheduleId id = new DailyScheduleId( day, 1);
        if(!repo.existsById(id)) {
            DailySchedule toSave = new DailySchedule(day, 1);
            repo.save(toSave);
        }
        DailySchedule dailySchedule = repo.findByDayAndResourcePoolId(day, 1).get();
        dailySchedule.addRequest(requestId);
        rpManagementService.updateResources(1, requestId);
        repo.save(dailySchedule);
    }
}
