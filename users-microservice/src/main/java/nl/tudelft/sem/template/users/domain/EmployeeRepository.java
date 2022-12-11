package nl.tudelft.sem.template.users.domain;

import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    /**
     * Find user by NetID.
     */
    Optional<Employee> findByNetId(String netId);

    /**
     * Check if an existing user already exists with this NetID.
     */
    boolean existsByNetId(String netId);

    /**
     * Deletes a user with the given netId.
     */
    @Transactional
    void deleteByNetId(String netId);
}