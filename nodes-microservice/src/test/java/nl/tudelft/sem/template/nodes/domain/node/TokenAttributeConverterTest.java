package nl.tudelft.sem.template.nodes.domain.node;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenAttributeConverterTest {

    private transient TokenAttributeConverter converter;

    @BeforeEach
    void setup() {
        converter = new TokenAttributeConverter();
    }

    @Test
    void convertToDatabaseColumn() {
        Token token = new Token("token");
        assertThat(converter.convertToDatabaseColumn(token)).isEqualTo("token");
    }

    @Test
    void convertToEntityAttribute() {
        Token token = new Token("token");
        assertThat(converter.convertToEntityAttribute("token")).isEqualTo(token);
    }
}