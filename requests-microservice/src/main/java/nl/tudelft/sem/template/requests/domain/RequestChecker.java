package nl.tudelft.sem.template.requests.domain;

import org.springframework.stereotype.Service;

@Service
public class RequestChecker {

    /**
     * Decides what happens with a request when it arrives - it can be approved, rejected,
     * left pending for manual review, or left pending until the FRP gets more resources at 18PM.
     *
     * @param timePeriod                the time period at which the request is submitted
     * @param isForTomorrow             whether the request is for tomorrow
     * @param frpHasEnoughResources     whether the FRP has enough resources for this request
     * @param facultyHasEnoughResources whether the faculty the request is scheduled to has enough resources for this request
     * @return the status of the request
     */
    public int decideStatusOfRequest(int timePeriod, boolean isForTomorrow, boolean frpHasEnoughResources,
                                     boolean facultyHasEnoughResources) {
        // 0 for pending manual approval,
        // 1 for approved,
        // 2 for rejected,
        // 3 pending and waiting for the free RP to get resources at the 6h before end of day deadline
        if (isRequestRejected(timePeriod, isForTomorrow, frpHasEnoughResources)) {
            //auto reject
            return 2;
        }
        if (isRequestAutoApproved(timePeriod, isForTomorrow, frpHasEnoughResources, facultyHasEnoughResources)) {
            //auto approve
            return 1;
        }
        if (isRequestDelayedUntilSix(timePeriod, frpHasEnoughResources, facultyHasEnoughResources)) {
            //wait for the FRP to get more resources at 6h before end of day and then automatically check again
            return 3;
        }
        //set for manual review
        return 0;
    }

    /**
     * Decides whether a request is left pending until the 6H deadline given the following variables.
     *
     * @param timePeriod                the current time period
     * @param frpHasEnoughResources     whether the free resource pool has enough resources to take this request
     * @param facultyHasEnoughResources whether the respective faculty has enough resources to take this request
     * @return true iff the request is rejected
     */
    public boolean isRequestDelayedUntilSix(int timePeriod, boolean frpHasEnoughResources,
                                            boolean facultyHasEnoughResources) {
        return timePeriod == 0 && !facultyHasEnoughResources && !frpHasEnoughResources;
    }

    /**
     * Decides whether a request is automatically approved given the following variables.
     *
     * @param timePeriod                the current time period
     * @param isForTomorrow             whether the request is for tomorrow
     * @param frpHasEnoughResources     whether the free resource pool has enough resources to take this request
     * @param facultyHasEnoughResources whether the respective faculty has enough resources to take this request
     * @return true iff the request is rejected
     */
    public boolean isRequestAutoApproved(int timePeriod, boolean isForTomorrow, boolean frpHasEnoughResources,
                                         boolean facultyHasEnoughResources) {
        return (timePeriod == 1 && frpHasEnoughResources) || (isForTomorrow && timePeriod == 0
                && !facultyHasEnoughResources && frpHasEnoughResources);
    }

    /**
     * Decides whether a request is rejected given the following variables.
     *
     * @param timePeriod            the current time period
     * @param isForTomorrow         whether the request is for tomorrow
     * @param frpHasEnoughResources whether the free resource pool has enough resources to take this request
     * @return true iff the request is rejected
     */
    public boolean isRequestRejected(int timePeriod, boolean isForTomorrow, boolean frpHasEnoughResources) {
        return (timePeriod == 2 && isForTomorrow) || (!frpHasEnoughResources && timePeriod == 1 && isForTomorrow);
    }
}
