package nl.tudelft.sem.template.users.domain;

import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import org.springframework.stereotype.Service;

/**
 * A DDD service for promoting employees and employing them to faculties.
 */
@Service
public class PromotionAndEmploymentService {
    private final transient SysadminRepository sysadminRepository;
    private final transient EmployeeRepository employeeRepository;
    private final transient FacultyAccountRepository facultyAccountRepository;

    private final transient RegistrationService registrationService;
    private final transient AuthorizationManager authorization;

    /**
     * Constructor for the Promotions and Employment Service.
     *
     * @param sysadminRepository the sysadmin repository
     * @param employeeRepository the employee repository
     * @param facultyAccountRepository the faculty account repository
     */
    public PromotionAndEmploymentService(SysadminRepository sysadminRepository,
                                         EmployeeRepository employeeRepository,
                                         FacultyAccountRepository facultyAccountRepository) {
        this.sysadminRepository = sysadminRepository;
        this.employeeRepository = employeeRepository;
        this.facultyAccountRepository = facultyAccountRepository;
        registrationService = new RegistrationService(sysadminRepository,
                employeeRepository, facultyAccountRepository);
        authorization = new AuthorizationManager(sysadminRepository,
                employeeRepository, facultyAccountRepository);
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
        if (authorization.isOfType(authorNetId, AccountType.SYSADMIN)) {
            if (authorization.isOfType(toBePromotedNetId, AccountType.EMPLOYEE)) {
                registrationService.dropEmployee(toBePromotedNetId);
                registrationService.addSysadmin(toBePromotedNetId);
            } else {
                throw new NoSuchUserException("No such employee: " + toBePromotedNetId);
            }
        } else {
            throw new UnauthorizedException("User (" + authorNetId + ") is not a Sysadmin => can not promote");
        }
    }
}
