package nl.tudelft.sem.template.nodes.domain.node.chain;

public interface Handler {

    void setNext(Handler handler);
    boolean handle(long nodeId) throws InvalidRequestException;
}
