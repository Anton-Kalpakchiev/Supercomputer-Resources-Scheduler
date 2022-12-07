package nl.tudelft.sem.template.users.domain;

public class Faculty {
    private FacultyAccount manager;

    private String name;

    /**
     * Constructor for the faculty.
     *
     * @param manager the faculty account
     * @param name the name of the faculty
     */
    public Faculty(FacultyAccount manager, String name) {
        this.manager = manager;
        this.name = name;
    }

    /**
     * Getter for the faculty manager account.
     *
     * @return the faculty manager account.
     */
    public FacultyAccount getManager() {
        return manager;
    }

    /**
     * Setter for the faculty manager.
     *
     * @param manager the new manager
     */
    public void setManager(FacultyAccount manager) {
        this.manager = manager;
    }

    /**
     * Setter for the faculty name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the name of the faculty.
     *
     * @return the name of the faculty
     */
    public String getName() {
        return name;
    }

    /**
     * String convertion of the object.
     *
     * @return the string of the object
     */
    @Override
    public String toString() {
        return name;
    }
}
