package nl.tudelft.sem.template.resourcepool.domain.resourcepool.distribution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.ResourcePool;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.RpFacultyRepository;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import nl.tudelft.sem.template.resourcepool.models.DistributionModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DistributionServiceTest {

    @MockBean
    private transient RpFacultyRepository mockRepo;

    @Autowired
    private transient DistributionService distributionService;

    @Test
    void createFreePoolTest() {
        distributionService.createFreePool();
        verify(mockRepo, times(2)).save(any(ResourcePool.class));
    }

    @Test
    void calculateTotalDistributionTest() throws Exception {
        // Arrange
        ResourcePool rp1 = new ResourcePool("rp1");
        ResourcePool rp2 = new ResourcePool("rp2");

        Resources resources1 = new Resources(10, 25, 30);
        Resources resources2 = new Resources(12, 5, 17);

        rp1.setBaseResources(resources1);
        rp2.setBaseResources(resources2);

        List<ResourcePool> resourcePools = new ArrayList<>();
        resourcePools.add(rp1);
        resourcePools.add(rp2);

        Resources expected = new Resources(22, 30, 47);

        // Act
        Resources result = distributionService.calculateTotalResources(resourcePools);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void addDistributionTest() throws Exception {
        // Arrange
        DistributionModel model = new DistributionModel();
        model.setName("name");
        model.setMemory(20);
        model.setCpu(20);
        model.setGpu(20);

        List<ResourceDistribution> expected = new ArrayList<>();
        Resources resources = new Resources(20, 20, 20);
        ResourceDistribution resourceDistribution = new ResourceDistribution("name",
                resources, 100, 100, 100);
        expected.add(resourceDistribution);

        ResourcePool resourcePool = new ResourcePool("name");
        resourcePool.setBaseResources(resources);
        List<ResourcePool> resourcePools = new ArrayList<>();
        resourcePools.add(resourcePool);

        when(mockRepo.existsByName("name")).thenReturn(true);
        when(mockRepo.findAll()).thenReturn(resourcePools);

        // Act
        distributionService.addDistribution(model);

        // Assert
        assertEquals(expected.toString(), distributionService.getCurrentDistribution());
    }
}