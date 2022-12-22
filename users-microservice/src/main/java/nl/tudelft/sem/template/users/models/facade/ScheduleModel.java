package nl.tudelft.sem.template.users.models.facade;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request model for retrieving schedules.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleModel {
    private long facultyId;
}
