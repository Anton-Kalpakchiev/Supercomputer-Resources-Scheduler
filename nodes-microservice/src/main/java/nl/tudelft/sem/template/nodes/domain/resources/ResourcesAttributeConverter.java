package nl.tudelft.sem.template.nodes.domain.resources;

import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the resource value object.
 */
@Converter
public class ResourcesAttributeConverter implements AttributeConverter<Resources, String> {

    /**
     * Returns a string representation of the resource to be stored in the column.
     *
     * @param attribute  the resource attribute value to be converted
     * @return a string representation of the resource
     */
    @Override
    public String convertToDatabaseColumn(Resources attribute) {
        return attribute.toString();
    }

    /**
     * Returns the resource object extracted from the string stored in the database.
     *
     * @param dbData  the data from the database column to be converted back to a resource
     * @return the resource object extracted from the string
     */
    @Override
    public Resources convertToEntityAttribute(String dbData) {
        List<String> parts = List.of(dbData.split(","));
        return new Resources(Integer.parseInt(parts.get(0).trim()),
                Integer.parseInt(parts.get(1).trim()),
                Integer.parseInt(parts.get(2).trim()));
    }
}

