package nl.tudelft.sem.template.resourcepool.domain.dailyschedule;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.resourcepool.domain.RequestService;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.FacultyNotFoundException;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.ResourcePool;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.RpFacultyRepository;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.RpManagementService;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * A DDD service for managing the daily schedules.
 */
@Service
public class DailyScheduleService {
    private final transient ScheduleRepository scheduleRepository;
    private final transient RpFacultyRepository resourcePoolRepo;
    private final transient RpManagementService rpManagementService;
    private final transient RequestService requestService;

    /**
     * Instantiates a new DailyScheduleService.
     *
     * @param repo the ScheduleRepository repository
     */
    public DailyScheduleService(ScheduleRepository repo, RpManagementService rpManagementService,
                                RequestService requestService, RpFacultyRepository resourcePoolRepo) {
        this.scheduleRepository = repo;
        this.rpManagementService = rpManagementService;
        this.resourcePoolRepo = resourcePoolRepo;
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
        scheduleRepository.save(dailySchedule);
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
        if (!scheduleRepository.existsById(id)) {
            DailySchedule toSave = new DailySchedule(day, 1);
            saveDailyScheduleInit(toSave);
            scheduleRepository.save(toSave);
        }
        DailySchedule dailySchedule = scheduleRepository.findByDayAndResourcePoolId(day, 1).get();
        dailySchedule.addRequest(requestId);
        updateResources(dailySchedule, requestId, token);
        scheduleRepository.save(dailySchedule);
    }

    public void scheduleFaculty(Calendar day, long requestId, String facultyName, String token) throws Exception {
        long facultyId = resourcePoolRepo.findByName(facultyName).get().getId();
        DailyScheduleId id = new DailyScheduleId(day, facultyId); //change to not create per every new request
        if (!scheduleRepository.existsById(id)) {
            DailySchedule toSave = new DailySchedule(day, facultyId);
            saveDailyScheduleInit(toSave);
            scheduleRepository.save(toSave);
        }
        DailySchedule dailySchedule = scheduleRepository.findByDayAndResourcePoolId(day, facultyId).get();
        dailySchedule.addRequest(requestId);
        updateResources(dailySchedule, requestId, token);
        scheduleRepository.save(dailySchedule);
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
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        Optional<DailySchedule> optional = scheduleRepository.findByDayAndResourcePoolId(tomorrow, resourcePoolId);
        if (optional.isPresent()) {
            return optional.get().getAvailableResources();
        } else {
            DailySchedule toSave = new DailySchedule(tomorrow, resourcePoolId);
            saveDailyScheduleInit(toSave);
            scheduleRepository.save(toSave);
            return toSave.getAvailableResources();
        }
    }

    /**
     * Gets all schedules in the repository.
     *
     * @return a map of faculty names and strings of schedules.
     */
    public Map<String, List<String>> getAllSchedules() throws FacultyNotFoundException {
        Set<Long> faculties = resourcePoolRepo.findAll().stream().map(ResourcePool::getId).collect(Collectors.toSet());
        return generateScheduleResponseContent(faculties);
    }

    /**
     * Gets all schedules for a given faculty and puts it in the correct format.
     *
     * @param facultyId the faculty to fetch the schedules for.
     * @return a map of faculty names and strings of schedules.
     */
    public Map<String, List<String>> getSchedulesPerFaculty(long facultyId) throws FacultyNotFoundException {
        return generateScheduleResponseContent(Set.of(facultyId));
    }

    /**
     * Gets all schedules for a given faculty.
     *
     * @param facultyId the faculty to retrieve the schedules for
     * @return the schedules of the faculty
     */
    public List<DailySchedule> getAllSchedulesPerFacultyId(long facultyId) {
        return scheduleRepository.findAllByResourcePoolId(facultyId);
    }


    /**
     * Fetches the daily schedules of the faculties and puts it in the correct format.
     *
     * @param faculties the list of faculties to fetch schedules for
     * @return a map of faculty names and
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public Map<String, List<String>> generateScheduleResponseContent(Set<Long> faculties) throws FacultyNotFoundException {
        Map<String, List<String>> allSchedulesMap = new HashMap<>();
        for (long faculty : faculties) {
            if (resourcePoolRepo.findById(faculty).isPresent()) {
                String facultyName = resourcePoolRepo.findById(faculty).get().getName();
                List<String> dailySchedules = getAllSchedulesPerFacultyId(faculty).stream()
                        .map(DailySchedule::toPrettyString).collect(Collectors.toList());
                allSchedulesMap.put(facultyName, dailySchedules);
            } else {
                throw new FacultyNotFoundException("Faculty was not found");
            }
        }
        return allSchedulesMap;
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
        if (!scheduleRepository.existsByDayAndResourcePoolId(day, 1)) {
            DailySchedule toSave = new DailySchedule(day, 1);
            saveDailyScheduleInit(toSave);
        }
        // Instantiate daily schedule of the provided resource pool at the given day if it does not exist yet
        if (!scheduleRepository.existsByDayAndResourcePoolId(day, resourcePoolId)) {
            DailySchedule newSchedule = new DailySchedule(day, resourcePoolId);
            saveDailyScheduleInit(newSchedule);
        }

        // Retrieve the free pool schedule
        DailySchedule freePoolSchedule = scheduleRepository.findByDayAndResourcePoolId(day, 1).get();

        // Retrieve the resource pool schedule
        DailySchedule dailySchedule = scheduleRepository.findByDayAndResourcePoolId(day, resourcePoolId).get();

        // Reset the available resources for that day in that faculty to 0
        Resources leftOverResources = dailySchedule.getAvailableResources();
        dailySchedule.setAvailableResources(new Resources(0, 0, 0));
        scheduleRepository.save(dailySchedule);

        // Add the leftover resources in the faculty to the free resource pool
        Resources availableInFreePool = freePoolSchedule.getAvailableResources();
        Resources totalInFreePool = freePoolSchedule.getAvailableResources();
        freePoolSchedule.setAvailableResources(Resources.add(availableInFreePool, leftOverResources));
        freePoolSchedule.setTotalResources(Resources.add(totalInFreePool, leftOverResources));
        scheduleRepository.save(freePoolSchedule);
    }

    /**
     * At 18PM every day, all faculties release their resources to the free resource pool.
     */
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    @Scheduled(cron = "0 0 18 * * *")
    public void releaseAllResourcesToFreePool() {
        List<Long> allIds = resourcePoolRepo.findAll().stream().map(x -> x.getId()).collect(Collectors.toList());
        for (long thisId : allIds) {
            if (thisId != 1L) {
                Calendar day = Calendar.getInstance();
                try {
                    releaseResources(day, thisId);
                } catch (Exception e) { //should never occur
                    e.printStackTrace();
                }
            }
        }
    }
}
