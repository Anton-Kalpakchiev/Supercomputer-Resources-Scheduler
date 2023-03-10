package nl.tudelft.sem.template.requests.models;

import lombok.Data;

@Data
public class RegistrationRequestModel {
    private String description;
    private int cpu;
    private int gpu;
    private int memory;
    private String facultyName;
    private String deadline;
}
