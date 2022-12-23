package nl.tudelft.sem.template.nodes.domain.node.chain;

/**
 * The interface for the handler of our chain of responsibility.
 */
public interface Handler {

    void setNext(Handler handler);

    default boolean handle(long nodeId) throws InvalidRequestException {
        return false;
    }
}
