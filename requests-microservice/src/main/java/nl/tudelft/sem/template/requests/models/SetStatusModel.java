package nl.tudelft.sem.template.requests.models;

import lombok.Data;

@Data
public class SetStatusModel {
    private long id;
    private int status;
}
