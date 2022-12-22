package nl.tudelft.sem.template.users.domain;

public class EmploymentException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public EmploymentException(String reason) {
        super(reason);
    }

}
