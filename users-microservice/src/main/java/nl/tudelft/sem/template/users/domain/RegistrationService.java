package nl.tudelft.sem.template.users.domain;

import org.springframework.stereotype.Service;

/**
 * A DDD service for registering a new user.
 */
@Service
public class RegistrationService {
    private final transient UserRepository userRepository;

    /**
     * Instantiates a new UserService.
     *
     * @param userRepository  the user repository
     */
    public RegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Register a new user.
     *
     * @param netId    The NetID of the user
     * @throws Exception if the user already exists
     */
    public User registerUser(NetId netId) throws Exception {

        return new Sysadmin(netId);
    }

    public boolean checkNetIdIsUnique(NetId netId) {
        return true;
    }
}
