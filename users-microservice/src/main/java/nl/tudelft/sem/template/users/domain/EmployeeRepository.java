package nl.tudelft.sem.template.users.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    /**
     * Find user by NetID.
     */
    Optional<Employee> findByNetId(String netId);

    /**
     * Check if an existing user already uses a NetID.
     */
    boolean existsByNetId(String netId);
}