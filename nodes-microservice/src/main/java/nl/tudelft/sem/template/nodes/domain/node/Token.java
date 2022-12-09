package nl.tudelft.sem.template.nodes.domain.node;

import java.util.Objects;

/**
 * A DDD value object representing a token of a node in our domain.
 */
public class Token {
    private final transient String nodeToken;

    public Token(String nodeToken) {
        // validate token
        this.nodeToken = nodeToken;
    }

    @Override
    public String toString() {
        return nodeToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Token token = (Token) o;
        return nodeToken.equals(token.nodeToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeToken);
    }
}
