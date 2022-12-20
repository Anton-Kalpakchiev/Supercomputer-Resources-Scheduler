package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import nl.tudelft.sem.template.resourcepool.domain.resourcepool.distribution.ResourceDistribution;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import org.junit.jupiter.api.Test;

class ResourceDistributionTest {

    @Test
    public void constructorTest() {
        ResourceDistribution distribution = new ResourceDistribution("EEMCS", new Resources(1, 2, 3),
                                        10.0, 20.0, 30.0);
        assertNotNull(distribution);
    }

    @Test
    void getName() {
        ResourceDistribution distribution = new ResourceDistribution("EEMCS", new Resources(1, 2, 3),
                10.0, 20.0, 30.0);
        assertEquals("EEMCS", distribution.getName());
    }

    @Test
    void getResources() {
        ResourceDistribution distribution = new ResourceDistribution("EEMCS", new Resources(1, 2, 3),
                10.0, 20.0, 30.0);
        assertEquals(new Resources(1, 2, 3), distribution.getResources());
    }

    @Test
    void getPercentageCpu() {
        ResourceDistribution distribution = new ResourceDistribution("EEMCS", new Resources(1, 2, 3),
                10.0, 20.0, 30.0);
        assertEquals(10.0, distribution.getPercentageCpu());
    }

    @Test
    void getPercentageGpu() {
        ResourceDistribution distribution = new ResourceDistribution("EEMCS", new Resources(1, 2, 3),
                10.0, 20.0, 30.0);
        assertEquals(20.0, distribution.getPercentageGpu());
    }

    @Test
    void getPercentageMemory() {
        ResourceDistribution distribution = new ResourceDistribution("EEMCS", new Resources(1, 2, 3),
                10.0, 20.0, 30.0);
        assertEquals(30.0, distribution.getPercentageMemory());
    }

    @Test
    void testEquals() {
        ResourceDistribution distribution1 = new ResourceDistribution("EEMCS", new Resources(1, 2, 3),
                10.0, 20.0, 30.0);
        ResourceDistribution distribution2 = new ResourceDistribution("EEMCS", new Resources(1, 2, 3),
                10.0, 20.0, 30.0);
        assertEquals(distribution1, distribution2);
    }

    @Test
    void testNotEquals() {
        ResourceDistribution distribution1 = new ResourceDistribution("EEMCS", new Resources(1, 2, 3),
                10.0, 20.0, 30.0);
        ResourceDistribution distribution2 = new ResourceDistribution("EEMCS", new Resources(1, 2, 3),
                10.0, 20.5, 30.0);
        assertNotEquals(distribution1, distribution2);
    }

    @Test
    void testToString() {
        ResourceDistribution distribution = new ResourceDistribution("EEMCS", new Resources(1, 2, 3),
                10.0, 20.0, 30.0);
        assertEquals("{EEMCS, (1, 2, 3), (10.0%, 20.0%, 30.0%)}", distribution.toString());
    }
}