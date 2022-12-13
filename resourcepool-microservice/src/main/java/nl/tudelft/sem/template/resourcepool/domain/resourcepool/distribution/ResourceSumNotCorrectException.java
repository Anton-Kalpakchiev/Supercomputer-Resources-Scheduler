package nl.tudelft.sem.template.resourcepool.domain.resourcepool.distribution;

/**
 * Exception to indicate that the sum of the resource doesn't add up to a 100.
 */
public class ResourceSumNotCorrectException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public ResourceSumNotCorrectException(String resource) {
        super(resource);
    }
}
