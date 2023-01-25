package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import java.util.Optional;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import nl.tudelft.sem.template.resourcepool.models.NodeInteractionRequestModel;
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
     * @param facultyId the id
     * @return the resources pool if found
     */
    public boolean verifyFaculty(long facultyId) {
        return repo.existsById(facultyId);
    }

    /**
     * Contributes a node to a faculty.
     *
     * @param nodeInfo The information needed to contribute the resources of the node
     * @return true if the contribution succeeded
     * @throws FacultyIdNotFoundException if a faculty with the given id can't be found
     */
    public boolean contributeNode(NodeInteractionRequestModel nodeInfo) throws FacultyIdNotFoundException {
        ResourcePool faculty = getFacultyById(nodeInfo.getFacultyId());
        Resources currentNodeResources = faculty.getNodeResources();
        Resources node = new Resources(nodeInfo.getCpu(), nodeInfo.getGpu(), nodeInfo.getMemory());
        faculty.setNodeResources(Resources.add(currentNodeResources, node));
        repo.save(faculty);
        return true;
    }

    /**
     * Mutation of the regular contributeNode method which fails to save to repository.
     *
     * @param nodeInfo The information needed to contribute the resources of the node
     * @return true if the contribution succeeded
     * @throws FacultyIdNotFoundException if a faculty with the given id can't be found
     */
    public boolean contributeNodeMutated(NodeInteractionRequestModel nodeInfo) throws FacultyIdNotFoundException {
        ResourcePool faculty = getFacultyById(nodeInfo.getFacultyId());
        Resources currentNodeResources = faculty.getNodeResources();
        Resources node = new Resources(nodeInfo.getCpu(), nodeInfo.getGpu(), nodeInfo.getMemory());
        faculty.setNodeResources(Resources.add(currentNodeResources, node));
        // Mutation: updated faculty is no longer re-saved to repo
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
        ResourcePool faculty = getFacultyById(nodeInfo.getFacultyId());
        Resources currentNodeResources = faculty.getNodeResources();
        Resources node = new Resources(nodeInfo.getCpu(), nodeInfo.getGpu(), nodeInfo.getMemory());
        Resources newNodeResources = Resources.subtract(currentNodeResources, node);
        if (!checkEnoughResourcesRemaining(newNodeResources)) {
            throw new RemainingResourcesInsufficientException(nodeInfo.getFacultyId());
        }
        faculty.setNodeResources(newNodeResources);
        repo.save(faculty);
        return true;
    }

    /**
     * Returns the Faculty object if the ID can be found in the database.
     *
     * @param facultyId the ID of the wanted faculty
     * @return the Faculty object related with the ID
     * @throws FacultyIdNotFoundException if the ID couldn't be found in the database
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public ResourcePool getFacultyById(long facultyId) throws FacultyIdNotFoundException {
        if (!repo.existsById(facultyId)) {
            throw new FacultyIdNotFoundException(facultyId);
        }
        return repo.findById(facultyId).get();
    }

    /**
     * Checks if all resource fields stay above 0.
     *
     * @param nodeResources the Resources object to be checked
     * @return whether the fields are bigger than 0
     */
    public boolean checkEnoughResourcesRemaining(Resources nodeResources) {
        return nodeResources.getCpu() >= 0 && nodeResources.getGpu() >= 0 && nodeResources.getMemory() >= 0;
    }

    /**
     * Finds the resourcePool given the id.
     *
     * @param resourcePoolId the id
     * @return the optional resourcePool
     */
    public Optional<ResourcePool> findById(long resourcePoolId) {
        return repo.findById(resourcePoolId);
    }

    /**
     * Finds the resourcePool given the name.
     *
     * @param facultyName the faculty name
     * @return the optional resourcePool
     */
    public Optional<ResourcePool> findByName(String facultyName) {
        return repo.findByName(facultyName);
    }


    /**
     * Returns a string with all resource pools in the database.
     *
     * @return String with all resource pools in the database
     */
    public String printDatabase() {
        return repo.findAll().toString();
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
