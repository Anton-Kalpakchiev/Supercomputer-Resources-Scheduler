package nl.tudelft.sem.template.users.facade;

import java.util.Set;
import lombok.AllArgsConstructor;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.AccountType;
import nl.tudelft.sem.template.users.domain.EmployeeRepository;
import nl.tudelft.sem.template.users.domain.EmploymentException;
import nl.tudelft.sem.template.users.domain.FacultyAccountService;
import nl.tudelft.sem.template.users.domain.FacultyException;
import nl.tudelft.sem.template.users.domain.FacultyVerificationService;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VerificationService {
    private AuthorizationManager authorization;
    private FacultyAccountService facultyAccountService;
    private FacultyVerificationService facultyVerificationService;
    private EmployeeRepository employeeRepository;

    /**
     * Authenticate a faculty manager.
     *
     * @param netId the netId of the user
     * @param providedFacultyId the provided faculty id
     * @return whether the faculty manager is validated.
     * @throws NoSuchUserException when no user is found
     * @throws FacultyException when the faculty doesn't exist
     */
    public boolean authenticateFacultyManager(String netId, long providedFacultyId, String token)
            throws NoSuchUserException, FacultyException {
        if (authorization.isOfType(netId, AccountType.FAC_ACCOUNT)) {
            if (facultyAccountService.getFacultyAssignedId(netId) == providedFacultyId) {
                return facultyVerificationService.verifyFaculty(providedFacultyId, token);
            }
        }
        return false;
    }

    /**
     * Authenticates a request to view the available resources for tomorrow.
     *
     * @param authorNetId the netId of the author of the request
     * @param token the authentication token
     * @param facultyId the provided faculty id
     * @return whether the user is authenticated
     * @throws FacultyException the faculty could not be found
     * @throws NoSuchUserException the user could not be found
     * @throws UnauthorizedException the user was not authorized
     * @throws EmploymentException the user was not employed at the relevant faculty
     */
    public boolean authenticateFacultyRequest(String authorNetId, String token, long facultyId)
            throws FacultyException, NoSuchUserException, UnauthorizedException, EmploymentException {
        try {
            facultyVerificationService.verifyFaculty(facultyId, token);
        } catch (FacultyException e) {
            throw new FacultyException("The faculty does not exist!");
        }
        if (authorization.isOfType(authorNetId, AccountType.SYSADMIN)) {
            return true;
        } else if (authorization.isOfType(authorNetId, AccountType.FAC_ACCOUNT)) {
            return facultyAccountService.getFacultyAssignedId(authorNetId) == facultyId;
        } else if (authorization.isOfType(authorNetId, AccountType.EMPLOYEE)) {
            if (employeeRepository.findByNetId(authorNetId).isPresent()) {
                Set<Long> faculties = employeeRepository.findByNetId(authorNetId).get().getParentFacultyIds();
                return faculties.contains(facultyId);
            } else {
                throw new EmploymentException("Employee was not employed at this faculty");
            }
        } else {
            throw new UnauthorizedException("Request to view schedules failed.");
        }
    }

    /**
     * Retrieves the faculty id from a faculty manager's netID.
     *
     * @param netId the author's netId
     * @return the returned facultyID
     * @throws NoSuchUserException thrown when the user is not found
     */
    public long retrieveFacultyId(String netId) throws NoSuchUserException {
        return facultyAccountService.getFacultyAssignedId(netId);
    }
}
