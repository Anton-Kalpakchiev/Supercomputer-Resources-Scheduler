package nl.tudelft.sem.template.users.models.facade;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponseModel {
    private Map<String, List<String>> schedules;
}
