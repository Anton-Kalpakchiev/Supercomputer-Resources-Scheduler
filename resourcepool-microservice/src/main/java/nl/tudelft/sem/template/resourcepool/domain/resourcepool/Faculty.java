package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;


@Entity
@Table(name = "RpFaculty")//contains both ResourcePools(which we should have only 1, the free resource pool) and Faculties
@NoArgsConstructor
public class Faculty extends ResourcePool {

    @Column(name = "managerId")//if null, it's a RP, not a faculty
    private long managerId;



    public Faculty(long id, String name, long managerId) {
        super(id, name);
        this.managerId = managerId;
    }

    public long getManagerId() {
        return managerId;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), managerId);
    }

    @Override
    public String toString() {
        return "Faculty{"
                + "id=" + super.getId()
                + ", name='" + super.getName() + '\''
                + ", baseResources=" + super.getBaseResources()
                + ", nodeResources=" + super.getNodeResources()
                + ", availableResources=" + super.getAvailableResources()
                + ", managerID=" + managerId
                + '}';
    }
}
