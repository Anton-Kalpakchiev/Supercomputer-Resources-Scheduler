package nl.tudelft.sem.template.nodes.domain.node;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import org.junit.jupiter.api.Test;



class NameTest {

    @Test
    public void nameTest() {
        Name name = new Name("Name");
        assertThat(name).isNotNull();
    }

    @Test
    public void nameStringTest() {
        assertThat(new Name("Name").toString()).isEqualTo("Name");
    }

    @Test
    public void equalsTestTrue() {
        assertThat(new Name("Mayte")).isEqualTo(new Name("Mayte"));
    }

    @Test
    public void equalsTestFalse() {
        assertThat(new Name("Mayte")).isNotEqualTo(new Name("Ivo"));
    }

    @Test
    public void hashTest() {
        assertThat(new Token("token").hashCode()).isEqualTo(Objects.hash("token"));
    }
}