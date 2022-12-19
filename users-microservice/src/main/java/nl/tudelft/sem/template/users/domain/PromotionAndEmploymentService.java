package nl.tudelft.sem.template.users.domain;

import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.models.FacultyCreationRequestModel;
import nl.tudelft.sem.template.users.models.FacultyCreationResponseModel;
import nl.tudelft.sem.template.users.models.TemporaryRequestModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    private final transient RestTemplate restTemplate;

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
        restTemplate = new RestTemplate();
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

    /**
     * Method for creating a faculty by calling the microservice Resource Pool.
     *
     * @param authorNetId the netId of the author of the request.
     * @param managerNetId the netid of the manager of the request.
     * @param facultyName the new faculty name.
     * @param token the token of the request.
     * @throws Exception if a user is unauthorized or does not exist
     */
    public void createFaculty(String authorNetId, String managerNetId, String facultyName, String token) throws Exception {
        if (authorization.isOfType(authorNetId, AccountType.SYSADMIN)) {
            if (authorization.isOfType(managerNetId, AccountType.EMPLOYEE)) {
                String url = "http://localhost:8085/createFaculty";

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(token);
                HttpEntity<TemporaryRequestModel> entity = new HttpEntity<>(
                        //TODO: change to string
                        new TemporaryRequestModel(facultyName, managerNetId.hashCode()), headers);

                FacultyCreationResponseModel result = restTemplate.postForObject(url, entity,
                        FacultyCreationResponseModel.class);

                System.out.println(result.getFacultyId());
                registrationService.dropEmployee(managerNetId);
                registrationService.addFacultyAccount(managerNetId, (int) result.getFacultyId());

            } else {
                throw new NoSuchUserException("No such employee: " + managerNetId);
            }
        } else {
            throw new UnauthorizedException("User (" + authorNetId + ") is not a Sysadmin => can not create a faculty");
        }
    }
}
