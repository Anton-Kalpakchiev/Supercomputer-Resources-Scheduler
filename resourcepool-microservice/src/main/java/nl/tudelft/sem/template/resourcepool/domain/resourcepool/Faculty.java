package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.NoArgsConstructor;


/**
 * The type Faculty.
 */
@Entity
@Table(name = "RpFaculty")//contains both ResourcePools(which we should have only 1, the free resource pool) and Faculties
@NoArgsConstructor
public class Faculty extends ResourcePool {

    @Column(name = "managerNetId")//if null, it's a RP, not a faculty
    private String managerNetId;


    /**
     * Instantiates a new Faculty with the specified id and name and faculty manager id,
     * the other fields will be set to empty recourses.
     *
     * @param name      the name
     * @param managerNetId the manager id
     */
    public Faculty(String name, String managerNetId) {
        super(name);
        this.managerNetId = managerNetId;
        this.recordThat(new FacultyWasCreatedEvent(name));
    }

    /**
     * Gets manager id.
     *
     * @return the manager id
     */
    public String getManagerNetId() {
        return managerNetId;
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
        return Objects.hash(super.hashCode(), managerNetId);
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
                + ", baseResources=(" + super.getBaseResources()
                + "), nodeResources=(" + super.getNodeResources()
                + "), managerNetId=" + managerNetId
                + '}';
    }
}
