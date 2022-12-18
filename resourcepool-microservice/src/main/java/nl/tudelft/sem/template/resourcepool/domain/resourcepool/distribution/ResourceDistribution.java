package nl.tudelft.sem.template.resourcepool.domain.resourcepool.distribution;

import java.util.Objects;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;

/**
 * Represents a distribution of resources for a specific faculty.
 */
public class ResourceDistribution {

    private final transient String name;

    private final transient Resources resources;

    private final transient double percentageCpu;
    private final transient double percentageGpu;
    private final transient double percentageMemory;


    /**
     * Constructs a new ResourceDistribution object with the specified name, resources, and resource percentages.
     *
     * @param name the name of the resource distribution
     * @param resources the resources available for distribution
     * @param percentageCpu the percentage of CPU resources to allocate
     * @param percentageGpu the percentage of GPU resources to allocate
     * @param percentageMemory the percentage of memory resources to allocate
     */
    public ResourceDistribution(String name, Resources resources, double percentageCpu,
                                double percentageGpu, double percentageMemory) {
        this.name = name;
        this.resources = resources;
        this.percentageCpu = percentageCpu;
        this.percentageGpu = percentageGpu;
        this.percentageMemory = percentageMemory;
    }

    /**
     * Returns a string with the name of the faculty for which the distribution is.
     *
     * @return String the name of the faculty for which the distribution is
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the absolute resources for the distribution.
     *
     * @return Resources the absolute resources for the distribution
     */
    public Resources getResources() {
        return resources;
    }

    /**
     * Returns a double for the relative amount of cpu.
     *
     * @return double for the relative amount of cpu
     */
    public double getPercentageCpu() {
        return percentageCpu;
    }

    /**
     * Returns a double for the relative amount of gpu.
     *
     * @return double for the relative amount of gpu
     */
    public double getPercentageGpu() {
        return percentageGpu;
    }

    /**
     * Returns a double for the relative amount of memory.
     *
     * @return double for the relative amount of memory
     */
    public double getPercentageMemory() {
        return percentageMemory;
    }

    /**
     * Equality based on all fields of the class.
     *
     * @param o the Object which is being compared
     * @return whether the distribution is equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourceDistribution)) {
            return false;
        }
        ResourceDistribution that = (ResourceDistribution) o;
        return Double.compare(that.getPercentageCpu(), getPercentageCpu()) == 0
                && Double.compare(that.getPercentageGpu(), getPercentageGpu()) == 0
                && Double.compare(that.getPercentageMemory(), getPercentageMemory()) == 0
                && getName().equals(that.getName()) && getResources().equals(that.getResources());
    }

    /**
     * Returns the hash code value for this resource distribution.
     *
     * @return the hash code value for this resource distribution
     */
    @Override
    public int hashCode() {
        return Objects.hash(getName(), getResources(), getPercentageCpu(), getPercentageGpu(), getPercentageMemory());
    }

    /**
     * Returns a string representation for this resource distribution.
     *
     * @return a string representation for this resource distribution
     */
    @Override
    public String toString() {
        return "{" + name + ", ("
                + resources + "), ("
                + percentageCpu + "%, "
                + percentageGpu + "%, "
                + percentageMemory + "%)}";
    }
}
