package nl.tudelft.sem.template.users.models.facade;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing a node deletion request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestStatusModel {

    private long requestId;
}