package nl.tudelft.sem.template.requests.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A DDD repository for quering and persisting user aggregate roots.
 */
@Repository
public interface RequestRepository extends JpaRepository<AppRequest, String> {
    /**
     * Find request by id.
     */
    Optional<AppRequest> findById(long id);
}
