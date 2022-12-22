package nl.tudelft.sem.template.nodes.models;

import lombok.Data;

/**
 * Model representing a node deletion request.
 */
@Data
public class NodeDeletionRequestModel {

    private long nodeId;
}