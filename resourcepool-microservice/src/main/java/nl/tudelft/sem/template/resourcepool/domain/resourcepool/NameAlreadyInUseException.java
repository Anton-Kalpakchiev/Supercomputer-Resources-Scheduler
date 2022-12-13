package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

/**
 * Exception to indicate the name is already in use.
 */
public class NameAlreadyInUseException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public NameAlreadyInUseException(String name) {
        super(name);
    }
}
