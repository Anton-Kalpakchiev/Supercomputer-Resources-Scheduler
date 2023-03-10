package nl.tudelft.sem.template.users.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.models.ResourcesDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class EmployeeService {
    private transient EmployeeRepository employeeRepository;
    private transient AuthorizationManager authorizationManager;
    private transient FacultyAccountService facultyAccountService;

    /**
     * Gets the set of parent faculty ids of employee if the employee exists.
     *
     * @param netId the users netId
     * @throws NoSuchUserException when the user is not found
     */
    public Set<Long> getParentFacultyId(String netId) throws NoSuchUserException {
        if (employeeRepository.findByNetId(netId).isPresent()) {
            return employeeRepository.findByNetId(netId).get().getParentFacultyIds();
        } else {
            throw new NoSuchUserException("No such user was found");
        }
    }


    /**
     * Gets the employee if they exist.
     *
     * @param netId the users netId
     * @return the Employee
     * @throws NoSuchUserException thrown when user is not found
     */
    public Employee getEmployee(String netId) throws NoSuchUserException {
        if (employeeRepository.findByNetId(netId).isPresent()) {
            return employeeRepository.findByNetId(netId).get();
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
    public ResourcesDto getResourcesForTomorrow(String netId, String token, RestTemplate restTemplate)
            throws NoSuchUserException, JsonProcessingException {
        Set<Long> facultyIds = this.getParentFacultyId(netId);

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
