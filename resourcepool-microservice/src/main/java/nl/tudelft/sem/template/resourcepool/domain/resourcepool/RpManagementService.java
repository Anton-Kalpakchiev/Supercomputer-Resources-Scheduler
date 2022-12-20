package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

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

}
