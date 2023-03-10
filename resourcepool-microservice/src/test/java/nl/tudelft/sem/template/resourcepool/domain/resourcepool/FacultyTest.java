package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import org.junit.jupiter.api.Test;

class FacultyTest {

    @Test
    public void constructorTest() {
        Faculty faculty = new Faculty("test", "EEMCS");
        assertNotNull(faculty);
    }

    @Test
    void getManagerNetId() {
        Faculty faculty = new Faculty("test", "EEMCS");
        assertEquals("EEMCS", faculty.getManagerNetId());
    }

    @Test
    void testEquals() {
        Faculty faculty1 = new Faculty("test", "EEMCS");
        Faculty faculty2 = new Faculty("test", "EEMCS");
        faculty2.setBaseResources(new Resources(10, 20, 30));
        assertEquals(faculty1, faculty2);
    }

    @Test
    void testNotEquals() {
        Faculty faculty1 = new Faculty("test1", "EEMCS");
        Faculty faculty2 = new Faculty("test2", "EEMCS");
        assertNotEquals(faculty1, faculty2);
    }

    @Test
    void testToString() {
        Faculty faculty = new Faculty("test", "EEMCS");
        faculty.setNodeResources(new Resources(42, 42, 42));
        assertEquals("Faculty{id=0, name='test', baseResources=(CPU: 0, GPU: 0, Memory: 0), nodeResources=(CPU: 42, GPU:"
            + " 42, Memory: 42), managerNetId=EEMCS}", faculty.toString());
    }
}