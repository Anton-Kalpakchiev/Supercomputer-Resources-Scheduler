package nl.tudelft.sem.template.nodes.domain.node.chain;

import nl.tudelft.sem.template.nodes.domain.node.NodeRepository;

public class ValidOwnerHandler extends BaseHandler {

    private final String requesterNetId;

    public ValidOwnerHandler(NodeRepository repo, String requesterNetId) {
        super(repo);
        this.requesterNetId = requesterNetId;
    }

    @Override
    public boolean handle(long nodeId) throws InvalidRequestException {
        String ownerNetId = super.getRepo().findById(nodeId).get().getOwnerNetId();

        if (!ownerNetId.equals(requesterNetId)) {
            throw new InvalidOwnerException(nodeId);
        }
        return super.checkNext(nodeId);
    }
}
