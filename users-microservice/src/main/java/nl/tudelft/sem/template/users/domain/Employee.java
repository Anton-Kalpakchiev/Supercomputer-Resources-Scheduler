package nl.tudelft.sem.template.users.domain;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "employee_accounts")
public class Employee extends User {

    @ElementCollection
    @Column(name = "parentFacultyId", nullable = true, unique = false)
    private Set<Long> parentFacultyIds;

    /**
     * Constructor for the regular employee.
     *
     * @param netId the netId of the employee
     * @param parentFacultyIds the parent faculty this employee works in.
     */
    public Employee(String netId, Set<Long> parentFacultyIds) {
        super(netId);
        this.parentFacultyIds = parentFacultyIds;
    }

    /**
     * An empty constructor as required by the annotation.
     */
    public Employee() {
        super("");
        this.parentFacultyIds = new HashSet<>();
    }

    /**
     * A constructor for an employee who is not yet employed.
     *
     * @param netId the netId of the user.
     */
    public Employee(String netId) {
        super(netId);
        this.parentFacultyIds = new HashSet<>();
    }

    /**
     * Getter for the parent faculties.
     *
     * @return the parent faculties.
     */
    public Set<Long> getParentFacultyIds() {
        return parentFacultyIds;
    }

    /**
     * Setter for the parent faculties.
     *
     * @param parentFacultyIds the new parent faculty id.
     */
    public void setParentFacultyIds(Set<Long> parentFacultyIds) {
        this.parentFacultyIds = parentFacultyIds;
    }

    /**
     * String representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        if (parentFacultyIds.isEmpty()) {
            return "Employee with netId: " + netId + " -> no faculty.";
        } else {
            return "Employee with netId: " + netId + " -> at faculty " + parentFacultyIds.toString();
        }
    }
}
