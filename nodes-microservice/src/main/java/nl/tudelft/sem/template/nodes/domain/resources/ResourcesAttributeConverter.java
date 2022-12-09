package nl.tudelft.sem.template.nodes.domain.resources;

import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the resource value object.
 */
@Converter
public class ResourcesAttributeConverter implements AttributeConverter<Resources, String> {

    @Override
    public String convertToDatabaseColumn(Resources attribute) {
        return attribute.toString();
    }

    @Override
    public Resources convertToEntityAttribute(String dbData) {
        List<String> parts = List.of(dbData.split(","));
        return new Resources(Integer.parseInt(parts.get(0).trim()),
                Integer.parseInt(parts.get(1).trim()),
                Integer.parseInt(parts.get(2).trim()));
    }
}

