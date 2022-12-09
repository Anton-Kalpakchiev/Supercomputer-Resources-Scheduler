package nl.tudelft.sem.template.nodes.domain.node;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.nodes.domain.resources.Resources;
import nl.tudelft.sem.template.nodes.domain.resources.ResourcesAttributeConverter;







/**
 * A DDD entity representing an application node in our domain.
 */
@Entity
@Table(name = "nodes")
@NoArgsConstructor
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;
    @Column(name = "name", nullable = false, unique = true)
    @Convert(converter = NameAttributeConverter.class)
    private Name name;
    @Column(name = "url", nullable = false, unique = true)
    @Convert(converter = NodeUrlAttributeConverter.class)
    private NodeUrl url;
    @Column(name = "token", nullable = false, unique = true)
    @Convert(converter = TokenAttributeConverter.class)
    private Token token;
    @Column(name = "resource", nullable = false, unique = false)
    @Convert(converter = ResourcesAttributeConverter.class)
    private Resources resource;

    /**
     * Instantiates a new Node.
     *
     * @param nodeName     the name
     * @param nodeUrl      the url
     * @param nodeToken    the token
     * @param nodeResource the resource
     */
    public Node(Name nodeName, NodeUrl nodeUrl, Token nodeToken, Resources nodeResource) {
        this.name = nodeName;
        this.url = nodeUrl;
        this.token = nodeToken;
        this.resource = nodeResource;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public Name getNodeName() {
        return name;
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public NodeUrl getUrl() {
        return url;
    }

    /**
     * Gets token.
     *
     * @return the token
     */
    public Token getToken() {
        return token;
    }

    /**
     * Gets resource.
     *
     * @return the resource
     */
    public Resources getResource() {
        return resource;
    }

    public void updateResource(Resources newResource) {
        this.resource = newResource;
    }

    /**
     * Equals method implementation for Node.
     *
     * @param o other
     * @return boolean equals
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Node node = (Node) o;
        return id == node.id
                && name.equals(node.name)
                && url.equals(node.url)
                && token.equals(node.token)
                && resource.equals(node.resource);
    }

    /**
     * Hash implementation for node object.
     *
     * @return int hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getNodeName(), this.getUrl(), this.getToken(), this.getResource());
    }

    /**
     * String converter for node object.
     *
     * @return String
     */
    @Override
    public String toString() {
        return "Node " + name + " {url:"
                + url + ", token:" + token + ", resource:[" + resource + "]}";
    }
}