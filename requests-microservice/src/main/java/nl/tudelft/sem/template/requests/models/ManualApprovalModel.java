package nl.tudelft.sem.template.requests.models;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Model representing a manual approval/rejection request.
 */
@AllArgsConstructor
@Data
public class ManualApprovalModel {

    private boolean approved;

    private long requestId;

    private String dayOfExecution;

}
