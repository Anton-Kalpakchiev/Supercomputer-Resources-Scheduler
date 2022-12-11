package nl.tudelft.sem.template.users.domain;

import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * A DDD service for authorizing users.
 */
@Service
public class AuthorizationService {
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
    public AuthorizationService(SysadminRepository sysadminRepository,
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
     * Adds a new sysadmin account in the respective repository.
     *
     * @param netId the netId of the new user.
     */
    public void addSysadmin(String netId) {
        Sysadmin newSysadmin = new Sysadmin(netId);
        sysadminRepository.save(newSysadmin);
    }

    /**
     * Adds a new employee account in the respective repository.
     *
     * @param netId the netId of the new user.
     */
    public void addEmployee(String netId) {
        Employee newEmployee = new Employee(netId);
        employeeRepository.save(newEmployee);
    }

    /**
     * Adds a new faculty account in the respective repository.
     *
     * @param netId the netId of the new user.
     * @param assignedFaculty the assigned faculty id.
     */
    public void addFacultyAccount(String netId, int assignedFaculty) {
        FacultyAccount newFacultyAccount = new FacultyAccount(netId, assignedFaculty);
        facultyAccountRepository.save(newFacultyAccount);
    }

    /**
     * Drops an employee from the table with a given netId.
     *
     * @param netId the netId of the employee
     * @return whether the employee was dropped
     */
    public boolean dropEmployee(String netId) {
        boolean isFound = employeeRepository.existsByNetId(netId);
        if (!isFound) {
            return false;
        } else {
            employeeRepository.deleteByNetId(netId);
            return true;
        }
    }

    /**
     * Promotes an Employee to a Sysadmin.
     * Only works if the author is a Sysadmin and the toBePromoted is an Employee.
     *
     * @param authorNetId the netId of the author of the request.
     * @param toBePromotedNetId the netId of the employee to be promoted.
     * @throws Exception if the request is unauthorized or there is no such employee
     */
    public void promoteEmployeeToSysadmin(String authorNetId, String toBePromotedNetId) throws Exception {
        if (checkAccess(authorNetId).equals(Sysadmin.class.getSimpleName())) {
            if (checkAccess(toBePromotedNetId).equals(Employee.class.getSimpleName())) {
                dropEmployee(toBePromotedNetId);
                addSysadmin(toBePromotedNetId);
            } else {
                throw new NoSuchUserException("No such employee: " + toBePromotedNetId);
            }
        } else {
            throw new UnauthorizedException("User (" + authorNetId + ") is not a Sysadmin => can not promote");
        }
    }

    /**
     * Checks what the access of a User is.
     *
     * @param netId the netId of the user.
     * @return the role of the user as a string.
     * @throws Exception if user is not found or a user has multiple roles.
     */
    public String checkAccess(String netId) throws Exception {
        boolean isEmployee = isEmployee(netId);
        boolean isFacultyAccount = isFacultyAccount(netId);
        boolean isSysadmin = isSysadmin(netId);

        if (!isEmployee && !isFacultyAccount && !isSysadmin) {
            throw new NoSuchUserException("User (" + netId + ") was not registered.");
        } else if (atLeastTwo(isEmployee, isFacultyAccount, isSysadmin)) {
            throw new Exception("User with multiple roles!!!");
        }

        if (isEmployee) {
            return Employee.class.getSimpleName();
        } else if (isFacultyAccount) {
            return FacultyAccount.class.getSimpleName();
        } else {
            return Sysadmin.class.getSimpleName();
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
}
