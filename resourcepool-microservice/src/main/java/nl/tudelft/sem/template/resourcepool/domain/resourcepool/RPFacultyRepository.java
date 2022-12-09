package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RPFacultyRepository  extends JpaRepository<ResourcePool, Long> {
    /**
     * Find RP/Faculty by id.
     */
    Optional<ResourcePool> findById(long id);

}
