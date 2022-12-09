package nl.tudelft.sem.template.nodes.domain.node;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the resource value object.
 */
@Converter
public class TokenAttributeConverter implements AttributeConverter<Token, String> {

    @Override
    public String convertToDatabaseColumn(Token attribute) {
        return attribute.toString();
    }

    @Override
    public Token convertToEntityAttribute(String dbData) {
        return new Token(dbData);
    }
}
