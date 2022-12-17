package nl.tudelft.sem.template.users.domain;

public enum AccountType {
    EMPLOYEE("Employee"), SYSADMIN("Sysadmin"), FAC_ACCOUNT("FacultyAccount");

    private final String name;

    /**
     * A constructor for the enum.
     *
     * @param name the name of the account type
     */
    AccountType(String name) {
        this.name = name;
    }

    /**
     * Returns a string representation of the account type.
     *
     * @return a string representation of the account type.
     */
    public String getName() {
        return name;
    }
}
