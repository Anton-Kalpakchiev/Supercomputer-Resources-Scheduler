package nl.tudelft.sem.template.resourcepool.models;

import lombok.Data;

@Data
public class ResourcesByIdModel {
    private int cpu;
    private int gpu;
    private int memory;
}
