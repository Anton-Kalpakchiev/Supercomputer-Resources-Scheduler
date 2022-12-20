package nl.tudelft.sem.template.authentication.domain.user;

import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppUserTest {

    @Test
    public void constructorTest() {
        NetId netId = new NetId("user");
        HashedPassword hashedPassword = new HashedPassword("hash");
        AppUser appUser = new AppUser(netId, hashedPassword);

        assertEquals(appUser.getNetId(), netId);
        assertEquals(appUser.getPassword(), hashedPassword);
    }

    @Test
    public void changePasswordTest() {
        NetId netId = new NetId("user");
        HashedPassword hashedPassword = new HashedPassword("hash");
        HashedPassword newHashPassword = new HashedPassword("new hash");
        AppUser appUser = new AppUser(netId, hashedPassword);

        appUser.changePassword(newHashPassword);

        assertEquals(appUser.getNetId(), netId);
        assertEquals(appUser.getPassword(), newHashPassword);
    }

    @Test
    public void equalsTest() {
        NetId netId = new NetId("user");
        HashedPassword hashedPassword = new HashedPassword("hash");
        AppUser appUser = new AppUser(netId, hashedPassword);
        AppUser theSame = new AppUser(netId, hashedPassword);

        assertTrue(appUser.equals(theSame));
    }

    @Test
    public void hashTest() {
        NetId netId = new NetId("user");
        HashedPassword hashedPassword = new HashedPassword("hash");
        AppUser appUser = new AppUser(netId, hashedPassword);
        int expectedHash = Objects.hash(netId);

        assertEquals(appUser.hashCode(), expectedHash);
    }
}