package nl.tudelft.sem.template.users.models.facade;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request model for retrieving schedules.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequestModel {
    private long facultyId;
}
