package nl.tudelft.sem.template.users.domain;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FacultyAccountService {

    private FacultyAccountRepository facultyAccountRepository;

    /**
     * Gets the id of the assigned faculty of the faculty account.
     *
     * @param netId the netId of the user
     * @return the id of the faculty
     * @throws NoSuchUserException when the user cannot be found in the repository
     */
    public long getFacultyAssignedId(String netId) throws NoSuchUserException {
        if (facultyAccountRepository.findByNetId(netId).isPresent()) {
            return facultyAccountRepository.findByNetId(netId).get().getAssignedFacultyId();
        } else {
            throw new NoSuchUserException("No such user was found");
        }
    }

}
