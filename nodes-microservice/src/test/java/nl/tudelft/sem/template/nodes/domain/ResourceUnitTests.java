package nl.tudelft.sem.template.nodes.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.tudelft.sem.template.nodes.domain.resource.Resource;
import org.junit.jupiter.api.Test;

public class ResourceUnitTests {

    @Test
    public void constructorTest() {
        Resource resource = new Resource(400, 400, 400);
        assertNotNull(resource);
    }

    @Test
    public void getCpuTest() {
        Resource resource = new Resource(500, 400, 400);
        assertEquals(resource.getCpu(), 500);
    }

    @Test
    public void getGpuTest() {
        Resource resource = new Resource(400, 300, 400);
        assertEquals(resource.getGpu(), 300);
    }

    @Test
    public void getMemoryTest() {
        Resource resource = new Resource(400, 400, 300);
        assertEquals(resource.getMemory(), 300);
    }

    @Test
    public void toStringTest() {
        Resource resource = new Resource(400, 400, 400);
        String stringTest = "400, 400, 400";
        assertEquals(stringTest, resource.toString());
    }

    @Test
    public void equalsTest() {
        Resource resource1 = new Resource(400, 400, 400);
        Resource resource2 = new Resource(400, 400, 400);
        assertTrue(resource1.equals(resource2));
    }

    @Test
    public void equalsTestFalse() {
        Resource resource1 = new Resource(400, 400, 400);
        Resource resource2 = new Resource(500, 400, 400);
        assertFalse(resource1.equals(resource2));
    }
}