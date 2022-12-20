package nl.tudelft.sem.template.users.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class User {

    @Id
    @Column(name = "net_id")
    protected String netId;

    /**
     * Constructor for the abstract parent User.
     *
     * @param netId netID of the user
     */
    public User(String netId) {
        this.netId = netId;
    }

    /**
     * Getter for the netID.
     *
     * @return the netId
     */
    public String getNetId() {
        return netId;
    }

    /**
     * Setter for the netId.
     *
     * @param netId the new netId.
     */
    public void setNetId(String netId) {
        this.netId = netId;
    }

    /**
     * Equals implementation for a User object.
     *
     * @param o the object to compare with
     * @return if the objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(netId, user.netId);
    }

    /**
     * Computes the hash code for the object.
     *
     * @return the object's hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(netId);
    }
}
