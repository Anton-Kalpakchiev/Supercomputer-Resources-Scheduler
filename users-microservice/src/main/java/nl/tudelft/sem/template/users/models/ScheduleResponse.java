package nl.tudelft.sem.template.users.models;

import java.util.Calendar;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponse {
    private Calendar day;
    private ResourceDto availableResources;
}

