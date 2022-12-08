package nl.tudelft.sem.template.nodes.domain.resources;

import java.util.Objects;

/**
 * A DDD value object representing a Resource in our domain.
 */
public class Resources {
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
    public Resources(int cpu, int gpu, int memory) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Resources resource = (Resources) o;
        return cpu == resource.cpu && gpu == resource.gpu && memory == resource.memory;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpu, gpu, memory);
    }
}
