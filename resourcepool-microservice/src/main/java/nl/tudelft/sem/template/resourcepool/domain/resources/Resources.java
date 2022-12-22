package nl.tudelft.sem.template.resourcepool.domain.resources;

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

    /**
     * Gets cpu.
     *
     * @return the cpu
     */
    public int getCpu() {
        return cpu;
    }

    /**
     * Gets gpu.
     *
     * @return the gpu
     */
    public int getGpu() {
        return gpu;
    }

    /**
     * Gets memory.
     *
     * @return the memory
     */
    public int getMemory() {
        return memory;
    }

    public static Resources add(Resources resources1, Resources resources2) {
        return new Resources(resources1.getCpu() + resources2.getCpu(),
                resources1.getGpu() + resources2.getCpu(), resources1.getMemory() + resources2.getMemory());
    }

    public static Resources subtract(Resources resources1, Resources resources2) {
        return new Resources(resources1.getCpu() - resources2.getCpu(),
                resources1.getGpu() - resources2.getCpu(), resources1.getMemory() - resources2.getMemory());
    }

    /**
     * Equality is based on all fields.
     *
     * @return whether the daily schedules are equal
     */
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

    /**
     * Returns the hash code value for these resources.
     *
     * @return the hash code value for these resources
     */
    @Override
    public int hashCode() {
        return Objects.hash(cpu, gpu, memory);
    }

    /**
     * Returns a string representation for these resources.
     *
     * @return a string representation for these resources
     */
    @Override
    public String toString() {
        return "CPU: " + cpu + ", GPU: " + gpu + ", Memory: " + memory;
    }
}
