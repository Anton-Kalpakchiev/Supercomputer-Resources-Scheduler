package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

/**
 * A DDD domain event that indicated a faculty was created.
 */
public class FacultyWasCreatedEvent {
    private final String name;

    public FacultyWasCreatedEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
