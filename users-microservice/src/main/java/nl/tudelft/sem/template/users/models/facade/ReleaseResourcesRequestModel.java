package nl.tudelft.sem.template.users.models.facade;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Calendar;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReleaseResourcesRequestModel {
    private long facultyId;
    private String day;
}
