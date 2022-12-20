package nl.tudelft.sem.template.users.authorization;

import nl.tudelft.sem.template.users.domain.AccountType;
import nl.tudelft.sem.template.users.domain.EmployeeRepository;
import nl.tudelft.sem.template.users.domain.FacultyAccountRepository;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
import nl.tudelft.sem.template.users.domain.SysadminRepository;
import org.springframework.stereotype.Component;

/**
 * A DDD component for authorizing users.
 */
@Component
public class AuthorizationManager {
    private final transient SysadminRepository sysadminRepository;
    private final transient EmployeeRepository employeeRepository;
    private final transient FacultyAccountRepository facultyAccountRepository;

    /**
     * Constructor for initializing a new authorization service.
     *
     * @param sysadminRepository the sysadmin accounts table
     * @param employeeRepository the employee accounts table
     * @param facultyAccountRepository the faculty accounts table
     */
    public AuthorizationManager(SysadminRepository sysadminRepository,
                                EmployeeRepository employeeRepository,
                                FacultyAccountRepository facultyAccountRepository) {
        this.sysadminRepository = sysadminRepository;
        this.employeeRepository = employeeRepository;
        this.facultyAccountRepository = facultyAccountRepository;
    }

    /**
     * Checks if the specified user is an admin.
     *
     * @param netId the netId of the user.
     * @return if the specified user is an admin.
     */
    public boolean isSysadmin(String netId) {
        return sysadminRepository.existsByNetId(netId);
    }

    /**
     * Checks if the specified user is an employee.
     *
     * @param netId the netId of the user.
     * @return if the specified user is an employee.
     */
    public boolean isEmployee(String netId) {
        return employeeRepository.existsByNetId(netId);
    }

    /**
     * Checks if the specified user is a faculty account.
     *
     * @param netId the netId of the user.
     * @return if the specified user is a faculty account.
     */
    public boolean isFacultyAccount(String netId) {
        return facultyAccountRepository.existsByNetId(netId);
    }

    /**
     * Checks what the access of a User is.
     *
     * @param netId the netId of the user.
     * @return the role of the user as a string.
     * @throws Exception if user is not found or a user has multiple roles.
     */
    public AccountType checkAccess(String netId) throws Exception {
        boolean isEmployee = isEmployee(netId);
        boolean isFacultyAccount = isFacultyAccount(netId);
        boolean isSysadmin = isSysadmin(netId);

        if (!isEmployee && !isFacultyAccount && !isSysadmin) {
            throw new NoSuchUserException("User (" + netId + ") was not registered.");
        } else if (atLeastTwo(isEmployee, isFacultyAccount, isSysadmin)) {
            throw new Exception("User with multiple roles!!!");
        }

        if (isEmployee) {
            return AccountType.EMPLOYEE;
        } else if (isFacultyAccount) {
            return AccountType.FAC_ACCOUNT;
        } else {
            return AccountType.SYSADMIN;
        }
    }

    /**
     * Helper method that check if at least two of the input
     * conditions are true.
     *
     * @param a first condition
     * @param b second condition
     * @param c third condition
     * @return if at least 2 are true.
     */
    private boolean atLeastTwo(boolean a, boolean b, boolean c) {
        return (a && b) || (a && c) || (b && c);
    }

    /**
     * Returns whether a user with the given netId is of the expected account type.
     *
     * @param netId the netId of the user
     * @param expected the expected account type
     * @return whether the user is of the expected account type
     * @throws Exception if the user does not exist or has multiple roles
     */
    public boolean isOfType(String netId, AccountType expected) throws Exception {
        return checkAccess(netId).equals(expected);
    }
}
