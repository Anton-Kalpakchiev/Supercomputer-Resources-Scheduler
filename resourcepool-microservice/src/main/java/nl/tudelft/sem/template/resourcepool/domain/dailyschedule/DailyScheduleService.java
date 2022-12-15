package nl.tudelft.sem.template.resourcepool.domain.dailyschedule;

import java.util.Calendar;
import nl.tudelft.sem.template.resourcepool.controllers.RequestService;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.RpManagementService;
import org.springframework.stereotype.Service;

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
    public DailyScheduleService(ScheduleRepository repo, RpManagementService rpManagementService,
                                RequestService requestService) {
        this.repo = repo;
        this.rpManagementService = rpManagementService;
        this.requestService = requestService;
    }

    /**
     * Schedules a request in the free pool.
     *
     * @param day the day that the request has to be scheduled on
     * @param requestId the id of the request
     * @param token the jwtToken
     * @throws Exception if something fails
     */
    public void scheduleFp(Calendar day, long requestId, String token) throws Exception {
        DailyScheduleId id = new DailyScheduleId(day, 1);
        if (!repo.existsById(id)) {
            DailySchedule toSave = new DailySchedule(day, 1);
            repo.save(toSave);
        }
        DailySchedule dailySchedule = repo.findByDayAndResourcePoolId(day, 1).get();
        dailySchedule.addRequest(requestId);
        rpManagementService.updateResources(1, requestId);
        repo.save(dailySchedule);
    }
}
