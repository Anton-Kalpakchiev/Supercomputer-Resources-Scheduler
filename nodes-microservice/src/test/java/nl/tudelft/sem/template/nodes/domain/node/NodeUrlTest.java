package nl.tudelft.sem.template.nodes.domain.node;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NodeUrlTest {

    @Test
    void constructorTest() {
        NodeUrl nodeurl = new NodeUrl("url");
        assertThat(nodeurl).isNotNull();
    }

    @Test
    void testToString() {
        assertThat(new NodeUrl("Name").toString()).isEqualTo("Name");
    }

    @Test
    void testEqualsTrue() {
        assertThat(new NodeUrl("url")).isEqualTo(new NodeUrl("url"));
    }

    @Test
    void testEqualsFalse() {
        assertThat(new NodeUrl("url")).isNotEqualTo(new NodeUrl("url2"));
    }
}