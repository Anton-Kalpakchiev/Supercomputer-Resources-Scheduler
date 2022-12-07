package nl.tudelft.sem.template.users.domain;

import javax.persistence.AttributeConverter;

public class FacultyConverter implements AttributeConverter<Faculty, String> {

    @Override
    public String convertToDatabaseColumn(Faculty attribute) {
        return attribute.toString();
    }

    @Override
    public Faculty convertToEntityAttribute(String dbData) {
        return null;
    }
}
