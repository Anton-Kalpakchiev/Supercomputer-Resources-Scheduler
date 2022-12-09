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
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import nl.tudelft.sem.template.resourcepool.domain.resources.ResourcesAttributeConverter;

@Entity
@Table(name = "RpFaculty")//contains both ResourcePools(which we should have only 1, the free resource pool) and Faculties
@NoArgsConstructor
public class ResourcePool {


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

    /**
     * Constructs a new ResourcePool with the specified id and name.
     *
     * @param id the id of the resource pool
     * @param name the name of the resource pool
     */
    public ResourcePool(long id, String name) {
        this.id = id;
        this.name = name;
        this.baseResources = new Resources(0, 0, 0);
        this.nodeResources = new Resources(0, 0, 0);
        this.availableResources = new Resources(0, 0, 0);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Resources getBaseResources() {
        return baseResources;
    }

    public Resources getNodeResources() {
        return nodeResources;
    }

    public Resources getAvailableResources() {
        return availableResources;
    }

    public void setBaseResources(Resources baseResources) {
        this.baseResources = baseResources;
    }

    public void setNodeResources(Resources nodeResources) {
        this.nodeResources = nodeResources;
    }

    public void setAvailableResources(Resources availableResources) {
        this.availableResources = availableResources;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResourcePool that = (ResourcePool) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, baseResources, nodeResources, availableResources);
    }

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
