package nl.tudelft.sem.template.nodes.domain.node.chain;


import nl.tudelft.sem.template.nodes.domain.node.NodeRepository;

public abstract class BaseHandler implements Handler {
    private Handler next;

    private final transient NodeRepository repo;

    public BaseHandler(NodeRepository repo) {
        this.repo = repo;
    }

    public NodeRepository getRepo() {
        return repo;
    }

    public void setNext(Handler h) {
        this.next = h;
    }

    protected boolean checkNext(long nodeId) throws InvalidRequestException {
        if (next == null) {
            return true;
        }
        return next.handle(nodeId);
    }
}
