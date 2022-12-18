package nl.tudelft.sem.template.users.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResourceDto {
    private final transient int cpu;
    private final transient int gpu;
    private final transient int memory;
}
