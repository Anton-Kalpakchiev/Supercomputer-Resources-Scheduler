package nl.tudelft.sem.template.users.domain;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a password in our domain.
 */
@EqualsAndHashCode
public class Password {
    private final transient String passwordValue;

    public Password(String password) {
        // Validate input
        this.passwordValue = password;
    }

    @Override
    public String toString() {
        return passwordValue;
    }
}
