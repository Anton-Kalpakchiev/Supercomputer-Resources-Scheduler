package nl.tudelft.sem.template.requests.domain;

/**
 * A DDD domain event that indicated a user was created.
 */
public class RequestWasCreatedEvent {
    private final String description;

    public RequestWasCreatedEvent(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
