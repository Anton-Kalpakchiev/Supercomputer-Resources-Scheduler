package nl.tudelft.sem.template.requests.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A DDD value object representing a password in our domain.
 */
@EqualsAndHashCode
public class Resources {
    @Getter
    private final transient int mem;
    @Getter
    private final transient int cpu;
    @Getter
    private final transient int gpu;

    /**
     * A container class for holding different types of resources.
     *
     * @param mem The number of memory resources.
     *
     * @param cpu The number of CPU resources.
     *
     * @param gpu The number of GPU resources.
     */
    public Resources(int mem, int cpu, int gpu) throws InvalidResourcesException {
        this.mem = mem;
        this.cpu = cpu;
        this.gpu = gpu;

        if (mem < 0 || cpu < 0 || gpu < 0) {
            throw new InvalidResourcesException(this);
        }

        if (cpu < gpu) {
            throw new InvalidResourcesException(this);
        }
    }

    @Override
    public String toString() {
        return "Memory: " + mem + " CPU: " + cpu + " GPU: " + gpu;
    }
}
