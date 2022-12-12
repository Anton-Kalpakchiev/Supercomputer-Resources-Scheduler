package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

/**
 * Exception to indicate the name is already in use.
 */
public class ManagerNetIdAlreadyAssignedException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public ManagerNetIdAlreadyAssignedException(long managerNetId) {
        super(String.valueOf(managerNetId));
    }
}
