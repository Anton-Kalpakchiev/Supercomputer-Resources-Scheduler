package nl.tudelft.sem.template.nodes.domain.node;

import java.util.Objects;

/**
 * A DDD value object representing a name of a node in our domain.
 */
public class Name {
    private final transient String nodeName;

    /**
     * Instantiates a new Name.
     *
     * @param nodeName the node name
     */
    public Name(String nodeName) {
        // validate name
        this.nodeName = nodeName;
    }

    @Override
    public String toString() {
        return nodeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Name name = (Name) o;
        return nodeName.equals(name.nodeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeName);
    }
}
