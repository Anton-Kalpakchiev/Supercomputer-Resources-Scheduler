package nl.tudelft.sem.template.resourcepool.models;

import lombok.Data;

/**
 * Model representing a distribution request.
 */
@Data
public class DistributionModel {

    private String name;

    private double cpu;

    private double gpu;

    private double memory;
}
