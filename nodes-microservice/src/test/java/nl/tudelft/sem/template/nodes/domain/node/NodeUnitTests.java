package nl.tudelft.sem.template.nodes.domain.node;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import nl.tudelft.sem.template.nodes.domain.resources.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NodeUnitTests {
    private Name name;
    private NodeUrl url;
    private Token token;
    private Resources resource;

    private Node node;

    @BeforeEach
    void setup() {
        name = new Name("Mayte");
        url = new NodeUrl("url");
        token = new Token("token");
        resource = new Resources(500, 400, 400);
        node = new Node(name, url, token, resource);
    }

    @Test
    public void constructorTestNode() {
        Node node = new Node(name, url, token, resource);
        assertThat(node).isNotNull();
    }

    @Test
    public void getNameNodeTest() {
        assertThat(node.getNodeName()).isEqualTo(name);
    }

    @Test
    public void getTokenTest() {
        assertThat(node.getToken()).isEqualTo(token);
    }

    @Test
    public void getUrlTest() {
        assertThat(node.getUrl()).isEqualTo(url);
    }

    @Test
    public void getResourceTest() {
        assertThat(node.getResource()).isEqualTo(resource);
    }

    @Test
    public void updateResourceTest() {
        Resources resource2 = new Resources(100, 100, 100);
        node.updateResource(resource2);
        assertThat(node.getResource()).isEqualTo(resource2);
    }

    @Test
    public void toStringNodeTest() {
        String nodeString = "Node Mayte {url:url, token:token, resource:[CPU: 500, GPU: 400, Memory: 400]}";
        assertThat(node.toString()).isEqualTo(nodeString);
    }

    @Test
    public void testNodeEqualsTrue() {
        Node node2 = new Node(new Name("Mayte"), new NodeUrl("url"), new Token("token"), new Resources(500, 400, 400));
        assertThat(node).isEqualTo(node2);
    }

    @Test
    public void testNodeEqualsFalseToken() {
        Node node2 = new Node(new Name("Mayte"), new NodeUrl("url"), new Token("tokenwrong"), new Resources(500, 400, 400));
        assertThat(node).isNotEqualTo(node2);
    }

    @Test
    public void testNodeNameEqualsFalse() {
        Node node2 = new Node(new Name("Ivo"), new NodeUrl("url"), new Token("token"), new Resources(500, 400, 400));
        assertThat(node).isNotEqualTo(node2);
    }

    @Test
    public void testNodeEqualsFalseUrl() {
        Node node2 = new Node(new Name("Mayte"), new NodeUrl("urlwrong"), new Token("token"), new Resources(500, 400, 400));
        assertThat(node).isNotEqualTo(node2);
    }

    @Test
    public void testNodeEqualsFalseResource() {
        Node node2 = new Node(new Name("Mayte"), new NodeUrl("url"), new Token("token"), new Resources(400, 400, 400));
        assertThat(node).isNotEqualTo(node2);
    }

    @Test
    public void testEqualsSameObject() {
        assertThat(node).isEqualTo(node);
    }

    @Test
    public void testEqualsNotSameClass() {
        assertThat(node).isNotEqualTo(null);
    }

    @Test
    public void hashTest() {
        assertThat(node.hashCode())
                .isEqualTo(Objects.hash(node.getNodeName(), node.getUrl(), node.getToken(), node.getResource()));
    }
}
