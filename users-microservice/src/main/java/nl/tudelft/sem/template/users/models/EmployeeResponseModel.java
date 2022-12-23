package nl.tudelft.sem.template.users.models;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseModel {
    String netId;
    String facultyIds;
}
