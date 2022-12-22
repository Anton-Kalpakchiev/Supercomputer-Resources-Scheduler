package nl.tudelft.sem.template.resourcepool.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing a contribution of a node to a faculty.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContributeToFacultyModel {

    private long facultyId;

    private int cpu;
    private int gpu;
    private int memory;
}
