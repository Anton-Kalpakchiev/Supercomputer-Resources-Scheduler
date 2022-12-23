package nl.tudelft.sem.template.requests.models;

import lombok.Data;

@Data
public class ResourcesDto {
    private int cpu;
    private int gpu;
    private int memory;
}
