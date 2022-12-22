package nl.tudelft.sem.template.users.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.models.FacultyCreationResponseModel;
import nl.tudelft.sem.template.users.models.TemporaryRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    private final transient FacultyAccountService facultyAccountService;
    private final transient AuthorizationManager authorization;

    private final transient FacultyVerificationService facultyVerificationService;

    private transient RestTemplate restTemplate;

    /**
     * Constructor for the Promotions and Employment Service.
     *
     * @param sysadminRepository the sysadmin repository
     * @param employeeRepository the employee repository
     * @param facultyAccountRepository the faculty account repository
     */
    public PromotionAndEmploymentService(SysadminRepository sysadminRepository,
                                         EmployeeRepository employeeRepository,
                                         FacultyAccountRepository facultyAccountRepository,
                                         RegistrationService registrationService,
                                         AuthorizationManager authorization,
                                         FacultyAccountService facultyAccountService,
                                         FacultyVerificationService facultyVerificationService,
                                         RestTemplate restTemplate) {
        this.sysadminRepository = sysadminRepository;
        this.employeeRepository = employeeRepository;
        this.facultyAccountRepository = facultyAccountRepository;
        this.registrationService = registrationService;
        this.authorization = authorization;
        this.facultyAccountService = facultyAccountService;
        this.facultyVerificationService = facultyVerificationService;
        this.restTemplate = restTemplate;
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
     * @return the id of the new faculty
     * @throws Exception if a user is unauthorized or does not exist
     */
    public long createFaculty(String authorNetId, String managerNetId, String facultyName, String token)
            throws FacultyException, NoSuchUserException, UnauthorizedException {
        if (authorization.isOfType(authorNetId, AccountType.SYSADMIN)) {
            if (authorization.isOfType(managerNetId, AccountType.EMPLOYEE)) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(token);
                HttpEntity<TemporaryRequestModel> entity = new HttpEntity<>(
                        //TODO: change to string
                        new TemporaryRequestModel(facultyName, managerNetId), headers);

                String url = "http://localhost:8085/createFaculty";
                ResponseEntity<FacultyCreationResponseModel> result = restTemplate.postForEntity(url, entity,
                        FacultyCreationResponseModel.class);

                if (result.getStatusCode().is2xxSuccessful()) {
                    registrationService.dropEmployee(managerNetId);
                    registrationService.addFacultyAccount(managerNetId, (int) result.getBody().getFacultyId());

                    return result.getBody().getFacultyId();
                } else {
                    throw new FacultyException(result.getStatusCode().getReasonPhrase());
                }
            } else {
                throw new NoSuchUserException("No such employee: " + managerNetId);
            }
        } else {
            throw new UnauthorizedException("User (" + authorNetId + ") is not a Sysadmin => can not create a faculty");
        }
    }



    /**
     * Authorizes an employee assignment request from a user.
     *
     * @param employerNetId the netId of the employer
     * @param employeeNetId the netId of the employee
     * @param facultyIds the ids of the faculties
     * @throws NoSuchUserException user not found
     * @throws EmploymentException employment handled incorrectly
     * @throws UnauthorizedException user is not authorized.
     */

    @SuppressWarnings({"PMD.AvoidLiteralsInIfCondition"})
    public Set<Long> authorizeEmploymentAssignmentRequest(
            String employerNetId, String employeeNetId, Set<Long> facultyIds, String token)
                throws EmploymentException, NoSuchUserException, UnauthorizedException {
        for (long facultyId : facultyIds) {
            try {
                facultyVerificationService.verifyFaculty(facultyId, token);
            } catch (FacultyException e) {
                throw new RuntimeException(e);
            }
        }
        if (authorization.isOfType(employerNetId, AccountType.SYSADMIN)) {
            for (Long facultyId : facultyIds) {
                assignFacultyToEmployee(employeeNetId, facultyId);
            }
            return facultyIds;
        } else if (authorization.isOfType(employerNetId, AccountType.FAC_ACCOUNT)) {
            long employerFacultyId = facultyAccountService.getFacultyAssignedId(employerNetId);
            if (facultyIds.contains(employerFacultyId)) {
                assignFacultyToEmployee(employeeNetId, employerFacultyId);
                return Set.of(employerFacultyId);
            } else if (facultyIds.size() > 1) {
                facultyIds.remove(employerFacultyId);
                throw new EmploymentException("Faculty manager of faculty " + employerFacultyId
                        + " cannot authorize employment at other faculties: " + facultyIds);
            } else {
                throw new EmploymentException("Faculty manager can only employ employees for their own faculty!");
            }
        } else {
            throw new UnauthorizedException("Employees cannot employ other employees");
        }
    }

    /**
     * Authorizes an employee removal request from a user.
     *
     * @param employerNetId the netId of the employer
     * @param employeeNetId the netId of the employee
     * @param facultyIds the ids of the faculties
     * @throws NoSuchUserException user not found
     * @throws EmploymentException employment handled incorrectly
     * @throws UnauthorizedException user is not authorized.
     */
    @SuppressWarnings({"PMD.AvoidLiteralsInIfCondition"})
    public Set<Long> authorizeEmploymentRemovalRequest(
            String employerNetId, String employeeNetId, Set<Long> facultyIds, String token)
                throws EmploymentException, UnauthorizedException, NoSuchUserException {
        for (long facultyId : facultyIds) {
            try {
                facultyVerificationService.verifyFaculty(facultyId, token);
            } catch (FacultyException e) {
                throw new RuntimeException(e);
            }
        }
        if (authorization.isOfType(employerNetId, AccountType.SYSADMIN)) {
            for (Long facultyId : facultyIds) {
                removeEmployeeFromFaculty(employeeNetId, facultyId);
            }
            return facultyIds;
        } else if (authorization.isOfType(employerNetId, AccountType.FAC_ACCOUNT)) {
            long employerFacultyId = facultyAccountService.getFacultyAssignedId(employerNetId);
            if (facultyIds.contains(employerFacultyId)) {
                removeEmployeeFromFaculty(employeeNetId, employerFacultyId);
                return Set.of(employerFacultyId);
            } else if (facultyIds.size() > 1) {
                facultyIds.remove(employerFacultyId);
                throw new EmploymentException("Faculty manager of faculty " + employerFacultyId
                        + " cannot authorize removal of employment at other faculties: " + facultyIds);
            } else {
                throw new EmploymentException("Faculty manager can only remove employees for their own faculty!");
            }

        } else {
            throw new UnauthorizedException("Employees cannot remove other employees");
        }
    }

    /**
     * Parses the json of a string of faculty ids into a set of longs.
     *
     * @param facultyIds the string faculty id
     * @return the set of facultIds
     * @throws EmploymentException thrown when request is formatted incorrectly
     */
    public Set<Long> parseJsonFacultyIds(String facultyIds) throws EmploymentException {
        try {
            return Arrays.stream(facultyIds.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new EmploymentException("Request was formatted incorrectly.");
        }
    }

    /**
     * Assign a new faculty to a employee.
     *
     * @param netId the netId of the employee
     * @param facultyId the faculty to be assigned to
     * @throws EmploymentException thrown when employee is already employed at given faculty
     */
    public void assignFacultyToEmployee(String netId, long facultyId)
                throws NoSuchUserException, EmploymentException {
        if (employeeRepository.findByNetId(netId).isPresent()) {
            Employee employee = employeeRepository.findByNetId(netId).get();
            if (employee.getParentFacultyIds().contains(facultyId)) {
                throw new EmploymentException(netId + " is already employed at faculty " + facultyId);
            } else {
                if (employee.addFaculty(facultyId)) {
                    employeeRepository.saveAndFlush(employee);
                } else {
                    throw new IllegalArgumentException();
                }
            }
        } else {
            throw new NoSuchUserException("No such user was found");
        }
    }

    /**
     * Remove a faculty an employee was assigned to.
     *
     * @param netId the employee netId
     * @param facultyId the faculty to be removed
     * @throws NoSuchUserException thrown when employee is not found
     * @throws EmploymentException thrown when employee is not employed at given faculty
     */
    public void removeEmployeeFromFaculty(String netId, long facultyId)
                throws NoSuchUserException, EmploymentException {
        if (employeeRepository.findByNetId(netId).isPresent()) {
            Employee employee = employeeRepository.findByNetId(netId).get();
            if (!employee.getParentFacultyIds().contains(facultyId)) {
                throw new EmploymentException(netId + " is not employed at faculty " + facultyId);
            } else {
                Set<Long> newIds = new HashSet<>(Set.copyOf(employee.getParentFacultyIds()));
                newIds.remove(facultyId);
                Employee newEmployee = new Employee(netId, newIds);
                employeeRepository.saveAndFlush(newEmployee);
            }
        } else {
            throw new NoSuchUserException("No such user was found");
        }
    }
}
