package nl.tudelft.sem.template.nodes.domain.node.chain;

import nl.tudelft.sem.template.nodes.domain.node.NodeRepository;

/**
 * The Handler that checks whether the requester is really the owner of the node.
 */
public class ValidOwnerHandler extends BaseHandler {

    private final transient String requesterNetId;

    /**
     * Constructs a ValidOwnerHandler.
     *
     * @param repo the NodeRepository of the system
     * @param requesterNetId the netId of the employee who made the request
     */
    public ValidOwnerHandler(NodeRepository repo, String requesterNetId) {
        super(repo);
        this.requesterNetId = requesterNetId;
    }

    /**
     * Verifies whether the requester is really the owner of the node.
     *
     * @param nodeId the id which is passed around and used to get the necessary information
     * @return whether the next handler passed recursively
     * @throws InvalidRequestException if the requester isn't the owner of the node
     */
    @Override
    public boolean handle(long nodeId) throws InvalidRequestException {
        String ownerNetId = super.getRepo().findById(nodeId).get().getOwnerNetId();

        if (!ownerNetId.equals(requesterNetId)) {
            throw new InvalidOwnerException(nodeId);
        }
        return super.checkNext(nodeId);
    }
}
