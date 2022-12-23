package nl.tudelft.sem.template.nodes.domain.node.chain;

/**
 * A general exception to indicate that the request made wasn't valid.
 */
public abstract class InvalidRequestException extends Exception {
    static final long serialVersionUID = -3387516993524229948L;

    public InvalidRequestException(long nodeId) {
        super(String.valueOf(nodeId));
    }
}