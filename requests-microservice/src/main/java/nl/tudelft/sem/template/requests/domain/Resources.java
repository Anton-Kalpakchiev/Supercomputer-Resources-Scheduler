package nl.tudelft.sem.template.requests.domain;

import java.util.Objects;
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
    public Resources(int cpu, int gpu, int memory) {
        this.cpu = cpu;
        this.gpu = gpu;
        this.memory = memory;
    }

    @Override
    public String toString() {
        return "Memory: " + memory + " CPU: " + cpu + " GPU: " + gpu;
    }

    @Override
    public boolean equals(Object other) {
        Resources o = (Resources) other;
        return (this.memory == o.getMemory() && this.cpu == o.getCpu() && this.gpu == o.getGpu());
    }

    @Override
    public int hashCode() {
        return Objects.hash(memory, cpu, gpu);
    }
}
