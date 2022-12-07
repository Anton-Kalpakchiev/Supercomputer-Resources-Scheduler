package nl.tudelft.sem.template.nodes.domain.node;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the resource value object.
 */
@Converter
public class NodeUrlAttributeConverter implements AttributeConverter<NodeUrl, String> {

    @Override
    public String convertToDatabaseColumn(NodeUrl attribute) {
        return attribute.toString();
    }

    @Override
    public NodeUrl convertToEntityAttribute(String dbData) {
        return new NodeUrl(dbData);
    }
}