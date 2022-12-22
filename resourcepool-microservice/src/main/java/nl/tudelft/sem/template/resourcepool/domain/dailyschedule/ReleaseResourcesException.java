package nl.tudelft.sem.template.resourcepool.domain.dailyschedule;

public class ReleaseResourcesException extends Exception {
    static final long serialVersionUID = -3387516993124229928L;

    public ReleaseResourcesException(String reason) {
        super(reason);
    }
}
