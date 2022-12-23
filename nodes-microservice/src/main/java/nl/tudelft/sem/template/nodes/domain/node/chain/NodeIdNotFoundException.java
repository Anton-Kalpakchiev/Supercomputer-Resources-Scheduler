package nl.tudelft.sem.template.nodes.domain.node.chain;

/**
 * Exception to indicate that the node id couldn't be found in the system.
 */
public class NodeIdNotFoundException extends InvalidRequestException {
    static final long serialVersionUID = -3387516993524229948L;

    public NodeIdNotFoundException(long nodeId) {
        super(nodeId);
    }
}