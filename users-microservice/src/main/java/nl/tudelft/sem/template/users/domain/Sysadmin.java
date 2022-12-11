package nl.tudelft.sem.template.users.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sysadmin_accounts")
public class Sysadmin extends User {

    /**
     * A constructor for the sysadmin.
     *
     * @param netId of the sysadmin
     */
    public Sysadmin(String netId) {
        super(netId);
    }

    /**
     * An empty constructor as per the annotation requirement.
     */
    public Sysadmin() {
        super("");
    }

    /**
     * String representation of the object.
     *
     * @return the string representation.
     */
    @Override
    public String toString() {
        return "Sysadmin with netId: " + netId;
    }
}
