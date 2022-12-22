package nl.tudelft.sem.template.resourcepool.domain.resources;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ResourcesConverterTests {

    private transient ResourcesAttributeConverter converter;

    @BeforeEach
    public void setup() {
        converter = new ResourcesAttributeConverter();
    }

    @Test
    public void testConvertToDatabaseColumn() {
        Resources resource = new Resources(400, 300, 300);
        assertThat(converter.convertToDatabaseColumn(resource)).isEqualTo("400,300,300");
    }

    @Test
    public void testConvertToEntityAttribute() {
        String resourceString = "400, 300, 300";
        Resources resource = new Resources(400, 300, 300);
        assertThat(converter.convertToEntityAttribute(resourceString)).isEqualTo(resource);
    }
}