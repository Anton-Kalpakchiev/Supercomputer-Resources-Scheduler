package nl.tudelft.sem.template.users.facade;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationRequestModel {
    private String description;
    private int cpu;
    private int gpu;
    private int memory;
    private String facultyName;
    private String deadline;
}