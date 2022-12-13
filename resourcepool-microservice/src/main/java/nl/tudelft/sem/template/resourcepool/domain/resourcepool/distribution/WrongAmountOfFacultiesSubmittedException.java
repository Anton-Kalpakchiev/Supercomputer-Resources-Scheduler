package nl.tudelft.sem.template.resourcepool.domain.resourcepool.distribution;

/**
 * Exception to indicate that the amount of added faculties for the distribution is wrong.
 */
public class WrongAmountOfFacultiesSubmittedException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public WrongAmountOfFacultiesSubmittedException() {
        super();
    }
}
