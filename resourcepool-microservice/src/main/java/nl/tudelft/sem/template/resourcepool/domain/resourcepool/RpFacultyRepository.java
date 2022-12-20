package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RpFacultyRepository  extends JpaRepository<ResourcePool, Long> {
    /**
     * Find RP/Faculty by id.
     */
    Optional<ResourcePool> findById(long id);

    /**
     * Find RP/Faculty by name.
     */
    Optional<ResourcePool> findByName(String name);

    /**
     * Checks if a RP/Faculty exists by name.
     */
    boolean existsByName(String name);

    /**
     * Checks if a RP/Faculty exists by managerNetId.
     */
    boolean existsByManagerNetId(String managerNetId);
}
