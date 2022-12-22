package nl.tudelft.sem.template.resourcepool.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing the information needed to interact with a faculty.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeInteractionRequestModel {

    private long facultyId;

    private int cpu;
    private int gpu;
    private int memory;
}
