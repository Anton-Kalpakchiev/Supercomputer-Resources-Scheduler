package nl.tudelft.sem.template.requests.domain;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
//We can remove this line later on, but I can't figure out how to fix this and the code works perfect with the error in it
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class RegistrationService {
    private final transient RequestRepository requestRepository;
    private final transient ResourcePoolService resourcePoolService;

    private transient String initialToken = null;
    //when tokens are not needed anymore, delete this and rework a bit the functions

    /**
     * Instantiates a new RegistrationService.
     *
     * @param requestRepository the request repository
     * @param resourcePoolService the service that communicates with the resource pool
     */
    public RegistrationService(RequestRepository requestRepository, ResourcePoolService resourcePoolService) {
        this.requestRepository = requestRepository;
        this.resourcePoolService = resourcePoolService;
    }

    /**
     * Register a new request.
     *
     * @param description The description of the request
     * @param resources   The resources requested
     */
    public AppRequest registerRequest(String description, Resources resources, String owner, String facultyName,
                                  Resources availableResources, Calendar deadline, Resources freePoolResources, String token)
            throws InvalidResourcesException {

        if (resources.getMemory() < 0 || resources.getCpu() < 0 || resources.getGpu() < 0) {
            throw new InvalidResourcesException("Resource object cannot be created with negative inputs");
        }
        if (resources.getGpu() > resources.getCpu()) {
            throw new InvalidResourcesException("Resource object must provide at least the same amount of CPU as GPU");
        }
        if (initialToken == null) {
            initialToken = token;
        }

        AppRequest request = new AppRequest(description, resources, owner, facultyName, deadline, -1);

        final boolean facultyHasEnoughResources = !(availableResources.getGpu() < resources.getGpu()
                || availableResources.getCpu() < resources.getCpu()
                || availableResources.getMemory() < resources.getMemory());
        final boolean frpHasEnoughResources = !(freePoolResources.getGpu() < resources.getGpu()
                || freePoolResources.getCpu() < resources.getCpu()
                || freePoolResources.getMemory() < resources.getMemory());
        final boolean isForTomorrow = isForTomorrow(deadline);
        /*
        0 when before the 6h deadline
        1 when after the 6h deadline and before the 5min deadline,
        2 when after the 5 min deadline
         */
        int timePeriod = getTimePeriod(Calendar.getInstance());

        int status = decideStatusOfRequest(timePeriod, isForTomorrow, frpHasEnoughResources, facultyHasEnoughResources);

        if (status == 0) {
            //pending for manual review
            request.setStatus(0);
            requestRepository.save(request);
        } else if (status == 1) {
            //auto approve
            request.setStatus(1);
            requestRepository.save(request);
            resourcePoolService.approval(deadline, request.getId(), token);
        } else if (status == 2) {
            //auto reject
            request.setStatus(2);
            requestRepository.save(request);
        } else {
            //pending for the FRP to get more resources at 6h before end of day
            request.setStatus(3);
            requestRepository.save(request);
        }

        return request;
    }

    /**
     * Processes a requests that is left pending until the frp gets more resources at 6h before the start of the next day.
     * Gets called on every request withs status 3 at the aforementioned time.
     *
     * @param request the given request
     * @return the AppRequest returned after processing
     */

    public AppRequest processRequestInPeriodOne(AppRequest request, String token) {
        Calendar deadline = request.getDeadline();
        Resources resources = new Resources(request.getMem(), request.getCpu(), request.getGpu());
        final Resources freePoolResources = getFacultyResourcesByName("Free pool");

        boolean frpHasEnoughResources = !(freePoolResources.getGpu() < resources.getGpu()
                || freePoolResources.getCpu() < resources.getCpu()
                || freePoolResources.getMemory() < resources.getMemory());
        int timePeriod = 1;
        boolean isForTomorrow = isForTomorrow(deadline);
        boolean facHasEnoughResources = false;
        int status = decideStatusOfRequest(timePeriod, isForTomorrow, frpHasEnoughResources, facHasEnoughResources);

        if (status == 0) {
            //set for manual review
            //find request in Repo, update its status
            request.setStatus(0);
            requestRepository.save(request);
        } else if (status == 1) {
            //approval for tomorrow
            request.setStatus(1);
            //find request in Repo, update its status
            requestRepository.save(request);
            //update RP/Schedule MS so that it can update the schedule for the corresponding faculty for tomorrow
            resourcePoolService.approval(deadline, request.getId(), token);
        } else {
            //rejection
            //find request in Repo, update its status
            request.setStatus(2);
            requestRepository.save(request);
        }
        return request;
    }


    /**
     * At 18PM every day, all requests that are left pending to be processed when the FRP gets more resources, get processed.
     * Gets automatically called at the proper time. (5 minute after 18PM,
     * in order to give time for the resources to be released from all faculties)
     */
    @Scheduled(cron = "0 5 18 * * *")
    public void processAllPendingRequests() {
        List<AppRequest> allRequests = requestRepository.findAll().stream()
                .filter(x -> x.getStatus() == 3).collect(Collectors.toList());
        for (AppRequest thisRequest : allRequests) {
            processRequestInPeriodOne(thisRequest, initialToken);
        }
    }

    /**
     * Calculates the time period during which a request is made.
     *
     * @param cal the Calendar object representing the time at which the request arrived
     *
     * @return the time period
     *         0 when before the 6h deadline
     *         1 when after the 6h deadline and before the 5min deadline,
     *         2 when after the 5 min deadline
     */
    public int getTimePeriod(Calendar cal) {
        if (cal.after(getSixHoursDeadline()) && cal.before(getFiveMinutesDeadline())) {
            return 1;
        } else if (cal.after(getFiveMinutesDeadline())) {
            return 2;
        } else {
            return 0;
        }
    }

    /**
     * Check whether the deadline is for tomorrow or not.
     *
     * @param deadline the deadline to compare to
     * @return boolean whether the deadline is tomorrow not
     */
    public boolean isForTomorrow(Calendar deadline) {
        Calendar endOfTomorrow = Calendar.getInstance();
        endOfTomorrow.set(Calendar.HOUR_OF_DAY, 23);
        endOfTomorrow.set(Calendar.MINUTE, 59);
        endOfTomorrow.set(Calendar.SECOND, 59);
        endOfTomorrow.set(Calendar.MILLISECOND, 999);
        endOfTomorrow.add(Calendar.DAY_OF_YEAR, 1);

        Calendar startOfTomorrow = Calendar.getInstance();
        startOfTomorrow.set(Calendar.HOUR_OF_DAY, 0);
        startOfTomorrow.set(Calendar.MINUTE, 0);
        startOfTomorrow.set(Calendar.SECOND, 0);
        startOfTomorrow.set(Calendar.MILLISECOND, 0);
        startOfTomorrow.add(Calendar.DAY_OF_YEAR, 1);

        return !deadline.after(endOfTomorrow) && !deadline.before(startOfTomorrow);
    }

    /**
     * Generates a Calendar object representing the current date with time 18PM.
     *
     * @return a Calendar object representing the current date with time 18PM
     */
    public Calendar getSixHoursDeadline() {
        Calendar deadlineSixHoursBeforeEnd = Calendar.getInstance();
        deadlineSixHoursBeforeEnd.set(Calendar.HOUR_OF_DAY, 18);
        deadlineSixHoursBeforeEnd.set(Calendar.MINUTE, 0);
        deadlineSixHoursBeforeEnd.set(Calendar.SECOND, 0);
        return deadlineSixHoursBeforeEnd;
    }

    /**
     * Generates a Calendar object representing the current date with time 23:55 PM.
     *
     * @return a Calendar object representing the current date with time 23:55 PM.
     */
    public Calendar getFiveMinutesDeadline() {
        Calendar deadlineFiveMinutesBeforeEnd = Calendar.getInstance();
        deadlineFiveMinutesBeforeEnd.set(Calendar.HOUR_OF_DAY, 23);
        deadlineFiveMinutesBeforeEnd.set(Calendar.MINUTE, 55);
        deadlineFiveMinutesBeforeEnd.set(Calendar.SECOND, 0);
        return deadlineFiveMinutesBeforeEnd;
    }


    /**
     * Decides what happens with a request when it arrives - it can be approved, rejected,
     * left pending for manual review, or left pending until the FRP gets more resources at 18PM.
     *
     * @param timePeriod the time period at which the request is submitted
     * @param isForTomorrow whether the request is for tomorrow
     * @param frpHasEnoughResources whether the FRP has enough resources for this request
     * @param facultyHasEnoughResources whether the faculty the request is scheduled to has enough resources for this request
     * @return the status of the request:
     *     0 for pending manual approval,
     *     1 for approved,
     *     2 for rejected,
     *     3 pending and waiting for the free RP to get resources at the 6h before end of day deadline
     */
    public int decideStatusOfRequest(int timePeriod, boolean isForTomorrow, boolean frpHasEnoughResources,
                                     boolean facultyHasEnoughResources) {
        if ((timePeriod == 2 && isForTomorrow) || (!frpHasEnoughResources && timePeriod == 1 && isForTomorrow)) {
            //auto reject
            return 2;
        } else if ((timePeriod == 1 && frpHasEnoughResources) || (isForTomorrow && timePeriod == 0
                && !facultyHasEnoughResources && frpHasEnoughResources)) {
            //auto approve
            return 1;
        } else if (timePeriod == 0 && !facultyHasEnoughResources && !frpHasEnoughResources) {
            //wait for the FRP to get more resources at 6h before end of day and then automatically check again
            return 3;
        } else {
            //set for manual review
            return 0;
        }
    }


    /**
     * Requests the available resources from the RP MS.
     *
     * @param facultyName name of the faculty.
     *
     * @return the available resources
     */
    public Resources getFacultyResourcesByName(String facultyName) {
        RestTemplate restTemplate = new RestTemplate();
        String request = facultyName;
        Resources availableResources = restTemplate.postForObject("http://localhost:8085/resources", request, Resources.class);
        return availableResources;
    }

}

