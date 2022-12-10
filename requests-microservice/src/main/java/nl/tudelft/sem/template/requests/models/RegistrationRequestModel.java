package nl.tudelft.sem.template.requests.models;

import lombok.Data;

@Data
public class RegistrationRequestModel {
    private String description;
    private int mem;
    private int cpu;
    private int gpu;
}
