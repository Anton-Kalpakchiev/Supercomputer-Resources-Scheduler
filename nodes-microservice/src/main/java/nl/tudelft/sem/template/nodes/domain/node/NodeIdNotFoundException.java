package nl.tudelft.sem.template.nodes.domain.node;

/**
 * Exception to indicate that the node id couldn't be found in the system.
 */
public class NodeIdNotFoundException extends Exception {
    static final long serialVersionUID = -3387516993524229948L;

    public NodeIdNotFoundException(long nodeId) {
        super(String.valueOf(nodeId));
    }
}