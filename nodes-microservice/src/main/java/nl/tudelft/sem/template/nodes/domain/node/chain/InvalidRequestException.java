package nl.tudelft.sem.template.nodes.domain.node.chain;

/**
 * Exception to indicate that the request made wasn't valid.
 */
public class InvalidRequestException extends Exception {
    static final long serialVersionUID = -3387516993524229948L;

    public InvalidRequestException(long nodeId) {
        super(String.valueOf(nodeId));
    }
}