package nl.tudelft.sem.template.users.models.facade;

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

    String dayOfExecution;

}
