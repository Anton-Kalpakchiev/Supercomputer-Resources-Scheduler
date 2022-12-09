package nl.tudelft.sem.template.users.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SYSADMINS")
public class Sysadmin extends User {

    /**
     * A constructor for the sysadmin.
     *
     * @param netId of the sysadmin
     */
    public Sysadmin(NetId netId) {
        super(netId);
    }

    public Sysadmin() {
        super(new NetId(""));
    }
}
