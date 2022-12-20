package nl.tudelft.sem.template.users.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "faculty_accounts")
public class FacultyAccount extends User {
    @Column(name = "faculty", nullable = false, unique = true)
    private long assignedFacultyId;

    /**
     * Constructor for the faculty account.
     *
     * @param netId the netId of the faculty account
     * @param assignedFacultyId the corresponding faculty.
     */
    public FacultyAccount(String netId, long assignedFacultyId) {
        super(netId);
        this.assignedFacultyId = assignedFacultyId;
    }

    public FacultyAccount() {
        super("");
    }

    /**
     * Getter for the corresponding faculty.
     *
     * @return the corresponding faculty.
     */
    public long getAssignedFacultyId() {
        return assignedFacultyId;
    }

    /**
     * Setter for the corresponding faculty.
     *
     * @param assignedFacultyId the new faculty.
     */
    public void setAssignedFacultyId(long assignedFacultyId) {
        this.assignedFacultyId = assignedFacultyId;
    }
}
