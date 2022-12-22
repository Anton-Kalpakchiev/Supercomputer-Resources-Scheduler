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
                resources, 10, 10, 10);
        expected.add(resourceDistribution);

        when(mockRepo.existsByName("name")).thenReturn(true);

        // Act
        distributionService.addDistribution(model);

        // Assert
        assertEquals(expected.toString(), distributionService.getCurrentDistribution());
    }
}