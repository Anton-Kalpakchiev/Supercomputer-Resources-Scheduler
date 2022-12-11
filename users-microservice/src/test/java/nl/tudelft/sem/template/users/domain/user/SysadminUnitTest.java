package nl.tudelft.sem.template.users.domain.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import nl.tudelft.sem.template.users.domain.Sysadmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SysadminUnitTest {
    private String netId;
    private String empty;

    @BeforeEach
    void setup() {
        netId = "admin";
        empty = "";
    }

    @Test
    public void constructorTest() {
        Sysadmin user = new Sysadmin(netId);
        assertThat(user).isNotNull();
    }

    @Test
    public void emptyConstructorTest() {
        Sysadmin user = new Sysadmin();
        assertThat(user).isNotNull();
    }

    @Test
    public void toStringTest() {
        Sysadmin user = new Sysadmin(netId);
        assertThat(user.toString()).isEqualTo("Sysadmin with netId: " + netId);
    }
}
