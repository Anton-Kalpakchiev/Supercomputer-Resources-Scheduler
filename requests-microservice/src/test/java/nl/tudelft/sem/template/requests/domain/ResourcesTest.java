package nl.tudelft.sem.template.requests.domain;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourcesTest {
    @Test
    public void createValidResources() throws Exception {
        Resources resources = new Resources(3, 2, 1);
        assertThat(resources.getMem() == 3);
        assertThat(resources.getCpu() == 2);
        assertThat(resources.getGpu() == 1);
    }

    @Test
    public void testToString() throws Exception {
        Resources resources = new Resources(3, 2, 1);
        String expected = "Memory: 3 CPU: 2 GPU: 1";
        assertEquals(resources.toString(), expected);
    }

    @Test
    public void createNegativeMemory() throws Exception {
        assertThrows(InvalidResourcesException.class, () -> {
            Resources resources = new Resources(-3, 2, 1);
        });
    }

    @Test
    public void createNegativeCpu() throws Exception {
        assertThrows(InvalidResourcesException.class, () -> {
            Resources resources = new Resources(3, -2, 1);
        });
    }

    @Test
    public void createNegativeGpu() throws Exception {
        assertThrows(InvalidResourcesException.class, () -> {
            Resources resources = new Resources(3, 2, -1);
        });
    }

    @Test
    public void createNotEnoughCpu() throws Exception {
        assertThrows(InvalidResourcesException.class, () -> {
            Resources resources = new Resources(30, 49, 50);
        });
    }
}