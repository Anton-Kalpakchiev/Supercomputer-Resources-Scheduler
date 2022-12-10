package nl.tudelft.sem.template.users.domain;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "FACULTY_ACCOUNTS")
public class FacultyAccount extends User {
    @Column(name = "faculty", nullable = false, unique = true)
    private int assignedFacultyId;

    /**
     * Constructor for the faculty account.
     *
     * @param netId the netId of the faculty account
     * @param assignedFacultyId the corresponding faculty.
     */
    public FacultyAccount(String netId, int assignedFacultyId) {
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
    public int getAssignedFacultyId() {
        return assignedFacultyId;
    }

    /**
     * Setter for the corresponding faculty.
     *
     * @param assignedFacultyId the new faculty.
     */
    public void setAssignedFacultyId(int assignedFacultyId) {
        this.assignedFacultyId = assignedFacultyId;
    }
}
