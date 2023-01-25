package nl.tudelft.sem.template.resourcepool.domain.resourcepool.distribution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        model.setName("wrong name");
        assertThrows(FacultyNameNotValidException.class, () -> {
            distributionService.addDistribution(model);
        });
    }

    @Test
    void validateDistributionTest() throws Exception {
        // Arrange
        DistributionModel notEnoughCpu = new DistributionModel();
        notEnoughCpu.setName("name");
        notEnoughCpu.setMemory(100);
        notEnoughCpu.setCpu(99);
        notEnoughCpu.setGpu(100);

        DistributionModel notEnoughGpu = new DistributionModel();
        notEnoughGpu.setName("name");
        notEnoughGpu.setMemory(100);
        notEnoughGpu.setCpu(100);
        notEnoughGpu.setGpu(99);

        DistributionModel notEnoughMemory = new DistributionModel();
        notEnoughMemory.setName("name");
        notEnoughMemory.setMemory(99);
        notEnoughMemory.setCpu(100);
        notEnoughMemory.setGpu(100);

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

        // Act and Assert

        assertThrows(WrongAmountOfFacultiesSubmittedException.class, () -> {
            distributionService.validateDistribution();
        });

        distributionService.addDistribution(notEnoughCpu);
        Throwable exceptionCpu = assertThrows(ResourceSumNotCorrectException.class, () -> {
            distributionService.validateDistribution();
        });
        assertEquals("cpu", exceptionCpu.getMessage());

        distributionService.clearDistribution();
        distributionService.addDistribution(notEnoughGpu);
        Throwable exceptionGpu = assertThrows(ResourceSumNotCorrectException.class, () -> {
            distributionService.validateDistribution();
        });
        assertEquals("gpu", exceptionGpu.getMessage());

        distributionService.clearDistribution();
        distributionService.addDistribution(notEnoughMemory);
        Throwable exceptionMemory = assertThrows(ResourceSumNotCorrectException.class, () -> {
            distributionService.validateDistribution();
        });
        assertEquals("memory", exceptionMemory.getMessage());
    }

    @Test
    void saveDistributionTest() throws Exception {
        // Arrange
        DistributionModel model = new DistributionModel();
        model.setName("name");
        model.setMemory(100);
        model.setCpu(100);
        model.setGpu(100);

        Resources resources = new Resources(20, 20, 20);
        ResourcePool resourcePool = new ResourcePool("name");
        resourcePool.setBaseResources(resources);
        List<ResourcePool> resourcePools = new ArrayList<>();
        resourcePools.add(resourcePool);

        when(mockRepo.existsByName("name")).thenReturn(true);
        when(mockRepo.findAll()).thenReturn(resourcePools);

        // Act
        distributionService.addDistribution(model);
        distributionService.saveDistribution();

        // Assert
        assertEquals("[]", distributionService.statusDistribution());
        verify(mockRepo, times(1)).save(any(ResourcePool.class));

    }

    @Test
    void killSaveDistributionMutant() throws Exception {
        DistributionModel model1 = new DistributionModel();
        model1.setName("name1");
        model1.setCpu(80);
        model1.setGpu(90);
        model1.setMemory(60);
        DistributionModel model2 = new DistributionModel();
        model2.setName("name2");
        model2.setCpu(20);
        model2.setGpu(30);
        model2.setMemory(40);

        Resources resources = new Resources(100, 100, 100);
        ResourcePool resourcePool1 = new ResourcePool("name1");
        resourcePool1.setBaseResources(resources);
        ResourcePool resourcePool2 = new ResourcePool("name2");
        resourcePool2.setBaseResources(resources);
        List<ResourcePool> resourcePools = new ArrayList<>();
        resourcePools.add(resourcePool1);
        resourcePools.add(resourcePool2);

        when(mockRepo.existsByName("name1")).thenReturn(true);
        when(mockRepo.existsByName("name2")).thenReturn(true);
        when(mockRepo.findAll()).thenReturn(resourcePools);

        distributionService.addDistribution(model1);
        distributionService.addDistribution(model2);

        ResourceSumNotCorrectException exc = assertThrows(ResourceSumNotCorrectException.class,
                () -> distributionService.saveDistribution());
        assertEquals("gpu", exc.getMessage());

        distributionService.saveDistributionMutated();
        assertEquals("[]", distributionService.statusDistribution());
        verify(mockRepo, times(1)).save(any(ResourcePool.class));
    }
}