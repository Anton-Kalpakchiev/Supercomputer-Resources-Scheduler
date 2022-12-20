package nl.tudelft.sem.template.users.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model to request a new user.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacultyAssignmentRequestModel {
    String netId;
    String facultyIds;
}
