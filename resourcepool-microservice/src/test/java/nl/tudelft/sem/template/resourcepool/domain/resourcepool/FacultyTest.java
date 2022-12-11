package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import org.junit.jupiter.api.Test;

class FacultyTest {

    @Test
    public void constructorTest() {
        Faculty faculty = new Faculty(1L, "test", 1L);
        assertNotNull(faculty);
    }

    @Test
    void getManagerId() {
        Faculty faculty = new Faculty(1L, "test", 1L);
        assertEquals(1L, faculty.getManagerId());
    }

    @Test
    void testEquals() {
        Faculty faculty1 = new Faculty(1L, "test", 1L);
        Faculty faculty2 = new Faculty(1L, "test", 1L);
        faculty1.setAvailableResources(new Resources(11, 22, 33));
        faculty2.setBaseResources(new Resources(10, 20, 30));
        assertEquals(faculty1, faculty2);
    }

    @Test
    void testNotEquals() {
        Faculty faculty1 = new Faculty(1L, "test", 1L);
        Faculty faculty2 = new Faculty(8L, "test", 1L);
        assertNotEquals(faculty1, faculty2);
    }

    @Test
    void testToString() {
        Faculty faculty = new Faculty(1L, "test", 8L);
        faculty.setNodeResources(new Resources(42, 42, 42));
        assertEquals("Faculty{id=1, name='test', baseResources=0, 0, 0, nodeResources=42, 42, 42,"
                + " availableResources=0, 0, 0, managerId=8}", faculty.toString());
    }
}