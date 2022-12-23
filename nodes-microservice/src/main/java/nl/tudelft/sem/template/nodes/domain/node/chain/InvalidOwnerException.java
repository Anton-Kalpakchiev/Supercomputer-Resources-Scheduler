package nl.tudelft.sem.template.nodes.domain.node.chain;

import nl.tudelft.sem.template.nodes.domain.node.chain.InvalidRequestException;

/**
 * Exception to indicate that the request isn't made by the owner of the node.
 */
public class InvalidOwnerException extends InvalidRequestException {
    static final long serialVersionUID = -3387516993524229948L;

    public InvalidOwnerException(long nodeId) {
        super(nodeId);
    }
}