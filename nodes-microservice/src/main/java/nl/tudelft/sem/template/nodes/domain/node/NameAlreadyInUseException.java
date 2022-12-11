package nl.tudelft.sem.template.nodes.domain.node;

/**
 * Exception to indicate the name is already in use.
 */
public class NameAlreadyInUseException extends Exception {
    static final long serialVersionUID = -3387516993524229948L;

    public NameAlreadyInUseException(Name name) {
        super(name.toString());
    }
}