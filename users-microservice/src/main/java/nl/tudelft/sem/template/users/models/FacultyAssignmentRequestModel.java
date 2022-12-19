package nl.tudelft.sem.template.users.models;

import lombok.Data;


/**
 * Model to request a new user.
 */
@Data
public class FacultyAssignmentRequestModel {
    String netId;
    String facultyIds;
}
