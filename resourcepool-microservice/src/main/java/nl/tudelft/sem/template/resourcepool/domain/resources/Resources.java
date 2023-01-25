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


    /**
     * Adds 2 Resources classes together.
     *
     * @param resources1 the first Resources
     * @param resources2 the second Resources
     * @return the combined Resources
     */
    public static Resources add(Resources resources1, Resources resources2) {
        return new Resources(resources1.getCpu() + resources2.getCpu(),
                resources1.getGpu() + resources2.getGpu(), resources1.getMemory() + resources2.getMemory());
    }

    /**
     * Subtracts the resources of one Resources class from the resources of another.
     *
     * @param resources1 the original Resources class
     * @param resources2 the Resources we subtract
     * @return the result of the operation
     */
    public static Resources subtract(Resources resources1, Resources resources2) {
        Resources result = new Resources(resources1.getCpu() - resources2.getCpu(),
                resources1.getGpu() - resources2.getGpu(), resources1.getMemory() - resources2.getMemory());
        return result;
    }

    /**
     * Mutated version of the subtract method used for mutation testing assignment 3.
     * Flips integer subtraction to integer addition in first line
     *
     * @param resources1 the original Resources class
     * @param resources2 the Resources we subtract
     * @return the result of the operation
     */
    public static Resources subtractMutated(Resources resources1, Resources resources2) {
        //mutation: CPUs are getting added instead of subtracted.
        Resources result = new Resources(resources1.getCpu() + resources2.getCpu(),
                resources1.getGpu() - resources2.getGpu(), resources1.getMemory() - resources2.getMemory());

        return result;
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
