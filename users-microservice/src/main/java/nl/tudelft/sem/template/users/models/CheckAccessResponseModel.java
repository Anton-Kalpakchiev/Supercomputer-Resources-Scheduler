package nl.tudelft.sem.template.users.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing a check access response.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckAccessResponseModel {
    private String access;
}
