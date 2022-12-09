package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import nl.tudelft.sem.template.resourcepool.domain.resources.ResourcesAttributeConverter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "RPFaculty")//contains both ResourcePools(which we should have only 1, the free resource pool) and Faculties
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


    public ResourcePool(long id, String name, Resources baseResources, Resources nodeResources, Resources availableResources) {
        this.id = id;
        this.name = name;
        this.baseResources = baseResources;
        this.nodeResources = nodeResources;
        this.availableResources = availableResources;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourcePool that = (ResourcePool) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(baseResources, that.baseResources) && Objects.equals(nodeResources, that.nodeResources) && Objects.equals(availableResources, that.availableResources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, baseResources, nodeResources, availableResources);
    }

    @Override
    public String toString() {
        return "ResourcePool{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", baseResources=" + baseResources +
                ", nodeResources=" + nodeResources +
                ", availableResources=" + availableResources +
                '}';
    }


}
