package nl.tudelft.sem.template.users.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model for requesting the schedule.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestScheduleModel {
    private long facultyId;
    private boolean facultyManager;
}
