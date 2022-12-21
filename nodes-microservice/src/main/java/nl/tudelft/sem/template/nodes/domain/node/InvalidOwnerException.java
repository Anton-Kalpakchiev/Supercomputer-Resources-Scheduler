package nl.tudelft.sem.template.nodes.domain.node;

/**
 * Exception to indicate that the request isn't made by the owner of the node.
 */
public class InvalidOwnerException extends Exception {
    static final long serialVersionUID = -3387516993524229948L;

    public InvalidOwnerException(String ownerNetId) {
        super(ownerNetId);
    }
}