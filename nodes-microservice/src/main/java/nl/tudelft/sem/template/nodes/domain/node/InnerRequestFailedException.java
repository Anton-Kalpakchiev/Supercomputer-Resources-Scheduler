package nl.tudelft.sem.template.nodes.domain.node;

public class InnerRequestFailedException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public InnerRequestFailedException(String reason) {
        super(reason);
    }
}
