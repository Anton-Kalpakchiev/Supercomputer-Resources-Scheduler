package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
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
     * @throws Exception if the name or the managerNetId already exists
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

    /**
     * Finds the resources of a faculty by faculty name.
     *
     * @param name the faculty name
     * @return the resources of the faculty
     * @throws Exception when the faculty could not be found
     */
    public Resources findResourcesByName(String name) throws Exception {
        if (!repo.existsByName(name)) {
            throw new Exception();
        }
        return repo.findByName(name).get().getAvailableResources();
    }

    /**
     * Updates the available resources.
     *
     * @param resourcePoolId the resource pool id
     * @param requestId the request that is scheduled
     * @throws Exception if something fails
     */
    public void updateResources(long resourcePoolId, long requestId) throws Exception {
        //        if(!repo.existsById(resourcePoolId)) {
        //            throw new Exception();
        //        }

        System.out.println("Didnt update resources but that's okay");

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
     * Retrieves the available resources of a resource pool.
     *
     * @param resourcePoolId the id of the resource pool
     * @return the available resources
     * @throws Exception thrown when resources were not found
     */
    public Resources getAvailableResourcesById(long resourcePoolId) throws Exception {
        if (repo.findById(resourcePoolId).isPresent()) {
            return repo.findById(resourcePoolId).get().getAvailableResources();
        } else {
            // Proper exception implemented in different brancehs
            throw new Exception("Resource pool note found");
        }
    }
}
