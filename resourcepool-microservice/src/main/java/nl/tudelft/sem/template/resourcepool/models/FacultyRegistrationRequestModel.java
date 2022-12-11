package nl.tudelft.sem.template.resourcepool.models;

import lombok.Data;

/**
 * Model representing a registration request.
 */
@Data
public class FacultyRegistrationRequestModel {

    private String name;

    private long managerId;
}