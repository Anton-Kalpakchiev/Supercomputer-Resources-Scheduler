package nl.tudelft.sem.template.users.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    /**
     * Find user by NetID.
     */
    Optional<User> findByNetId(NetId netId);

    /**
     * Check if an existing user already uses a NetID.
     */
    boolean existsByNetId(NetId netId);
}
