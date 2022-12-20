package nl.tudelft.sem.template.requests.domain;

import java.io.IOException;
import java.util.Calendar;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private final transient RequestRepository requestRepository;
    private final transient ResourcePoolService resourcePoolService;
    private transient boolean facultyHasEnoughResources;
    private transient boolean frpHasEnoughResources;
    private transient int timePeriod;

    /**
     * Instantiates a new RegistrationService.
     *
     * @param requestRepository the request repository
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
                                  Resources availableResources, Calendar deadline, Resources frpResources, String token)
            throws IOException, InvalidResourcesException {
        AppRequest request = new AppRequest(description, resources, owner, facultyName, deadline, -1);

        Calendar deadlineSixHoursBeforeEnd = Calendar.getInstance();
        deadlineSixHoursBeforeEnd.set(Calendar.HOUR_OF_DAY, 18);
        deadlineSixHoursBeforeEnd.set(Calendar.MINUTE, 0);
        deadlineSixHoursBeforeEnd.set(Calendar.SECOND, 0);

        Calendar deadlineFiveMinutesBeforeEnd = Calendar.getInstance();
        deadlineFiveMinutesBeforeEnd.set(Calendar.HOUR_OF_DAY, 23);
        deadlineFiveMinutesBeforeEnd.set(Calendar.MINUTE, 55);
        deadlineFiveMinutesBeforeEnd.set(Calendar.SECOND, 0);

        if (availableResources.getGpu() < resources.getGpu() || availableResources.getCpu() < resources.getCpu()
                || availableResources.getMemory() < resources.getMemory()) {
            facultyHasEnoughResources = false;
        } else {
            facultyHasEnoughResources = true;
        }
        if (frpResources.getGpu() < resources.getGpu() || frpResources.getCpu() < resources.getCpu()
                || frpResources.getMemory() < resources.getMemory()) {
            frpHasEnoughResources = false;
        } else {
            frpHasEnoughResources = true;
        }
        boolean isForTomorrow = isForTomorrow(deadline);
        // 0 when before the 6h deadline,
        // 1 when after the 6h deadline and before the 5min deadline,
        // 2 when after the 5 min deadline
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
        } else if ((timePeriod == 1 && frpHasEnoughResources) || (isForTomorrow && timePeriod == 0
                && !facultyHasEnoughResources && frpHasEnoughResources)) {
            //auto approve
            request.setStatus(1);
            requestRepository.save(request);
            resourcePoolService.automaticApproval(deadline, request.getId(), token);
            //update RP/Schedule MS os that it can update the schedule for the corresponding faculty for tomorrow
        } else if (timePeriod == 0 && !facultyHasEnoughResources && !frpHasEnoughResources) {
            //wait for the FRP to get more resources at 6h before end of day and then automatically check again
            request.setStatus(3);
        } else {
            //set for manual approval/rejection
            request.setStatus(0);
        }

        requestRepository.save(request);
        return request;
    }

    /**
     * Processes a request in period one.
     *
     * @param request the given request
     * @param freePoolResources the free resources
     * @return the AppRequest returned after processing
     * @throws InvalidResourcesException thrown when resources are invalid
     */
    public AppRequest processRequestInPeriodOne(
            AppRequest request, Resources freePoolResources) throws InvalidResourcesException {
        Calendar deadline = Calendar.getInstance(); //request.getDeadline();
        Resources resources = new Resources(request.getMem(), request.getCpu(), request.getGpu());

        frpHasEnoughResources = true;
        if (freePoolResources.getGpu() < resources.getGpu()
                || freePoolResources.getCpu() < resources.getCpu()
                || freePoolResources.getMemory() < resources.getMemory()) {
            frpHasEnoughResources = false;
        }
        //int timePeriod = 1;
        /*
        0 when before the 6h deadline,
        1 when after the 6h deadline and before the 5min deadline,
        2 when after the 5 min deadline
        */

        //automatic rejection
        if ((!frpHasEnoughResources && isForTomorrow(deadline))) {
            System.out.println("To be implemented!");
            //auto reject
            //find request in Repo, update its status
        } else if (frpHasEnoughResources) {
            System.out.println("To be implemented!");
            //auto approve for tomorrow
            //find request in Repo, update its status
            //update RP/Schedule MS os that it can update the schedule for the corresponding faculty for tomorrow
        } else {
            System.out.println("To be implemented!");
            //set for manual approval
            //find request in Repo, update its status
        }
        //0 when before the 6h deadline,
        // 1 when after the 6h deadline and before the 5min deadline,
        // 2 when after the 5min deadline
        // timePeriod = 1;
        //        if ((!FRPHasEnoughResources && isForTomorrow(request.getDeadline())) {
        //            //auto reject
        //            //find request in Repo, update its status
        //        } else if (FRPHasEnoughResources) {
        //            //auto approve for tomorrow
        //            //find request in Repo, update its status
        //            //update RP/Schedule MS os that it can update the schedule for the corresponding faculty for tomorrow
        //        } else {
        //            //set for manual approval
        //            //find request in Repo, update its status
        //        }
        return request;
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

}

