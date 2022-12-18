package nl.tudelft.sem.template.users.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "employee_accounts")
public class Employee extends User {

    @Column(name = "parentFacultyId", nullable = true, unique = false)
    private long parentFacultyId;

    /**
     * Constructor for the regular employee.
     *
     * @param netId the netId of the employee
     * @param parentFacultyId the parent faculty this employee works in.
     */
    public Employee(String netId, long parentFacultyId) {
        super(netId);
        this.parentFacultyId = parentFacultyId;
    }

    /**
     * An empty constructor as required by the annotation.
     */
    public Employee() {
        super("");
        this.parentFacultyId = -1;
    }

    /**
     * A constructor for an employee who is not yet employed.
     *
     * @param netId the netId of the user.
     */
    public Employee(String netId) {
        super(netId);
        this.parentFacultyId = -1;
    }

    /**
     * Getter for the parent faculties.
     *
     * @return the parent faculties.
     */
    public long getParentFacultyId() {
        return parentFacultyId;
    }

    /**
     * Setter for the parent faculties.
     *
     * @param parentFacultyId the new parent faculty id.
     */
    public void setParentFacultyId(long parentFacultyId) {
        this.parentFacultyId = parentFacultyId;
    }

    /**
     * String representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        if (parentFacultyId == -1) {
            return "Employee with netId: " + netId + " -> no faculty.";
        } else {
            return "Employee with netId: " + netId + " -> at faculty " + parentFacultyId;
        }
    }
}
