package nl.tudelft.sem.template.nodes.domain.node.chain;


import nl.tudelft.sem.template.nodes.domain.node.NodeRepository;

/**
 * Abstract implementation of a handler which takes care of the getRepo, setNext adn checkNext methods.
 */
public abstract class BaseHandler implements Handler {
    private transient Handler next;

    private final transient NodeRepository repo;

    /**
     * Constructor to initialise a Handler.
     *
     * @param repo the NodeRepository of the system
     */
    public BaseHandler(NodeRepository repo) {
        this.repo = repo;
    }

    /**
     * Gets the NodeRepository.
     *
     * @return the NodeRepository
     */
    public NodeRepository getRepo() {
        return repo;
    }

    /**
     * Sets the next handler in the chain.
     *
     * @param h the next handler
     */
    public void setNext(Handler h) {
        this.next = h;
    }

    /**
     * Checks if there is a next handler in the chains, otherwise stops the chain.
     *
     * @param nodeId the id which is passed around and used to get the necessary information
     * @return whether the next handler passed recursively
     * @throws InvalidRequestException if some verification step failed
     */
    protected boolean checkNext(long nodeId) throws InvalidRequestException {
        if (next == null) {
            return true;
        }
        return next.handle(nodeId);
    }
}
