package nl.tudelft.sem.template.nodes.domain.node.chain;

import nl.tudelft.sem.template.nodes.domain.node.NodeRepository;

public class NodeExistenceHandler extends BaseHandler {

    public NodeExistenceHandler(NodeRepository repo) {
        super(repo);
    }

    @Override
    public boolean handle(long nodeId) throws InvalidRequestException {
        if (!super.getRepo().existsById(nodeId)) {
            throw new NodeIdNotFoundException(nodeId);
        }
        return super.checkNext(nodeId);
    }
}
