package nl.tudelft.sem.template.users.domain;


import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
@MappedSuperclass
abstract class User {
    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "net_id", nullable = false, unique = true)
    @Convert(converter = NetIdAttributeConverter.class)
    private NetId netId;

    @Column(name = "password_hash", nullable = false)
    @Convert(converter = HashedPasswordAttributeConverter.class)
    private HashedPassword hashedPassword;

    /**
     * Constructor for the abstract parent User.
     *
     * @param netId netID of the user
     * @param hashedPassword hashed password of the user
     */
    public User(NetId netId, HashedPassword hashedPassword) {
        this.netId = netId;
        this.hashedPassword = hashedPassword;
    }

    /**
     * Getter for the netID.
     *
     * @return the netId
     */
    public NetId getNetId() {
        return netId;
    }

    /**
     * Getter for the hashed password.
     *
     * @return the hashed password
     */
    public HashedPassword getHashedPassword() {
        return hashedPassword;
    }

    /**
     * Setter for the hashed password.
     *
     * @param hashedPassword the new hashed password
     */
    public void setHashedPassword(HashedPassword hashedPassword) {
        this.hashedPassword = hashedPassword;
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
        return id == user.id && Objects.equals(netId, user.netId) && Objects.equals(hashedPassword, user.hashedPassword);
    }

    /**
     * Hash implementation of the User.
     *
     * @return the hash of the user
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, netId, hashedPassword);
    }

    /**
     * String converter for a User.
     *
     * @return the string of the user.
     */
    @Override
    public String toString() {
        return "User{"
                + "id=" + id
                + ", netId=" + netId
                + ", hashedPassword=" + hashedPassword
                + '}';
    }
}
