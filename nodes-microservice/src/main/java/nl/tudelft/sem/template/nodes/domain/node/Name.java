package nl.tudelft.sem.template.nodes.domain.node;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a name of a node in our domain.
 */
@EqualsAndHashCode
public class Name {
    private final transient String nodeName;

    public Name(String nodeName) {
        // validate name
        this.nodeName = nodeName;
    }

    @Override
    public String toString() {
        return nodeName;
    }
}
