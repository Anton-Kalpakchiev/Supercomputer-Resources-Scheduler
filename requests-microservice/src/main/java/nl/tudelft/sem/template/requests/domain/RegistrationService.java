package nl.tudelft.sem.template.requests.domain;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;

@Service
public class RegistrationService {
    private final transient RequestRepository requestRepository;
    private final transient ResourcePoolService resourcePoolService;

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
     * @param resources   The resorces requested
     */
    public AppRequest registerRequest(String description, Resources resources, String owner, String facultyName, Resources availableResources, Calendar deadline, Resources FRPResources, String token) throws IOException, InvalidResourcesException {
        AppRequest request = new AppRequest(description, resources, owner, facultyName, deadline, -1);

        Calendar deadlineSixHoursBeforeEnd = Calendar.getInstance();
        Calendar deadlineFiveMinutesBeforeEnd = Calendar.getInstance();

        deadlineSixHoursBeforeEnd.set(Calendar.HOUR_OF_DAY, 18);
        deadlineSixHoursBeforeEnd.set(Calendar.MINUTE, 0);
        deadlineSixHoursBeforeEnd.set(Calendar.SECOND, 0);

        deadlineFiveMinutesBeforeEnd.set(Calendar.HOUR_OF_DAY, 23);
        deadlineFiveMinutesBeforeEnd.set(Calendar.MINUTE, 55);
        deadlineFiveMinutesBeforeEnd.set(Calendar.SECOND, 0);

        boolean facultyHasEnoughResources = true;
        if(availableResources.getGpu() < resources.getGpu() || availableResources.getCpu() < resources.getCpu() || availableResources.getMemory() < resources.getMemory()) facultyHasEnoughResources = false;
        boolean FRPHasEnoughResources = true;
        if(FRPResources.getGpu() < resources.getGpu() || FRPResources.getCpu() < resources.getCpu() || FRPResources.getMemory() < resources.getMemory()) FRPHasEnoughResources = false;
        boolean isForTomorrow = isForTomorrow(deadline);
        int timePeriod = 0;//0 when before the 6h deadline, 1 when after the 6h deadline and before the 5min deadline, 2 when after the 5 min deadline
        if(Calendar.getInstance().after(deadlineSixHoursBeforeEnd) && Calendar.getInstance().before(deadlineFiveMinutesBeforeEnd)) timePeriod = 1;
        if(Calendar.getInstance().after(deadlineFiveMinutesBeforeEnd)) timePeriod = 2;

        //automatic rejection
        if((timePeriod == 2 && isForTomorrow) || (!FRPHasEnoughResources && timePeriod == 1 && isForTomorrow)){
//            //auto reject
            request.setStatus(2);
        }
        //automatic approval
        else if((timePeriod == 1 && FRPHasEnoughResources )|| (isForTomorrow && timePeriod == 0 && !facultyHasEnoughResources && FRPHasEnoughResources)){
            //auto approve
            request.setStatus(1);
            requestRepository.save(request);
            resourcePoolService.automaticApproval(deadline, request.getId(), token);
            //update RP/Schedule MS os that it can update the schedule for the corresponding faculty for tomorrow
        }
        //waiting for the free resource pool to get resources at 6h before end of day
        else if(timePeriod == 0 && !facultyHasEnoughResources && !FRPHasEnoughResources){
            //wait for the FRP to get more resources at 6h before end of day and then automatically check again
            request.setStatus(3);
        }
        //manual(pending)
        else{
            //set for manual approval/rejection
            request.setStatus(0);
        }
        requestRepository.save(request);

        return request;
    }

    public AppRequest processRequestInPeriodOne(AppRequest request, Resources FRPResources) throws InvalidResourcesException {
        Calendar deadline = request.getDeadline();
        Resources resources = new Resources(request.getMem(), request.getCpu(), request.getGpu());
        boolean isForTomorrow = isForTomorrow(deadline);

        boolean FRPHasEnoughResources = true;
        if(FRPResources.getGpu() < resources.getGpu() || FRPResources.getCpu() < resources.getCpu() || FRPResources.getMemory() < resources.getMemory()) FRPHasEnoughResources = false;
        int timePeriod = 1;//0 when before the 6h deadline, 1 when after the 6h deadline and before the 5min deadline, 2 when after the 5 min deadline

        //automatic rejection
        if((!FRPHasEnoughResources && isForTomorrow)){
            //auto reject
            //find request in Repo, update its status
        }
        //automatic approval
        else if(FRPHasEnoughResources){
            //auto approve for tomorrow
            //find request in Repo, update its status
            //update RP/Schedule MS os that it can update the schedule for the corresponding faculty for tomorrow
        }
        //manual(pending)
        else{
            //set for manual approval
            //find request in Repo, update its status
        }

        return request;
    }

    public boolean isForTomorrow(Calendar deadline){
        Calendar endOfTomorrow = Calendar.getInstance();
        endOfTomorrow.set(Calendar.HOUR_OF_DAY, 23);
        endOfTomorrow.set(Calendar.MINUTE, 59);
        endOfTomorrow.set(Calendar.SECOND, 59);
        endOfTomorrow.set(Calendar.MILLISECOND, 999);
        endOfTomorrow.add(Calendar.DAY_OF_YEAR, 1);
        if(deadline.after(endOfTomorrow)) return false;
        return true;
    }

}

