package nl.tudelft.sem.template.requests.models;

import java.util.Calendar;
import lombok.Data;


/**
 * Model representing a manual approval/rejection request.
 */
@Data
public class ManualApprovalModel {

    private boolean approved;

    private long requestId;

    private String dayOfExecution;

    /**
     * Constructor for a ManualApprovalModel.
     *
     * @param approved whether the request is approved or rejected
     * @param requestId the id of the request
     * @param dayOfExecution the day of execution
     */
    public ManualApprovalModel(boolean approved, long requestId, String dayOfExecution) {
        this.approved = approved;
        this.requestId = requestId;
        this.dayOfExecution = dayOfExecution;
    }
}
