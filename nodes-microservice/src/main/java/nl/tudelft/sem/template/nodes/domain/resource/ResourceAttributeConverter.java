package nl.tudelft.sem.template.nodes.domain.resource;

import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the resource value object.
 */
@Converter
public class ResourceAttributeConverter implements AttributeConverter<Resource, String> {

    @Override
    public String convertToDatabaseColumn(Resource attribute) {
        return attribute.toString();
    }

    @Override
    public Resource convertToEntityAttribute(String dbData) {
        List<String> parts = List.of(dbData.split(","));
        return new Resource(Integer.parseInt(parts.get(0).trim()),
                Integer.parseInt(parts.get(1).trim()),
                Integer.parseInt(parts.get(2).trim()));
    }
}

