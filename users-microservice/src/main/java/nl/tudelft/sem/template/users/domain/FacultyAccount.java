package nl.tudelft.sem.template.users.domain;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "facultyAccounts")
@NoArgsConstructor
public class FacultyAccount extends User {
    @Column(name = "faculty", nullable = false, unique = true)
    @Convert(converter = FacultyConverter.class)
    private Faculty assignedFaculty;

    /**
     * Constructor for the faculty account.
     *
     * @param netId the netId of the faculty account
     * @param hashedPassword the hashed password of the faculty account
     * @param assignedFaculty the corresponding faculty.
     */
    public FacultyAccount(NetId netId, HashedPassword hashedPassword, Faculty assignedFaculty) {
        super(netId, hashedPassword);
        this.assignedFaculty = assignedFaculty;
    }

    /**
     * Getter for the corresponding faculty.
     *
     * @return the corresponding faculty.
     */
    public Faculty getAssignedFaculty() {
        return assignedFaculty;
    }
}
