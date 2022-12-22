package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import nl.tudelft.sem.template.resourcepool.models.NodeInteractionRequestModel;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * A DDD service for managing the resource pools.
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
     * Creates a new Faculty.
     *
     * @param name    The name of the new faculty
     * @param managerNetId The NetId of the faculty manager
     * @return the newly created faculty
     * @throws Exception if the name or the managerNetId already exists
     */
    public Faculty createFaculty(String name, String managerNetId) throws Exception {
        if (repo.existsByName(name)) {
            throw new NameAlreadyInUseException(name);
        }
        if (repo.existsByManagerNetId(managerNetId)) {
            throw new ManagerNetIdAlreadyAssignedException(managerNetId);
        }
        Faculty faculty = new Faculty(name, managerNetId);
        repo.save(faculty);
        return faculty;
    }

    /**
     * Finds the resource pool given the id.
     *
     * @param resourcePoolId the id
     * @return the resources pool if found
     * @throws Exception when the resource pool could not be found
     */
    public boolean verifyFaculty(long facultyId) {
        return repo.existsById(facultyId);
    }

    /**
     * Contributes a node to a faculty.
     *
     * @param nodeInfo The information needed to contribute the resources of the node
     * @return true if the contribution succeeded
     * @throws Exception if a faculty with the given id can't be found
     */
    public boolean contributeNode(NodeInteractionRequestModel nodeInfo) throws Exception {
        long facultyId = nodeInfo.getFacultyId();
        if (!repo.existsById(facultyId)) {
            throw new FacultyIdNotFoundException(facultyId);
        }
        ResourcePool faculty = repo.findById(facultyId).get();
        Resources currentNodeResources = faculty.getNodeResources();
        int cpu = currentNodeResources.getCpu() + nodeInfo.getCpu();
        int gpu = currentNodeResources.getGpu() + nodeInfo.getGpu();
        int memory = currentNodeResources.getMemory() + nodeInfo.getMemory();
        faculty.setNodeResources(new Resources(cpu, gpu, memory));
        repo.save(faculty);
        return true;
    }

    /**
     * Deletes a node from a faculty.
     *
     * @param nodeInfo The information needed to delete the resources of the node
     * @return true if the deletion succeeded
     * @throws Exception if a faculty with the given id can't be found or doesn't have enough resources
     */
    public boolean deleteNode(NodeInteractionRequestModel nodeInfo) throws Exception {
        long facultyId = nodeInfo.getFacultyId();
        if (!repo.existsById(facultyId)) {
            throw new FacultyIdNotFoundException(facultyId);
        }
        ResourcePool faculty = repo.findById(facultyId).get();
        Resources currentNodeResources = faculty.getNodeResources();
        int cpu = currentNodeResources.getCpu() - nodeInfo.getCpu();
        int gpu = currentNodeResources.getGpu() - nodeInfo.getGpu();
        int memory = currentNodeResources.getMemory() - nodeInfo.getMemory();
        if (cpu < 0 || gpu < 0 || memory < 0) {
            throw new RemainingResourcesInsufficientException(facultyId);
        }
        faculty.setNodeResources(new Resources(cpu, gpu, memory));
        repo.save(faculty);
        return true;
    }

    public Optional<ResourcePool> findById(long resourcePoolId) throws Exception {
        if (!repo.existsById(resourcePoolId)) {
            throw new Exception();
        }
        return repo.findById(resourcePoolId);
    }


    /**
     * Returns a string with all resource pools in the database.
     *
     * @return String with all resource pools in the database
     */
    public String printDatabase() {
        return repo.findAll().toString();
    }

    /**
     * Check whether a faculty exists in the repository.
     *
     * @param facultyId the faculty to be verified
     * @return whether it exists
     */
    public boolean verifyFaculty(long facultyId) {
        return repo.existsById(facultyId);
    }

    //    /**
    //     * Finds the resources of a faculty by faculty name.
    //     *
    //     * @param name the faculty name
    //     * @return the resources of the faculty
    //     * @throws Exception when the faculty could not be found
    //     */
    //    public Resources findResourcesByName(String name) throws FacultyNotFoundException {
    //        if (!repo.existsByName(name)) {
    //            throw new FacultyNotFoundException(name);
    //        }
    //        return repo.findByName(name).get().getAvailableResources();
    //    }

    //    /**
    //     * Retrieves the available resources of a resource pool.
    //     *
    //     * @param resourcePoolId the id of the resource pool
    //     * @return the available resources
    //     * @throws Exception thrown when resources were not found
    //     */
    //    public Resources getAvailableResourcesById(long resourcePoolId) throws Exception {
    //        if (repo.findById(resourcePoolId).isPresent()) {
    //            return repo.findById(resourcePoolId).get().getAvailableResources();
    //        } else {
    //            // Proper exception implemented in different branches
    //            throw new Exception("Resource pool does not exist");
    //        }
    //    }

}
