package nl.tudelft.sem.template.users.domain.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Objects;
import nl.tudelft.sem.template.users.domain.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EmployeeAndUserUnitTest {
    private String netId;
    private String empty;
    private int parentFacultyId;
    private Employee testSubject;

    @BeforeEach
    void setup() {
        netId = "employee";
        empty = "";
        parentFacultyId = 5;
        testSubject = new Employee(netId, parentFacultyId);
    }

    @Test
    public void constructorTest() {
        Employee user = new Employee(netId, parentFacultyId);
        assertThat(user).isNotNull();
    }

    @Test
    public void emptyConstructorTest() {
        Employee user = new Employee();
        assertThat(user).isNotNull();
    }

    @Test
    public void onlyNetIdConstructorTest() {
        Employee user = new Employee(netId);
        assertThat(user).isNotNull();
    }

    @Test
    public void getParentFacultyIdTest() {
        assertThat(testSubject.getParentFacultyId()).isEqualTo(parentFacultyId);
    }

    @Test
    public void setParentFacultyIdTest() {
        testSubject.setParentFacultyId(6);
        assertThat(testSubject.getParentFacultyId()).isEqualTo(6);
    }

    @Test
    public void toStringTest() {
        assertThat(testSubject.toString())
                .isEqualTo("Employee with netId: " + netId + " -> at faculty " + parentFacultyId);
        Employee noFaculty = new Employee(netId);
        assertThat(noFaculty.toString())
                .isEqualTo("Employee with netId: " + netId + " -> no faculty.");
    }

    //-----------------------------------------------------
    // User Tests:

    @Test
    public void getNetIdTest() {
        assertThat(testSubject.getNetId()).isEqualTo(netId);
    }

    @Test
    public void setNetIdTest() {
        testSubject.setNetId(empty);
        assertThat(testSubject.getNetId()).isEqualTo(empty);
    }

    @Test
    public void equalsTest() {
        Employee other = new Employee(netId, 6);
        //only compared by netId.
        assertThat(testSubject.equals(other)).isTrue();
    }

    @Test
    public void hashTest() {
        assertThat(testSubject.hashCode()).isEqualTo(Objects.hash(netId));
    }
}
