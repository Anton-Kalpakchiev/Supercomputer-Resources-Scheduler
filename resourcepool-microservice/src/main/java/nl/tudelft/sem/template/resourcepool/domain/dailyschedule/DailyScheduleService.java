package nl.tudelft.sem.template.resourcepool.domain.dailyschedule;

import java.util.Calendar;
import nl.tudelft.sem.template.resourcepool.domain.RequestService;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.ResourcePool;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.RpManagementService;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
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

    public void saveDailyScheduleInit(DailySchedule dailySchedule) throws Exception {
        ResourcePool resourcePool = rpManagementService.findById(dailySchedule.getResourcePoolId()).get();
        Resources resources = Resources.add(resourcePool.getNodeResources(), resourcePool.getBaseResources());
        dailySchedule.setAvailableResources(resources);
        dailySchedule.setTotalResources(resources);
        repo.save(dailySchedule);
    }

    public void updateResources(DailySchedule dailySchedule, long requestId) {
        requestService.getRequestedResourcesById()
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
        updateResources(dailySchedule, requestId);
        repo.save(dailySchedule);
    }

    /**
     * Updates the available resources.
     *
     * @param resourcePoolId the resource pool id
     * @param requestId the request that is scheduled
     * @throws Exception if something fails
     */
    public void updateResources(long resourcePoolId, long requestId) throws Exception {
        //        if(!repo.existsById(resourcePoolId)) {
        //            throw new Exception();
        //        }

        System.out.println("Didnt update resources but that's okay");

    }
}
