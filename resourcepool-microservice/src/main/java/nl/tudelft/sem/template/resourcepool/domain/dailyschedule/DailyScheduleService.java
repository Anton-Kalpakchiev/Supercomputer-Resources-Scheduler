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
     * Sets the resources of the daily schedule upon initializing.
     *
     * @param dailySchedule the daily schedule of which the resources need to be set
     * @throws Exception if the saving doesn't work
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
     * @throws IOException if something isn't formatted the right way
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
            // Proper exception implemented in different branches
            throw new Exception("Resource pool note found");
        }
    }


    /**
     * Releases resources of a faculty for a given day into the free resource pool of that day.
     *
     * @param day the day of the schedule
     * @param resourcePoolId the
     * @throws Exception when daily schedule cannot be initialized.
     */
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public void releaseResources(Calendar day, long resourcePoolId) throws Exception {
        if (resourcePoolId == 1L) {
            throw new ReleaseResourcesException("The free resource pool cannot release resources!");
        }
        // Create resource pool daily schedule for that day if it does not exist
        if (!repo.existsByDayAndResourcePoolId(day, 1)) {
            DailySchedule toSave = new DailySchedule(day, 1);
            saveDailyScheduleInit(toSave);
        }
        // Instantiate daily schedule of the provided resource pool at the given day if it does not exist yet
        if (!repo.existsByDayAndResourcePoolId(day, resourcePoolId)) {
            DailySchedule newSchedule = new DailySchedule(day, resourcePoolId);
            saveDailyScheduleInit(newSchedule);
        }

        // Retrieve the free pool schedule
        DailySchedule freePoolSchedule = repo.findByDayAndResourcePoolId(day, 1).get();

        // Retrieve the resource pool schedule
        DailySchedule dailySchedule = repo.findByDayAndResourcePoolId(day, resourcePoolId).get();

        // Reset the available resources for that day in that faculty to 0
        Resources leftOverResources = dailySchedule.getAvailableResources();
        dailySchedule.setAvailableResources(new Resources(0, 0, 0));
        repo.save(dailySchedule);

        // Add the leftover resources in the faculty to the free resource pool
        Resources availableInFreePool = freePoolSchedule.getAvailableResources();
        Resources totalInFreePool = freePoolSchedule.getAvailableResources();
        freePoolSchedule.setAvailableResources(Resources.add(availableInFreePool, leftOverResources));
        freePoolSchedule.setTotalResources(Resources.add(totalInFreePool, leftOverResources));
        repo.save(freePoolSchedule);
    }
}
