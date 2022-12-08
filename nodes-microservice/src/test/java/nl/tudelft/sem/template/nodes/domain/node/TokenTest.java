package nl.tudelft.sem.template.nodes.domain.node;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TokenTest {

    @Test
    void constructorTest() {
        Token token = new Token("token");
        assertThat(token).isNotNull();
    }

    @Test
    void testToString() {
        assertThat(new Token("token").toString()).isEqualTo("token");
    }

    @Test
    void testEqualsTrue() {
        assertThat(new Token("token")).isEqualTo(new Token("token"));
    }

    @Test
    void testEqualsFalse() {
        assertThat(new Token("token")).isNotEqualTo(new Token("token2"));
    }
}