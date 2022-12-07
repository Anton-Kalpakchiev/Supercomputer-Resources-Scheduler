package nl.tudelft.sem.template.nodes.domain.resource;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a Resource in our domain.
 */
@EqualsAndHashCode
public class Resource {
    private final transient int cpu;
    private final transient int gpu;
    private final transient int memory;

    /**
     * Instantiates a new Resource.
     *
     * @param cpu    the cpu
     * @param gpu    the gpu
     * @param memory the memory
     */
    public Resource(int cpu, int gpu, int memory) {
        this.cpu = cpu;
        this.gpu = gpu;
        this.memory = memory;
    }

    public int getCpu() {
        return cpu;
    }

    public int getGpu() {
        return gpu;
    }

    public int getMemory() {
        return memory;
    }

    @Override
    public String toString() {
        return getCpu() + ", " + getGpu() + ", " + getMemory();
    }
}
