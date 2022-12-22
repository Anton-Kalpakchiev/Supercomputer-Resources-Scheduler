package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

/**
 * Exception to indicate that the remaining node resources are less than 0.
 */
public class RemainingResourcesInsufficientException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public RemainingResourcesInsufficientException(long id) {
        super(String.valueOf(id));
    }
}
