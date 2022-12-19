package nl.tudelft.sem.template.resourcepool.models;

import lombok.Data;

/**
 * Model representing a registration request.
 */
@Data
public class FacultyCreationModel {

    private String name;

    private String managerNetId;
}