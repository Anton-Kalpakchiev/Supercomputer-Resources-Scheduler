package nl.tudelft.sem.template.users.domain.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import nl.tudelft.sem.template.users.domain.Employee;
import nl.tudelft.sem.template.users.domain.Sysadmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EmployeeAndUserUnitTest {
    private String netId;
    private String empty;
    private long parentFacultyId;
    private Employee testSubject;
    private Employee emptyTestSubject;

    @BeforeEach
    void setup() {
        netId = "employee";
        empty = "";
        parentFacultyId = 5L;
        testSubject = new Employee(netId, Set.of(parentFacultyId));
        emptyTestSubject = new Employee(netId);
    }

    @Test
    public void constructorTest() {
        Employee user = new Employee(netId, Set.of(parentFacultyId));
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
        assertThat(testSubject.getParentFacultyIds()).isEqualTo(Set.of(parentFacultyId));
    }

    @Test
    public void setParentFacultyIdTest() {
        Set<Long> parentIds = new HashSet<>();
        parentIds.add(1L);
        parentIds.add(2L);
        testSubject.setParentFacultyIds(parentIds);
        assertThat(testSubject.getParentFacultyIds()).isEqualTo(parentIds);
    }

    @Test
    public void addFacultySuccessful() {
        assertThat(emptyTestSubject.addFaculty(parentFacultyId)).isTrue();
    }

    @Test
    public void addFacultyUnsuccessful() {
        emptyTestSubject.addFaculty(parentFacultyId);
        assertThat(emptyTestSubject.addFaculty(parentFacultyId)).isFalse();
    }

    @Test
    public void removeFacultySuccessful() {
        emptyTestSubject.addFaculty(parentFacultyId);
        assertThat(emptyTestSubject.removeFaculty(parentFacultyId)).isTrue();
    }

    @Test
    public void removeFacultyUnsuccessful() {
        assertThat(emptyTestSubject.removeFaculty(parentFacultyId)).isFalse();
    }

    @Test
    public void toStringTest() {
        assertThat(testSubject.toString())
                .isEqualTo("Employee with netId: " + netId + " -> at faculty [" + parentFacultyId + "]");
        assertThat(emptyTestSubject.toString())
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
        Employee other = new Employee(netId, Set.of(6L));
        //only compared by netId.
        assertThat(testSubject.equals(other)).isTrue();
    }

    @Test
    public void equalsSameTest() {
        //only compared by netId.
        assertThat(testSubject.equals(testSubject)).isTrue();
    }

    @Test
    public void equalsOtherClass() {
        Sysadmin other = new Sysadmin(netId);
        //only compared by netId.
        assertThat(testSubject.equals(other)).isFalse();
    }

    @Test
    public void equalsNull() {
        Sysadmin other = new Sysadmin(netId);
        //only compared by netId.
        assertThat(testSubject.equals(null)).isFalse();
    }

    @Test
    public void hashTest() {
        assertThat(testSubject.hashCode()).isEqualTo(Objects.hash(netId));
    }
}
