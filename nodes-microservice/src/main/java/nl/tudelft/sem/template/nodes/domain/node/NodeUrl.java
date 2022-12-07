package nl.tudelft.sem.template.nodes.domain.node;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a name of a node in our domain.
 */
@EqualsAndHashCode
public class NodeUrl {
    private final transient String url;

    public NodeUrl(String url) {
        // validate url
        this.url = url;
    }

    @Override
    public String toString() {
        return url;
    }
}
