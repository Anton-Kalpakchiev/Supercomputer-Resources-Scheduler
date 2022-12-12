package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import nl.tudelft.sem.template.resourcepool.models.DistributionModel;
import org.springframework.stereotype.Service;

/**
 * A DDD service for registering a new user.
 */
@Service
public class RpManagementService {
    private final transient RpFacultyRepository repo;

    /**
     * Instantiates a new RpManagementService.
     *
     * @param repo the RpFaculty repository
     */
    public RpManagementService(RpFacultyRepository repo) {
        this.repo = repo;
    }

    /**
     * Create a new Faculty.
     *
     * @param name    The name of the new faculty
     * @param managerNetId The NetId of the faculty manager
     * @throws Exception if the user already exists
     */
    public void createFaculty(String name, long managerNetId) throws Exception {
        if (repo.existsByName(name)) {
            throw new NameAlreadyInUseException(name);
        }
        if (repo.existsByManagerNetId(managerNetId)) {
            throw new ManagerNetIdAlreadyAssignedException(managerNetId);
        }
        Faculty faculty = new Faculty(name, managerNetId);
        repo.save(faculty);
    }

    public String getDistribution() {
        List<ResourcePool> rps = repo.findAll();
        Resources totalResources = calculateTotalResources(rps);
        List<ResourceDistribution> distributionList = new ArrayList<>();

        for (ResourcePool rp : rps) {
            String name = rp.getName();
            Resources resources = rp.getBaseResources();
            float relativeCpu = (float) resources.getCpu() / totalResources.getCpu() * 100;
            float relativeGpu = (float) resources.getGpu() / totalResources.getGpu() * 100;
            float relativeMemory = (float) resources.getMemory() / totalResources.getMemory() * 100;
            distributionList.add(new ResourceDistribution(name, resources, relativeCpu, relativeGpu, relativeMemory));
        }
        return distributionList.toString();
    }

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

    /**
     * Returns a string with all resource pools in the database.
     *
     * @return String with all resource pools in the database
     */
    public String printDatabase() {
        return repo.findAll().toString();
    }
}
