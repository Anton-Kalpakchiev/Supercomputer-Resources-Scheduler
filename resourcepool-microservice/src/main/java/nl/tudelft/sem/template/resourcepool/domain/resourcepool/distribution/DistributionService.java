package nl.tudelft.sem.template.resourcepool.domain.resourcepool.distribution;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.ResourcePool;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.RpFacultyRepository;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import nl.tudelft.sem.template.resourcepool.models.DistributionModel;
import org.springframework.stereotype.Service;

/**
 * A DDD service for distributing the resources.
 */
@Service
//We can remove this line later on, but I can't figure out how to fix this and the code works perfect with the error in it
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class DistributionService {
    private final transient RpFacultyRepository repo;

    private final transient Resources systemResources;

    private final transient List<ResourceDistribution> distributions;

    private static final double TOTAL_PERCENTAGE = 100.0;

    /**
     * Instantiates a new DistributionService and sets the initial resources in the system.
     *
     * @param repo the RpFaculty repository
     */
    public DistributionService(RpFacultyRepository repo) {
        this.repo = repo;
        systemResources = new Resources(1000, 200, 8000); //initial resources in the system
        distributions = new ArrayList<>();
    }

    /**
     * Creates the free pool on service startup.
     */
    @PostConstruct
    public void createFreePool() {
        ResourcePool freePool = new ResourcePool("Free pool");
        freePool.setBaseResources(systemResources);
        repo.save(freePool);
    }

    /**
     * Returns a string with the current distribution of the resources in the system.
     *
     * @return String with the current distribution of the resources in the system
     */
    public String getCurrentDistribution() {
        List<ResourcePool> rps = repo.findAll();
        Resources totalResources = calculateTotalResources(rps);
        List<ResourceDistribution> distributionList = new ArrayList<>();

        for (ResourcePool rp : rps) {
            String name = rp.getName();
            Resources resources = rp.getBaseResources();
            double relativeCpu = (double) resources.getCpu() / totalResources.getCpu() * 100;
            double relativeGpu = (double) resources.getGpu() / totalResources.getGpu() * 100;
            double relativeMemory = (double) resources.getMemory() / totalResources.getMemory() * 100;
            distributionList.add(new ResourceDistribution(name, resources, relativeCpu, relativeGpu, relativeMemory));
        }
        return distributionList.toString();
    }

    /**
     * Adds a distribution for a faculty to the queue.
     *
     * @param distribution the wanted percentage of resources for a faculty
     * @throws Exception if the faculty name is invalid
     */
    public void addDistribution(DistributionModel distribution) throws Exception {
        String name = distribution.getName();
        List<String> names = new ArrayList<>();
        for (ResourceDistribution temp : distributions) {
            names.add(temp.getName());
        }

        if (!repo.existsByName(name) || names.contains(name)) {
            throw new FacultyNameNotValidException(name);
        }

        distributions.add(new ResourceDistribution(name, new Resources(0, 0, 0),
                distribution.getCpu(), distribution.getGpu(), distribution.getMemory()));
    }

    /**
     * Returns a string with the current distributions in the queue.
     *
     * @return String with the current distributions in the queue
     */
    public String statusDistribution() {
        return distributions.toString();
    }

    /**
     * Saves all the current faculty distributions in the queue to the full system.
     *
     * @throws Exception if there is a wrong amount of distributions or if the percentages don't add up
     */
    public void saveDistribution() throws Exception {
        validateDistribution();
        List<ResourcePool> rps = repo.findAll();
        for (ResourceDistribution distribution : distributions) {
            String name = distribution.getName();
            ResourcePool resourcePool;
            Optional<ResourcePool> tempResourcePool = repo.findByName(name);
            if (tempResourcePool.isEmpty()) {
                break;
            }
            resourcePool = tempResourcePool.get();
            double cpu = distribution.getPercentageCpu() / 100.0 * systemResources.getCpu();
            double gpu = distribution.getPercentageGpu() / 100.0 * systemResources.getGpu();
            double memory = distribution.getPercentageMemory() / 100.0 * systemResources.getMemory();
            Resources resources = new Resources((int) cpu, (int) gpu, (int) memory);
            resourcePool.setBaseResources(resources);
            repo.save(resourcePool);
        }
        distributions.clear();
    }

    /**
     * Clears the queue with all the current faculty distributions.
     */
    public void clearDistribution() {
        distributions.clear();
    }

    /**
     * Validates if the queue is valid to make official by checking its size and checking the percentages.
     *
     * @throws Exception if there is a wrong amount of distributions or if the percentages don't add up
     */
    public void validateDistribution() throws Exception {
        List<ResourcePool> rps = repo.findAll();
        if (rps.size() != distributions.size()) {
            throw new WrongAmountOfFacultiesSubmittedException();
        }

        double totalCpu = 0.0;
        double totalGpu = 0.0;
        double totalMemory = 0.0;
        for (ResourceDistribution distribution : distributions) {
            totalCpu += distribution.getPercentageCpu();
            totalGpu += distribution.getPercentageGpu();
            totalMemory += distribution.getPercentageMemory();
        }
        if (totalCpu != TOTAL_PERCENTAGE) {
            throw new ResourceSumNotCorrectException("cpu");
        }
        if (totalGpu != TOTAL_PERCENTAGE) {
            throw new ResourceSumNotCorrectException("gpu");
        }
        if (totalMemory != TOTAL_PERCENTAGE) {
            throw new ResourceSumNotCorrectException("memory");
        }
    }

    /**
     * Returns a Resources object with the total of all base resources in the list.
     *
     * @param rps a list of the resource pools of which we want to add up the base resources
     * @return Resources object with the total of all base resources in the list
     */
    public Resources calculateTotalResources(List<ResourcePool> rps) {
        int totalCpu = 0;
        int totalGpu = 0;
        int totalMemory = 0;
        for (ResourcePool rp : rps) {
            Resources resources = rp.getBaseResources();
            totalCpu += resources.getCpu();
            totalGpu += resources.getGpu();
            totalMemory += resources.getMemory();
        }
        if (totalCpu == 0) {
            totalCpu++;
        }
        if (totalGpu == 0) {
            totalGpu++;
        }
        if (totalMemory == 0) {
            totalMemory++;
        }
        return new Resources(totalCpu, totalGpu, totalMemory);
    }
}
