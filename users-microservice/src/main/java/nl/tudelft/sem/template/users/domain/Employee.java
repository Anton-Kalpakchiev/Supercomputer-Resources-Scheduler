package nl.tudelft.sem.template.users.domain;

import java.util.List;

public class Employee extends User {
    private List<FacultyAccount> parentFaculties;

    /**
     * Constructor for the regular employee.
     *
     * @param netId the netId of the employee
     * @param hashedPassword the hashed password of the employee.
     * @param parentFaculties the parent faculties this employee works in.
     */
    public Employee(NetId netId, HashedPassword hashedPassword, List<FacultyAccount> parentFaculties) {
        super(netId, hashedPassword);
        this.parentFaculties = parentFaculties;
    }

    /**
     * Getter for the parent faculties.
     *
     * @return the parent faculties.
     */
    public List<FacultyAccount> getParentFaculties() {
        return parentFaculties;
    }

    /**
     * Setter for the parent faculties.
     *
     * @param parentFaculties the new parent faculties.
     */
    public void setParentFaculties(List<FacultyAccount> parentFaculties) {
        this.parentFaculties = parentFaculties;
    }
}
