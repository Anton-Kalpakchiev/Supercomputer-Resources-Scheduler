package nl.tudelft.sem.template.users.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemporaryRequestModel {
    private String name;
    private String managerNetId;
}
