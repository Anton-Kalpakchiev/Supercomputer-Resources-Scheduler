package nl.tudelft.sem.template.users.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResourcesDto {
    private transient int cpu;
    private transient int gpu;
    private transient int memory;
}
