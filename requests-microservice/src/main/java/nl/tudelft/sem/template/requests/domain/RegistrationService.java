package nl.tudelft.sem.template.requests.domain;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import nl.tudelft.sem.template.requests.controllers.RequestController;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
//We can remove this line later on, but I can't figure out how to fix this and the code works perfect with the error in it
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class RegistrationService {
    private final transient RequestRepository requestRepository;
    private final transient ResourcePoolService resourcePoolService;

    String initialToken = null;//when tokens are not needed anymore, delete this and rework a bit the functions

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
            throws IOException, InvalidResourcesException {
        if(initialToken == null) initialToken = token;
        AppRequest request = new AppRequest(description, resources, owner, facultyName, deadline, -1);

        Calendar deadlineSixHoursBeforeEnd = Calendar.getInstance();
        deadlineSixHoursBeforeEnd.set(Calendar.HOUR_OF_DAY, 18);
        deadlineSixHoursBeforeEnd.set(Calendar.MINUTE, 0);
        deadlineSixHoursBeforeEnd.set(Calendar.SECOND, 0);

        Calendar deadlineFiveMinutesBeforeEnd = Calendar.getInstance();
        deadlineFiveMinutesBeforeEnd.set(Calendar.HOUR_OF_DAY, 23);
        deadlineFiveMinutesBeforeEnd.set(Calendar.MINUTE, 55);
        deadlineFiveMinutesBeforeEnd.set(Calendar.SECOND, 0);

        final boolean facultyHasEnoughResources = !(availableResources.getGpu() < resources.getGpu()
                || availableResources.getCpu() < resources.getCpu()
                || availableResources.getMemory() < resources.getMemory());

        final boolean frpHasEnoughResources = !(freePoolResources.getGpu() < resources.getGpu()
                || freePoolResources.getCpu() < resources.getCpu()
                || freePoolResources.getMemory() < resources.getMemory());

        final boolean isForTomorrow = isForTomorrow(deadline);
        int timePeriod;
        /*
        0 when before the 6h deadline
        1 when after the 6h deadline and before the 5min deadline,
        2 when after the 5 min deadline
         */
        if (Calendar.getInstance().after(deadlineSixHoursBeforeEnd)
                && Calendar.getInstance().before(deadlineFiveMinutesBeforeEnd)) {
            timePeriod = 1;
        } else if (Calendar.getInstance().after(deadlineFiveMinutesBeforeEnd)) {
            timePeriod = 2;
        } else {
            timePeriod = 0;
        }

        //automatic rejection
        if ((timePeriod == 2 && isForTomorrow) || (!frpHasEnoughResources && timePeriod == 1 && isForTomorrow)) {
            //auto reject
            request.setStatus(2);
            requestRepository.save(request);
        } else if ((timePeriod == 1 && frpHasEnoughResources) || (isForTomorrow && timePeriod == 0
                && !facultyHasEnoughResources && frpHasEnoughResources)) {
            //auto approve
            request.setStatus(1);
            requestRepository.save(request);
            resourcePoolService.approval(deadline, request.getId(), token);
            //update RP/Schedule MS so that it can update the schedule for the corresponding faculty for tomorrow
        } else if (timePeriod == 0 && !facultyHasEnoughResources && !frpHasEnoughResources) {
            //wait for the FRP to get more resources at 6h before end of day and then automatically check again
            request.setStatus(3);
            requestRepository.save(request);
        } else {
            //set for manual review
            request.setStatus(0);
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
     * @throws InvalidResourcesException thrown when resources are invalid
     */
    public AppRequest processRequestInPeriodOne(AppRequest request, String token) throws InvalidResourcesException, IOException {
        Calendar deadline = request.getDeadline();
        Resources resources = new Resources(request.getMem(), request.getCpu(), request.getGpu());

        final Resources freePoolResources = getFacultyResourcesByName("Free pool");

        boolean frpHasEnoughResources = !(freePoolResources.getGpu() < resources.getGpu()
                || freePoolResources.getCpu() < resources.getCpu()
                || freePoolResources.getMemory() < resources.getMemory());

        // int timePeriod = 1;
        /*
        0 when before the 6h deadline,
        1 when after the 6h deadline and before the 5min deadline,
        2 when after the 5 min deadline
        */

        if ((!frpHasEnoughResources && isForTomorrow(deadline))) {
            //rejection
            //find request in Repo, update its status
            request.setStatus(2);
            requestRepository.save(request);
        } else if (frpHasEnoughResources) {
            //approval for tomorrow
            request.setStatus(1);
            //find request in Repo, update its status
            requestRepository.save(request);
            //update RP/Schedule MS os that it can update the schedule for the corresponding faculty for tomorrow
            resourcePoolService.approval(deadline, request.getId(), token);
        } else {
            //set for manual review
            //find request in Repo, update its status
            request.setStatus(0);
            requestRepository.save(request);
        }
        return request;
    }


    /**
     * At 18PM everyday, all requests that are left pending to be processed when the FRP gets more resources, get processed.
     * Gets automatically called at the proper time. (5 minute after 18PM, in order to give time for the resources to be released from all faculties)
     * @throws IOException when the request is not in the repository
     * @throws InvalidResourcesException this exception will be removed further on in the development period
     */
    @Scheduled(cron = "0 5 18 * * *")
    public void processAllPendingRequests() throws IOException, InvalidResourcesException {
        List<AppRequest> allRequests = requestRepository.findAll().stream().filter(x -> x.getStatus() == 3).collect(Collectors.toList());
        for(AppRequest thisRequest : allRequests){
            processRequestInPeriodOne(thisRequest, initialToken);
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

        if (deadline.after(endOfTomorrow)) {
            return false;
        }
        return true;
    }


    /**
     * Requests the available resources from the RP MS.
     *
     * @param facultyName name of the faculty.
     *
     * @return the available resources
     *
     * @throws IOException when post for object fails
     */
    public Resources getFacultyResourcesByName(String facultyName) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String request = facultyName;
        Resources availableResources = restTemplate.postForObject("http://localhost:8085/resources", request, Resources.class);
        return availableResources;
    }

}

