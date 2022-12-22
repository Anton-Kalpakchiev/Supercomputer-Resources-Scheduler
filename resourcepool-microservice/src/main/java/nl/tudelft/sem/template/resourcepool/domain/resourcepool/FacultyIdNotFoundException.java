package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

/**
 * Exception to indicate that the requested id can't be found.
 */
public class FacultyIdNotFoundException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public FacultyIdNotFoundException(long id) {
        super(String.valueOf(id));
    }
}
