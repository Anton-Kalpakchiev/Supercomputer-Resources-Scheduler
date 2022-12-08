package nl.tudelft.sem.template.nodes.domain.node;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NameAttributeConverterTest {

    @Test
    void convertToDatabaseColumn() {
        Name name = new Name("Name");
        NameAttributeConverter converter = new NameAttributeConverter();
        assertThat(converter.convertToDatabaseColumn(name)).isEqualTo(name.toString());
    }

    @Test
    void convertToEntityAttribute() {
        String nameString = "Name";
        Name name = new Name("Name");
        NameAttributeConverter converter = new NameAttributeConverter();
        assertThat(converter.convertToEntityAttribute(nameString)).isEqualTo(name);
    }
}