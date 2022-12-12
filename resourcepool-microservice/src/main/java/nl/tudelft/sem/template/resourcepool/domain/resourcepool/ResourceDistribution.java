package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;

import java.util.Objects;

public class ResourceDistribution {

    private final transient String name;

    private final transient Resources resources;

    private final transient float percentageCpu;

    private final transient float percentageGpu;

    private final transient float percentageMemory;

    public ResourceDistribution(String name, Resources resources, float percentageCpu, float percentageGpu, float percentageMemory) {
        this.name = name;
        this.resources = resources;
        this.percentageCpu = percentageCpu;
        this.percentageGpu = percentageGpu;
        this.percentageMemory = percentageMemory;
    }

    public String getName() {
        return name;
    }

    public Resources getResources() {
        return resources;
    }

    public float getPercentageCpu() {
        return percentageCpu;
    }

    public float getPercentageGpu() {
        return percentageGpu;
    }

    public float getPercentageMemory() {
        return percentageMemory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourceDistribution)) {
            return false;
        }
        ResourceDistribution that = (ResourceDistribution) o;
        return Float.compare(that.getPercentageCpu(), getPercentageCpu()) == 0
                && Float.compare(that.getPercentageGpu(), getPercentageGpu()) == 0
                && Float.compare(that.getPercentageMemory(), getPercentageMemory()) == 0
                && getName().equals(that.getName()) && getResources().equals(that.getResources());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getResources(), getPercentageCpu(), getPercentageGpu(), getPercentageMemory());
    }

    @Override
    public String toString() {
        return "{" + name + ", ("
                + resources + "), ("
                + percentageCpu + "%, "
                + percentageGpu + "%, "
                + percentageMemory + "%)}";
    }
}
