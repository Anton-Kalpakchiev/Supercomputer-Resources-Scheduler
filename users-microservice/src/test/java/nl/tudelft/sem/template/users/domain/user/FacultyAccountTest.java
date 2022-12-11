package nl.tudelft.sem.template.users.domain.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import nl.tudelft.sem.template.users.domain.FacultyAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FacultyAccountTest {
    private String netId;
    private String empty;
    private int assignedFacultyId;
    private FacultyAccount testSubject;

    @BeforeEach
    void setup() {
        netId = "faculty";
        empty = "";
        assignedFacultyId = 5;
        testSubject = new FacultyAccount(netId, assignedFacultyId);
    }

    @Test
    public void constructorTest() {
        assertThat(testSubject).isNotNull();
    }

    @Test
    public void emptyConstructorTest() {
        FacultyAccount empty = new FacultyAccount();
        assertThat(empty).isNotNull();
    }

    @Test
    public void getAssignedFacultyIdTest() {
        assertThat(testSubject.getAssignedFacultyId()).isEqualTo(assignedFacultyId);
    }

    @Test
    public void setAssignedFacultyIdTest() {
        testSubject.setAssignedFacultyId(6);
        assertThat(testSubject.getAssignedFacultyId()).isEqualTo(6);
    }
}
