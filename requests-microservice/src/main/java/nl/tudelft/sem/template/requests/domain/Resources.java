package nl.tudelft.sem.template.requests.domain;

import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A DDD value object representing a password in our domain.
 */

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

        if (mem < 0 || cpu < 0 || gpu < 0 || cpu < gpu) {
            throw new InvalidResourcesException(this);
        }
    }

    @Override
    public String toString() {
        return "Memory: " + mem + " CPU: " + cpu + " GPU: " + gpu;
    }

    @Override
    public boolean equals(Object other) {
        Resources o = (Resources) other;
        return (this.mem == o.getMem() && this.cpu == o.getCpu() && this.gpu == o.getGpu());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this);
    }
}
