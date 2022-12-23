package nl.tudelft.sem.template.nodes.domain.node.chain;

/**
 * Exception to indicate that the faculty id couldn't be found in the resourcePool microservice.
 */
public class FacultyDoesNotExistException extends InvalidRequestException {
    static final long serialVersionUID = -3387516993524229948L;

    public FacultyDoesNotExistException(long nodeId) {
        super(nodeId);
    }
}