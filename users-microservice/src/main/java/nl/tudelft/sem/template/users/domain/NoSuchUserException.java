package nl.tudelft.sem.template.users.domain;

public class NoSuchUserException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public NoSuchUserException(String netId) {
        super("User '" + netId + "' does not exist.");
    }
}
