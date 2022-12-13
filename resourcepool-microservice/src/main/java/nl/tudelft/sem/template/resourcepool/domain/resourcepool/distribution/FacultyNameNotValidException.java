package nl.tudelft.sem.template.resourcepool.domain.resourcepool.distribution;

/**
 * Exception to indicate the faculty name doesn't exist or already is submitted.
 */
public class FacultyNameNotValidException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public FacultyNameNotValidException(String name) {
        super(name);
    }
}
