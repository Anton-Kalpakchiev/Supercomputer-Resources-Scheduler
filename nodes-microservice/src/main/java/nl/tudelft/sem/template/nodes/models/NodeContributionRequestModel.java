package nl.tudelft.sem.template.nodes.models;

import lombok.Data;

/**
 * Model representing a node contribution request.
 */
@Data
public class NodeContributionRequestModel {

    private String name;
    private String url;
    private long facultyId;
    private String token;
    private int cpu;
    private int gpu;
    private int memory;
}