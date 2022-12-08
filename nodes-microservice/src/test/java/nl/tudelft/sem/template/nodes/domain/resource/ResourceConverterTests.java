package nl.tudelft.sem.template.nodes.domain.resource;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ResourceConverterTests {

    private transient ResourceAttributeConverter converter;

    @BeforeEach
    public void setup() {
        converter = new ResourceAttributeConverter();
    }

    @Test
    public void testConvertToDatabaseColumn() {
        Resource resource = new Resource(400, 300, 300);
        assertThat(resource.toString()).isEqualTo(converter.convertToDatabaseColumn(resource));
    }

    @Test
    public void testConvertToEntityAttribute() {
        String resourceString = "400, 300, 300";
        Resource resource = new Resource(400, 300, 300);
        assertThat(converter.convertToEntityAttribute(resourceString)).isEqualTo(resource);
    }
}