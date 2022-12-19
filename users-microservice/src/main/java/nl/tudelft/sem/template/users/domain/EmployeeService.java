package nl.tudelft.sem.template.users.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.models.ResourcesDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;



@Service
@AllArgsConstructor
public class EmployeeService {

    private EmployeeRepository employeeRepository;
    private AuthorizationManager authorizationManager;

    private FacultyAccountService facultyAccountService;

    /**
     * Gets the employee if they exist.
     *
     * @param netId the users netId
     */
    public Set<Long> getParentFacultyId(String netId) throws NoSuchUserException {
        if (employeeRepository.findByNetId(netId).isPresent()) {
            return employeeRepository.findByNetId(netId).get().getParentFacultyIds();
        } else {
            throw new NoSuchUserException("No such user was found");
        }
    }

    /**
     * Authenticates an employee assignment request from a user.
     *
     * @param employerNetId the netId of the employer
     * @param employeeNetId the netId of the employee
     * @param facultyIds the ids of the faculties
     * @throws NoSuchUserException user not found
     * @throws EmploymentException employment handled incorrectly
     * @throws UnauthorizedException user is not authorized.
     */
    public Set<Long> authenticateEmploymentAssignmentRequest(String employerNetId, String employeeNetId, Set<Long> facultyIds) throws NoSuchUserException, EmploymentException, UnauthorizedException {
        if (authorizationManager.isSysadmin(employerNetId)) {
            for (Long facultyId : facultyIds) {
                assignFaculty(employeeNetId, facultyId);
            }
            return facultyIds;
        } else if (authorizationManager.isFacultyAccount(employerNetId)) {
            long employerFacultyId = facultyAccountService.getFacultyAssignedId(employerNetId);
            if (facultyIds.contains(employerFacultyId)) {
                assignFaculty(employeeNetId, employerFacultyId);
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
     * Authenticates an employee removal request from a user.
     *
     * @param employerNetId the netId of the employer
     * @param employeeNetId the netId of the employee
     * @param facultyIds the ids of the faculties
     * @throws NoSuchUserException user not found
     * @throws EmploymentException employment handled incorrectly
     * @throws UnauthorizedException user is not authorized.
     */
    public Set<Long> authenticateEmploymentRemovalRequest(String employerNetId, String employeeNetId, Set<Long> facultyIds) throws NoSuchUserException, EmploymentException, UnauthorizedException {
        if (authorizationManager.isSysadmin(employerNetId)) {
            for (Long facultyId : facultyIds) {
                removeFaculty(employeeNetId, facultyId);
            }
            return facultyIds;
        } else if (authorizationManager.isFacultyAccount(employerNetId)) {
            long employerFacultyId = facultyAccountService.getFacultyAssignedId(employerNetId);
            if (facultyIds.contains(employerFacultyId)) {
                removeFaculty(employeeNetId, employerFacultyId);
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
            facultyIds = facultyIds.split(":")[1];
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
    public void assignFaculty(String netId, long facultyId) throws NoSuchUserException, EmploymentException {
        if (employeeRepository.findByNetId(netId).isPresent()) {
            Employee employee = employeeRepository.findByNetId(netId).get();
            if (employee.getParentFacultyIds().contains(facultyId)) {
                throw new EmploymentException(netId + " is already employed at faculty " + facultyId);
            } else {
                employee.getParentFacultyIds().add(facultyId);
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
    public void removeFaculty(String netId, long facultyId) throws NoSuchUserException, EmploymentException {
        if (employeeRepository.findByNetId(netId).isPresent()) {
            Employee employee = employeeRepository.findByNetId(netId).get();
            if (!employee.getParentFacultyIds().contains(facultyId)) {
                throw new EmploymentException(netId + " is not employed at faculty " + facultyId);
            } else {
                employee.getParentFacultyIds().remove(facultyId);
            }
        } else {
            throw new NoSuchUserException("No such user was found");
        }
    }

    /**
     * Retrieves the resources for an employees faculty for the next day.
     *
     * @param netId the users netId
     * @return the Resources for tomorrow
     * @throws NoSuchUserException thrown if the employee cannot be found.
     */
    public ResourcesDto getResourcesForTomorrow(String netId, String token) throws NoSuchUserException,
                                                                                JsonProcessingException {
        Set<Long> facultyIds = this.getParentFacultyId(netId);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + token);

        String requestBody = "{\"resourcePoolId\": \"" + facultyIds + "\"}";
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8085/availableFacultyResources", request, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getBody(), ResourcesDto.class);
    }

}
