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
        if (checkFacultyAccount(netId, providedFacultyId)) {
            return facultyVerificationService.verifyFaculty(providedFacultyId, token);
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
        throws NoSuchUserException, UnauthorizedException, EmploymentException, FacultyException {
        checkFacultyExists(token, facultyId);
        if (checkSysAdmin(authorNetId)) {
            return true;
        } else if (checkFacultyAccount(authorNetId, facultyId)) {
            return true;
        } else if (checkEmployee(authorNetId, facultyId)) {
            return true;
        } else {
            throw new UnauthorizedException("Request to view schedules failed.");
        }
    }

    /**
     * Checks whether the given facultyId has an existing matching faculty.
     *
     * @param token the jwtToken
     * @param facultyId the id of the faculty to check
     * @throws FacultyException if the given faculty does not exist
     */
    private void checkFacultyExists(String token, long facultyId) throws FacultyException {
        try {
            facultyVerificationService.verifyFaculty(facultyId, token);
        } catch (FacultyException e) {
            throw new FacultyException("The faculty does not exist!");
        }
    }

    /**
     * Checks whether the netId belongs to a SysAdmin.
     *
     * @param authorNetId the NetId to check the access for
     * @return whether the authorNetId belongs to a SysAdmin
     * @throws NoSuchUserException if the NetId does not belong to an existing user
     */
    private boolean checkSysAdmin(String authorNetId) throws NoSuchUserException {
        return authorization.isOfType(authorNetId, AccountType.SYSADMIN);
    }

    /**
     * Checks whether the netId belongs to a FacultyAccount and whether the Faculty the FacultyAccount belongs to is that
     * of the given facultyId.
     *
     * @param authorNetId the NetId to check for
     * @param facultyId the FacultyId to check for
     * @return the NetId belongs to a FacultyAccount and is the manager of the faculty of the given facultyId
     * @throws NoSuchUserException if the NetId does not belong to an existing user
     */
    private boolean checkFacultyAccount(String authorNetId, long facultyId) throws NoSuchUserException {
        if(!authorization.isOfType(authorNetId, AccountType.FAC_ACCOUNT)) {
            return false;
        }
        return facultyAccountService.getFacultyAssignedId(authorNetId) == facultyId;
    }

    /**
     * Checks whether the netId belongs to an Employee and whether the Employee is employed at the Faculty of the given
     * facultyId.
     *
     * @param authorNetId the NetId to check for
     * @param facultyId the FacultyId to check for
     * @return the NetId belongs to an Employee and is employed at the faculty of the given facultyId
     * @throws NoSuchUserException if the NetId does not belong to an existing user
     */
    private boolean checkEmployee(String authorNetId, long facultyId) throws NoSuchUserException, EmploymentException {
        if(!authorization.isOfType(authorNetId, AccountType.EMPLOYEE)) {
            return false;
        }
        if (employeeRepository.findByNetId(authorNetId).isPresent()) {
            Set<Long> faculties = employeeRepository.findByNetId(authorNetId).get().getParentFacultyIds();
            return faculties.contains(facultyId);
        } else {
            throw new EmploymentException("Employee was not employed at this faculty.");
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
