package nl.tudelft.sem.template.users.domain;

import org.springframework.stereotype.Service;

/**
 * A DDD service for registering a new user.
 */
@Service
public class RegistrationService {
    private final transient SysadminRepository sysadminRepository;
    private final transient EmployeeRepository employeeRepository;
    private final transient FacultyAccountRepository facultyAccountRepository;

    /**
     * Instanciates a new Registration service.
     *
     * @param sysadminRepository the system admin repository
     * @param employeeRepository the employee repository
     * @param facultyAccountRepository the faculty repository
     */
    public RegistrationService(SysadminRepository sysadminRepository,
                               EmployeeRepository employeeRepository,
                               FacultyAccountRepository facultyAccountRepository) {
        this.sysadminRepository = sysadminRepository;
        this.employeeRepository = employeeRepository;
        this.facultyAccountRepository = facultyAccountRepository;
    }

    /**
     * Register a new user. If the user has a netId of admin he is added a sysadmin.
     *
     * @param netId The NetID of the user
     */
    public User registerUser(String netId) {
        if (netId.equals("admin")) {
            Sysadmin admin = new Sysadmin(netId);
            sysadminRepository.save(admin);
            System.out.println(netId + " was added as an admin.");
            return admin;
        } else {
            Employee employee = new Employee(netId);
            employeeRepository.save(employee);
            System.out.println(netId + " was added as an employee.");
            return employee;
        }
    }

    /**
     * Adds a new sysadmin account in the respective repository.
     *
     * @param netId the netId of the user
     * @return the newly created Sysadmin.
     */
    public Sysadmin addSysadmin(String netId) {
        Sysadmin newSysadmin = new Sysadmin(netId);
        sysadminRepository.save(newSysadmin);
        return newSysadmin;
    }

    /**
     * Adds a new employee account in the respective repository.
     *
     * @param netId the netId of the user
     * @return the newly created Employee.
     */
    public Employee addEmployee(String netId) {
        Employee newEmployee = new Employee(netId);
        employeeRepository.save(newEmployee);
        return newEmployee;
    }

    /**
     * Adds a new faculty account in the respective repository.
     *
     * @param netId the netId of the user
     * @param assignedFaculty the assigned faculty of the user.
     * @return the newly created faculty account.
     */
    public FacultyAccount addFacultyAccount(String netId, long assignedFaculty) {
        FacultyAccount newFacultyAccount = new FacultyAccount(netId, assignedFaculty);
        facultyAccountRepository.save(newFacultyAccount);
        return newFacultyAccount;
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
}
