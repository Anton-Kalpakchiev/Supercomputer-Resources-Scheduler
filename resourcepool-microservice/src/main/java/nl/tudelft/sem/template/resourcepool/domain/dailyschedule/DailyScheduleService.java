package nl.tudelft.sem.template.resourcepool.domain.dailyschedule;

import java.io.IOException;
import java.util.Calendar;
import nl.tudelft.sem.template.resourcepool.domain.RequestService;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.ResourcePool;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.RpManagementService;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import org.springframework.stereotype.Service;

/**
 * A DDD service for managing the daily schedules.
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
     * Sets the resources of the daily schedule upon initializing
     *
     * @param dailySchedule the daily schedule of which the resources need to be set
     * @throws Exception
     */
    public void saveDailyScheduleInit(DailySchedule dailySchedule) throws Exception {
        ResourcePool resourcePool = rpManagementService.findById(dailySchedule.getResourcePoolId()).get();
        Resources resources = Resources.add(resourcePool.getNodeResources(), resourcePool.getBaseResources());
        dailySchedule.setAvailableResources(resources);
        dailySchedule.setTotalResources(resources);
        repo.save(dailySchedule);
    }

    /**
     * Updates the available resources.
     *
     * @param dailySchedule the daily schedule from which the available resources need to be updated
     * @param requestId the id of the request that is scheduled
     * @param token the jwtToken
     * @throws IOException
     */
    public void updateResources(DailySchedule dailySchedule, long requestId, String token) throws IOException {
        Resources requestedResources = requestService.getRequestedResourcesById(requestId, token);
        dailySchedule.setAvailableResources(Resources.subtract(dailySchedule.getAvailableResources(), requestedResources));
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
            saveDailyScheduleInit(toSave);
            repo.save(toSave);
        }
        DailySchedule dailySchedule = repo.findByDayAndResourcePoolId(day, 1).get();
        dailySchedule.addRequest(requestId);
        updateResources(dailySchedule, requestId, token);
        repo.save(dailySchedule);
    }

    /**
     * Retrieves the available resources of a resource pool.
     *
     * @param resourcePoolId the id of the resource pool
     * @return the available resources
     * @throws Exception thrown when resources were not found
     */
    public Resources getAvailableResourcesById(long resourcePoolId) throws Exception {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        if (repo.findByDayAndResourcePoolId(tomorrow, resourcePoolId).isPresent()) {
            return repo.findByDayAndResourcePoolId(tomorrow, resourcePoolId).get().getAvailableResources();
        } else {
            // Proper exception implemented in different brancehs
            throw new Exception("Resource pool note found");
        }
    }
}
