package nl.tudelft.sem.template.requests.models;

import lombok.Data;
import java.util.Calendar;

@Data
public class RegistrationRequestModel {
    private String description;
    private int mem;
    private int cpu;
    private int gpu;
    private String facultyName;
    private String deadline;
}
