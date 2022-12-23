package nl.tudelft.sem.template.users.models.facade;

import lombok.Data;

/**
 * Model representing a node deletion request.
 */
@Data
public class NodeDeletionRequestModel {

    private long nodeId;
}