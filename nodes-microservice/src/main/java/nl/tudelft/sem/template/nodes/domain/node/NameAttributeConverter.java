package nl.tudelft.sem.template.nodes.domain.node;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the resource value object.
 */
@Converter
public class NameAttributeConverter implements AttributeConverter<Name, String> {

    @Override
    public String convertToDatabaseColumn(Name attribute) {
        return attribute.toString();
    }

    @Override
    public Name convertToEntityAttribute(String dbData) {
        return new Name(dbData);
    }
}