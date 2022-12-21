package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import org.junit.jupiter.api.Test;

class ResourcePoolTest {

    @Test
    public void constructorTest() {
        ResourcePool rp = new ResourcePool("test");
        assertNotNull(rp);
    }

    @Test
    void getId() {
        ResourcePool rp = new ResourcePool("test");
        assertEquals(0L, rp.getId());
    }

    @Test
    void getName() {
        ResourcePool rp = new ResourcePool("test");
        assertEquals("test", rp.getName());
    }

    @Test
    void getBaseResources() {
        ResourcePool rp = new ResourcePool("test");
        assertEquals(new Resources(0, 0, 0), rp.getBaseResources());
    }

    @Test
    void getNodeResources() {
        ResourcePool rp = new ResourcePool("test");
        assertEquals(new Resources(0, 0, 0), rp.getNodeResources());
    }

    @Test
    void getAvailableResources() {
        ResourcePool rp = new ResourcePool("test");
        assertEquals(new Resources(0, 0, 0), rp.getAvailableResources());
    }

    @Test
    void setBaseResources() {
        ResourcePool rp = new ResourcePool("test");
        rp.setBaseResources(new Resources(10, 20, 30));
        assertEquals(new Resources(10, 20, 30), rp.getBaseResources());
    }

    @Test
    void setNodeResources() {
        ResourcePool rp = new ResourcePool("test");
        rp.setNodeResources(new Resources(10, 20, 30));
        assertEquals(new Resources(10, 20, 30), rp.getNodeResources());
    }

    @Test
    void setAvailableResources() {
        ResourcePool rp = new ResourcePool("test");
        rp.setAvailableResources(new Resources(10, 20, 30));
        assertEquals(new Resources(10, 20, 30), rp.getAvailableResources());
    }

    @Test
    void testEquals() {
        ResourcePool rp1 = new ResourcePool("test");
        ResourcePool rp2 = new ResourcePool("test");
        rp1.setAvailableResources(new Resources(11, 22, 33));
        rp2.setBaseResources(new Resources(10, 20, 30));
        assertEquals(rp1, rp2);
    }

    @Test
    void testNotEquals() {
        ResourcePool rp1 = new ResourcePool("test1");
        ResourcePool rp2 = new ResourcePool("test2");
        assertNotEquals(rp1, rp2);
    }

    @Test
    void testToString() {
        ResourcePool rp = new ResourcePool("test");
        rp.setNodeResources(new Resources(42, 42, 42));
        assertEquals("ResourcePool{id=0, name='test', baseResources=(CPU: 0, GPU: 0, Memory: 0), nodeResources="
                + "(CPU: 42, GPU: 42, Memory: 42), availableResources=(CPU: 0, GPU: 0, Memory: 0)}", rp.toString());
    }
}