package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import org.junit.jupiter.api.Test;

class ResourcePoolTest {

    @Test
    public void constructorTest() {
        ResourcePool rp = new ResourcePool(1L, "test");
        assertNotNull(rp);
    }

    @Test
    void getId() {
        ResourcePool rp = new ResourcePool(1L, "test");
        assertEquals(1L, rp.getId());
    }

    @Test
    void getName() {
        ResourcePool rp = new ResourcePool(1L, "test");
        assertEquals("test", rp.getName());
    }

    @Test
    void getBaseResources() {
        ResourcePool rp = new ResourcePool(1L, "test");
        assertEquals(new Resources(0, 0, 0), rp.getBaseResources());
    }

    @Test
    void getNodeResources() {
        ResourcePool rp = new ResourcePool(1L, "test");
        assertEquals(new Resources(0, 0, 0), rp.getNodeResources());
    }

    @Test
    void getAvailableResources() {
        ResourcePool rp = new ResourcePool(1L, "test");
        assertEquals(new Resources(0, 0, 0), rp.getAvailableResources());
    }

    @Test
    void setBaseResources() {
        ResourcePool rp = new ResourcePool(1L, "test");
        rp.setBaseResources(new Resources(10, 20, 30));
        assertEquals(new Resources(10, 20, 30), rp.getBaseResources());
    }

    @Test
    void setNodeResources() {
        ResourcePool rp = new ResourcePool(1L, "test");
        rp.setNodeResources(new Resources(10, 20, 30));
        assertEquals(new Resources(10, 20, 30), rp.getNodeResources());
    }

    @Test
    void setAvailableResources() {
        ResourcePool rp = new ResourcePool(1L, "test");
        rp.setAvailableResources(new Resources(10, 20, 30));
        assertEquals(new Resources(10, 20, 30), rp.getAvailableResources());
    }

    @Test
    void testEquals() {
        ResourcePool rp1 = new ResourcePool(1L, "test");
        ResourcePool rp2 = new ResourcePool(1L, "test2");
        rp1.setAvailableResources(new Resources(11, 22, 33));
        rp2.setBaseResources(new Resources(10, 20, 30));
        assertEquals(rp1, rp2);
    }

    @Test
    void testNotEquals() {
        ResourcePool rp1 = new ResourcePool(1L, "test");
        ResourcePool rp2 = new ResourcePool(8L, "test");
        assertNotEquals(rp1, rp2);
    }

    @Test
    void testToString() {
        ResourcePool rp = new ResourcePool(1L, "test");
        rp.setNodeResources(new Resources(42, 42, 42));
        assertEquals("ResourcePool{id=1, name='test', baseResources=0, 0, 0,"
                + " nodeResources=42, 42, 42, availableResources=0, 0, 0}", rp.toString());
    }
}