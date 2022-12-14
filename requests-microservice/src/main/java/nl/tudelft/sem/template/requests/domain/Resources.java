package nl.tudelft.sem.template.requests.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A DDD value object representing a password in our domain.
 */
@EqualsAndHashCode
@Data
public class Resources {

    @Getter
    private final transient int cpu;
    @Getter
    private final transient int gpu;
    @Getter
    private final transient int memory;

    /**
     * A container class for holding different types of resources.
     *
     *
     * @param cpu The number of CPU resources.
     * @param gpu The number of GPU resources.
     * @param memory The number of memory resources.
     */
    public Resources(int cpu, int gpu, int memory) throws InvalidResourcesException {
        this.cpu = cpu;
        this.gpu = gpu;
        this.memory = memory;

        if (memory < 0 || cpu < 0 || gpu < 0) {
            throw new InvalidResourcesException(this);
        }

        if (cpu < gpu) {
            throw new InvalidResourcesException(this);
        }
    }

    @Override
    public String toString() {
        return "Memory: " + memory + " CPU: " + cpu + " GPU: " + gpu;
    }
}
