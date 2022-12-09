package nl.tudelft.sem.template.nodes.domain.node;

public class TokenAlreadyInUseException extends Exception {
    static final long serialVersionUID = -3387516993524289948L;

    public TokenAlreadyInUseException(Token token) {
        super(token.toString());
    }
}