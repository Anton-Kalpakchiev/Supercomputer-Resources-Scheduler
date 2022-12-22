package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

/**
 * Exception to indicate the name is already in use.
 */
public class FacultyNotFoundException extends Exception {
    static final long serialVersionUID = -3387116993124229948L;

    public FacultyNotFoundException(String name) {
        super(name);
    }
}
