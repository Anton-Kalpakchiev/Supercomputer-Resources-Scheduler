package nl.tudelft.sem.template.resourcepool.domain.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;


public class ResourcesUnitTests {

    @Test
    public void constructorTest() {
        Resources resource = new Resources(400, 400, 400);
        assertNotNull(resource);
    }

    @Test
    public void getCpuTest() {
        Resources resource = new Resources(500, 400, 400);
        assertEquals(resource.getCpu(), 500);
    }

    @Test
    public void getGpuTest() {
        Resources resource = new Resources(400, 300, 400);
        assertEquals(resource.getGpu(), 300);
    }

    @Test
    public void getMemoryTest() {
        Resources resource = new Resources(400, 400, 300);
        assertEquals(resource.getMemory(), 300);
    }

    @Test
    public void subtractTest() {
        Resources left = new Resources(200, 50, 100);
        Resources right = new Resources(60, 15, 50);
        Resources expectedResult = new Resources(140, 35, 50);
        Resources result = Resources.subtract(left, right);

        assertEquals(result, expectedResult);
    }

    @Test
    public void subtractMutatedTest() {
        Resources left = new Resources(200, 50, 100);
        Resources right = new Resources(60, 15, 50);
        Resources expectedResult = new Resources(140, 35, 50);
        Resources result = Resources.subtractMutated(left, right);

        assertNotEquals(result, expectedResult);
    }

    @Test
    public void toStringTest() {
        Resources resource = new Resources(400, 400, 400);
        String stringTest = "CPU: 400, GPU: 400, Memory: 400";
        assertEquals(stringTest, resource.toString());
    }

    @Test
    public void equalsTest() {
        Resources resource1 = new Resources(400, 400, 400);
        Resources resource2 = new Resources(400, 400, 400);
        assertEquals(resource1, resource2);
    }

    @Test
    public void equalsTestFalse() {
        Resources resource1 = new Resources(400, 400, 400);
        Resources resource2 = new Resources(500, 400, 400);
        assertNotEquals(resource1, resource2);
    }
}