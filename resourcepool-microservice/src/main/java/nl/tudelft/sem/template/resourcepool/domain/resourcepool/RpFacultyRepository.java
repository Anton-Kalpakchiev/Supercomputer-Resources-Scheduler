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

    boolean existsByName(String name);

    //boolean existsByManagerNetId(String managerNetId);
}
