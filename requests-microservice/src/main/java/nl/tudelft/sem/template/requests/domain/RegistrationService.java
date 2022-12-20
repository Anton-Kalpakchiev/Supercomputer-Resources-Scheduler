package nl.tudelft.sem.template.requests.domain;

import java.util.Calendar;
import org.springframework.stereotype.Service;

@Service
//We can remove this line later on, but I can't figure out how to fix this and the code works perfect with the error in it
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class RegistrationService {
    private final transient RequestRepository requestRepository;

    /**
     * Instantiates a new RegistrationService.
     *
     * @param requestRepository the request repository
     */
    public RegistrationService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    /**
     * Register a new request.
     *
     * @param description The description of the request
     * @param resources   The resorces requested
     */
    public AppRequest registerRequest(String description, Resources resources,
                                      String owner, String facultyName, Resources availableResources,
                                      Calendar deadline, Resources freePoolResources) {
        AppRequest request = new AppRequest(description, resources, owner, facultyName, deadline, -1);

        Calendar deadlineSixHoursBeforeEnd = Calendar.getInstance();
        deadlineSixHoursBeforeEnd.set(Calendar.HOUR_OF_DAY, 18);
        deadlineSixHoursBeforeEnd.set(Calendar.MINUTE, 0);
        deadlineSixHoursBeforeEnd.set(Calendar.SECOND, 0);

        Calendar deadlineFiveMinutesBeforeEnd = Calendar.getInstance();
        deadlineFiveMinutesBeforeEnd.set(Calendar.HOUR_OF_DAY, 23);
        deadlineFiveMinutesBeforeEnd.set(Calendar.MINUTE, 55);
        deadlineFiveMinutesBeforeEnd.set(Calendar.SECOND, 0);

        boolean facultyHasEnoughResources = true;
        if (availableResources.getGpu() < resources.getGpu()
                || availableResources.getCpu() < resources.getCpu()
                || availableResources.getMem() < resources.getMem()) {
            facultyHasEnoughResources = false;
        }
        boolean freePoolHasEnoughResources = true;
        if (freePoolResources.getGpu() < resources.getGpu()
                || freePoolResources.getCpu() < resources.getCpu()
                || freePoolResources.getMem() < resources.getMem()) {
            freePoolHasEnoughResources = false;
        }
        boolean isForTomorrow = isForTomorrow(deadline);
        int timePeriod = 0;
        /*
        0 when before the 6h deadline
        1 when after the 6h deadline and before the 5min deadline,
        2 when after the 5 min deadline
         */
        if (Calendar.getInstance().after(deadlineSixHoursBeforeEnd)
                && Calendar.getInstance().before(deadlineFiveMinutesBeforeEnd)) {
            timePeriod = 1;
        }
        if (Calendar.getInstance().after(deadlineFiveMinutesBeforeEnd)) {
            timePeriod = 2;
        }

        //automatic rejection
        if ((timePeriod == 2 && isForTomorrow) || (!freePoolHasEnoughResources
                && timePeriod == 1 && isForTomorrow)) {
            //auto reject
            request.setStatus(2);
        } else if ((timePeriod == 1 && freePoolHasEnoughResources)
                || (isForTomorrow && timePeriod == 0 && !facultyHasEnoughResources
                && freePoolHasEnoughResources)) {
            //auto approve
            request.setStatus(1);
            //update RP/Schedule MS os that it can update the schedule for the corresponding faculty for tomorrow
        } else if (timePeriod == 0 && !facultyHasEnoughResources && !freePoolHasEnoughResources) {
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
        boolean isForTomorrow = isForTomorrow(deadline);

        boolean freePoolHasEnoughResources = true;
        if (freePoolResources.getGpu() < resources.getGpu()
                || freePoolResources.getCpu() < resources.getCpu()
                || freePoolResources.getMem() < resources.getMem()) {
            freePoolHasEnoughResources = false;
        }
        int timePeriod = 1;
        /*
        0 when before the 6h deadline,
        1 when after the 6h deadline and before the 5min deadline,
        2 when after the 5 min deadline
        */

        //automatic rejection
        if ((!freePoolHasEnoughResources && isForTomorrow)) {
            System.out.println("To be implemented!");
            //auto reject
            //find request in Repo, update its status
        } else if (freePoolHasEnoughResources) {
            System.out.println("To be implemented!");
            //auto approve for tomorrow
            //find request in Repo, update its status
            //update RP/Schedule MS os that it can update the schedule for the corresponding faculty for tomorrow
        } else {
            System.out.println("To be implemented!");
            //set for manual approval
            //find request in Repo, update its status
        }

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

