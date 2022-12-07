package nl.tudelft.sem.template.users.domain;

public class Sysadmin extends User {
    /**
     * A constructor for the sysadmin.
     *
     * @param netId of the sysadmin
     * @param hashedPassword of the sysadmin
     */
    public Sysadmin(NetId netId, HashedPassword hashedPassword) {
        super(netId, hashedPassword);
    }
}
