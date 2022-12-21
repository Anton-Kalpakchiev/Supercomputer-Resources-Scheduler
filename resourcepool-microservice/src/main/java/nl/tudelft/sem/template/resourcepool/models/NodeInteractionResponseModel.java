package nl.tudelft.sem.template.resourcepool.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Model representing the response to the node microservice.
 */
@Data
@AllArgsConstructor
public class NodeInteractionResponseModel {
    private boolean successful;
}
