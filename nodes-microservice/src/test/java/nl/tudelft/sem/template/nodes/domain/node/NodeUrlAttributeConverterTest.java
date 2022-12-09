package nl.tudelft.sem.template.nodes.domain.node;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NodeUrlAttributeConverterTest {

    private transient NodeUrlAttributeConverter converter;

    @BeforeEach
    void setup() {
        converter = new NodeUrlAttributeConverter();
    }

    @Test
    void convertToDatabaseColumn() {
        NodeUrl nodeUrl = new NodeUrl("url");
        assertThat(converter.convertToDatabaseColumn(nodeUrl)).isEqualTo("url");
    }

    @Test
    void convertToEntityAttribute() {
        NodeUrl nodeUrl = new NodeUrl("url");
        assertThat(converter.convertToEntityAttribute("url")).isEqualTo(nodeUrl);
    }
}