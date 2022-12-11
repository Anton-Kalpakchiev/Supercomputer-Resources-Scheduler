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

}
