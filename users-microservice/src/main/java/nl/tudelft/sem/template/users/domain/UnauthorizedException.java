package nl.tudelft.sem.template.users.domain;

public class UnauthorizedException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public UnauthorizedException(String reason) {
        super(reason);
    }
}
