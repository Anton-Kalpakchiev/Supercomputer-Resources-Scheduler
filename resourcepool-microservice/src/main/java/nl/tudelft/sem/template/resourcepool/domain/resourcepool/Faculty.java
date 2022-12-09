package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;


@Entity
@Table(name = "RPFaculty")//contains both ResourcePools(which we should have only 1, the free resource pool) and Faculties
@NoArgsConstructor
public class Faculty extends ResourcePool{

    @Column(name = "managerID")//if null, it's a RP, not a faculty
    private long managerID;



    public Faculty(long id, String name, Resources baseResources, Resources nodeResources, Resources availableResources, long managerID) {
        super(id, name, baseResources, nodeResources, availableResources);
        this.managerID = managerID;
    }

    public long getManagerID() {
        return managerID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Faculty faculty = (Faculty) o;
        return managerID == faculty.managerID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), managerID);
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "id=" + super.getId() +
                ", name='" + super.getName() + '\'' +
                ", baseResources=" + super.getBaseResources() +
                ", nodeResources=" + super.getNodeResources() +
                ", availableResources=" + super.getAvailableResources() +
                ", managerID=" + managerID +
                '}';
    }
}
