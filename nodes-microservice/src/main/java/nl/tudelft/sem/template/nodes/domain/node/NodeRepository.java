package nl.tudelft.sem.template.nodes.domain.node;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A DDD repository for quering and persisting node aggregate roots.
 */
@Repository
public interface NodeRepository extends JpaRepository<Node, Integer> {

    Optional<Node> findByName(Name name);

    /**
     * Check if an existing node already uses a name.
     */
    boolean existsByName(Name name);

    boolean existsByUrl(NodeUrl url);

    boolean existsByToken(Token token);
}
