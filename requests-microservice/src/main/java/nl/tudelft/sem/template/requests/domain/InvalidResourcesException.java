package nl.tudelft.sem.template.requests.domain;

public class InvalidResourcesException extends Exception {

    static final long serialVersionUID = -3387516993124229948L;

    public InvalidResourcesException(Resources resources) {
        super(resources.toString());
    }
}
