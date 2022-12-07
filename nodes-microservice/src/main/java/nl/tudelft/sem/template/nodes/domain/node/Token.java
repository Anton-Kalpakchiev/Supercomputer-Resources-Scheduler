package nl.tudelft.sem.template.nodes.domain.node;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a token of a node in our domain.
 */
@EqualsAndHashCode
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
}
