package nl.tudelft.sem.template.nodes.models;

import lombok.Data;

/**
 * Model representing a node creation request.
 */
@Data
public class NodeCreationRequestModel {
    private String name;
    private String url;
    private String token;
    private String resources;
}