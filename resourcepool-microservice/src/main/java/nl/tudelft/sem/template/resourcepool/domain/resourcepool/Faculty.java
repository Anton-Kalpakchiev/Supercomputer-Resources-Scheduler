package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;


/**
 * The type Faculty.
 */
@Entity
@Table(name = "RpFaculty")//contains both ResourcePools(which we should have only 1, the free resource pool) and Faculties
@NoArgsConstructor
public class Faculty extends ResourcePool {

    @Column(name = "managerId")//if null, it's a RP, not a faculty
    private long managerId;


    /**
     * Instantiates a new Faculty with the specified id and name and faculty manager id,
     * the other fields will be set to empty recourses.
     *
     * @param id        the id
     * @param name      the name
     * @param managerId the manager id
     */
    public Faculty(long id, String name, long managerId) {
        super(id, name);
        this.managerId = managerId;
    }

    /**
     * Gets manager id.
     *
     * @return the manager id
     */
    public long getManagerId() {
        return managerId;
    }

    /**
     * Equality is only based on the identifier, just like the parent class.
     *
     * @return whether the faculties are equal
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    /**
     * Returns the hash code value for this faculty.
     *
     * @return the hash code value for this faculty
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), managerId);
    }

    /**
     * Returns a string representation for this faculty.
     *
     * @return a string representation for this faculty
     */
    @Override
    public String toString() {
        return "Faculty{"
                + "id=" + super.getId()
                + ", name='" + super.getName() + '\''
                + ", baseResources=" + super.getBaseResources()
                + ", nodeResources=" + super.getNodeResources()
                + ", availableResources=" + super.getAvailableResources()
                + ", managerId=" + managerId
                + '}';
    }
}
