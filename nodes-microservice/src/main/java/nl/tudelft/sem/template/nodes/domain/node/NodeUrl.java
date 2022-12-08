package nl.tudelft.sem.template.nodes.domain.node;

import java.util.Objects;

/**
 * A DDD value object representing a name of a node in our domain.
 */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NodeUrl nodeUrl = (NodeUrl) o;
        return url.equals(nodeUrl.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
