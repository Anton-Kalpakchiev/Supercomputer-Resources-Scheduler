package nl.tudelft.sem.template.users.models.facade;

import lombok.Data;

import java.util.Objects;

/**
 * Model representing a node contribution request.
 */
@Data
public class NodeContributionRequestModel {

    private String name;
    private String url;
    private long facultyId;
    private String token;
    private int cpu;
    private int gpu;
    private int memory;


    /**
     * Equality is only based on the name.
     *
     * @param o the object to be compared to
     * @return where the two objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NodeContributionRequestModel)) {
            return false;
        }
        NodeContributionRequestModel that = (NodeContributionRequestModel) o;
        return getName().equals(that.getName());
    }
}