package nl.tudelft.sem.template.users.models.facade;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Model representing a distribution request.
 */
@Data
@AllArgsConstructor
public class DistributionModel {

    private String name;

    private double cpu;

    private double gpu;

    private double memory;
}
