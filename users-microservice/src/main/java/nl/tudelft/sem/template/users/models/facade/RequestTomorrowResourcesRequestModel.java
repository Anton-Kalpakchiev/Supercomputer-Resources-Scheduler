package nl.tudelft.sem.template.users.models.facade;

import java.util.Calendar;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestTomorrowResourcesRequestModel {
    private long resourcePoolId;
}
