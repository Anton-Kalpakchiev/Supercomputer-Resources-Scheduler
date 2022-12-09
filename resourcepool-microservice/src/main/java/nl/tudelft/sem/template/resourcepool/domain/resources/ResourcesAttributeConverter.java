package nl.tudelft.sem.template.resourcepool.domain.resources;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;

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

