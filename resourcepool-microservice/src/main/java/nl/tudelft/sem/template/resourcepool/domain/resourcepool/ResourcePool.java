package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.resourcepool.domain.HasEvents;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import nl.tudelft.sem.template.resourcepool.domain.resources.ResourcesAttributeConverter;

/**
 * The type Resource pool.
 */
@Entity
@Table(name = "RpFaculty")//contains both ResourcePools(which we should have only 1, the free resource pool) and Faculties
@NoArgsConstructor
public class ResourcePool extends HasEvents {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "baseResources", nullable = false)
    @Convert(converter = ResourcesAttributeConverter.class)
    private Resources baseResources;
    @Column(name = "nodeResources", nullable = false)
    @Convert(converter = ResourcesAttributeConverter.class)
    private Resources nodeResources;
    @Column(name = "availableResources", nullable = false)
    @Convert(converter = ResourcesAttributeConverter.class)
    private Resources availableResources;

    @Column(name = "managerNetId")//this is just here so the repo knows the column exists
    private long managerNetId;

    /**
     * Constructs a new ResourcePool with the specified id and name,
     * the other fields will be set to empty recourses.
     *
     * @param name the name of the resource pool
     */
    public ResourcePool(String name) {
        this.name = name;
        this.baseResources = new Resources(0, 0, 0);
        this.nodeResources = new Resources(0, 0, 0);
        this.availableResources = new Resources(0, 0, 0);
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets base resources.
     *
     * @return the base resources
     */
    public Resources getBaseResources() {
        return baseResources;
    }

    /**
     * Gets node resources.
     *
     * @return the node resources
     */
    public Resources getNodeResources() {
        return nodeResources;
    }

    /**
     * Gets available resources.
     *
     * @return the available resources
     */
    public Resources getAvailableResources() {
        return availableResources;
    }

    /**
     * Sets base resources.
     *
     * @param baseResources the base resources
     */
    public void setBaseResources(Resources baseResources) {
        this.baseResources = baseResources;
    }

    /**
     * Sets node resources.
     *
     * @param nodeResources the node resources
     */
    public void setNodeResources(Resources nodeResources) {
        this.nodeResources = nodeResources;
    }

    /**
     * Sets available resources.
     *
     * @param availableResources the available resources
     */
    public void setAvailableResources(Resources availableResources) {
        this.availableResources = availableResources;
    }

    /**
     * Equality is only based on the name.
     *
     * @return whether the resource pools are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResourcePool that = (ResourcePool) o;
        return name.equals(that.name);
    }

    /**
     * Returns the hash code value for this resource pool.
     *
     * @return the hash code value for this resource pool
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, baseResources, nodeResources, availableResources);
    }

    /**
     * Returns a string representation for this resource pool.
     *
     * @return a string representation for this resource pool
     */
    @Override
    public String toString() {
        return "ResourcePool{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", baseResources=" + baseResources
                + ", nodeResources=" + nodeResources
                + ", availableResources=" + availableResources
                + '}';
    }
}
