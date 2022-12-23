package nl.tudelft.sem.template.nodes.domain.node.chain;

import nl.tudelft.sem.template.nodes.domain.node.NodeRepository;

/**
 * The Handler that checks whether a nodeId exists in the system.
 */
public class NodeExistenceHandler extends BaseHandler {

    /**
     * Constructs a NodeExistenceHandler.
     *
     * @param repo the NodeRepository of the system
     */
    public NodeExistenceHandler(NodeRepository repo) {
        super(repo);
    }

    /**
     * Verifies whether a nodeId exists in the system.
     *
     * @param nodeId the id which is passed around and used to get the necessary information
     * @return whether the next handler passed recursively
     * @throws InvalidRequestException if the nodeId doesn't exist
     */
    @Override
    public boolean handle(long nodeId) throws InvalidRequestException {
        if (!super.getRepo().existsById(nodeId)) {
            throw new NodeIdNotFoundException(nodeId);
        }
        return super.checkNext(nodeId);
    }
}
