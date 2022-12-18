package nl.tudelft.sem.template.users.domain;

import lombok.AllArgsConstructor;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.models.ResourceDto;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmployeeService {

    private EmployeeRepository employeeRepository;

    /**
     * Gets the employee if they exist.
     *
     * @param netId the users netId
     */
    public long getParentFacultyId(String netId) throws NoSuchUserException {
        if (employeeRepository.findByNetId(netId).isPresent()) {
            return employeeRepository.findByNetId(netId).get().getParentFacultyId();
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
    public ResourceDto getResourcesForTomorrow(String netId) throws NoSuchUserException {
        long facultyId = this.getParentFacultyId(netId);
        //TODO: take implementation from Sophie's branch upon completion
        return new ResourceDto(100, 100, 100);
    }

}
