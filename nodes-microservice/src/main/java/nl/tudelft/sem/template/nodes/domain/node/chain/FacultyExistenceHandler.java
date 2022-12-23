package nl.tudelft.sem.template.nodes.domain.node.chain;

import nl.tudelft.sem.template.nodes.domain.node.NodeManagementService;
import nl.tudelft.sem.template.nodes.domain.node.NodeRepository;

/**
 * The Handler that checks whether a facultyId exists in the ResourcePool-microservice.
 */
public class FacultyExistenceHandler extends BaseHandler {

    private final transient NodeManagementService nodeManagementService;

    /**
     * Constructs a FacultyExistenceHandler.
     *
     * @param repo the NodeRepository of the system
     * @param nodeManagementService the service where we make a request to the ResourcePool-microservice
     */
    public FacultyExistenceHandler(NodeRepository repo, NodeManagementService nodeManagementService) {
        super(repo);
        this.nodeManagementService = nodeManagementService;
    }

    /**
     * Verifies whether a facultyId exists in the ResourcePool-microservice.
     *
     * @param nodeId the id which is passed around and used to get the necessary information
     * @return whether the next handler passed recursively
     * @throws InvalidRequestException if the facultyId doesn't exist
     */
    @Override
    public boolean handle(long nodeId) throws InvalidRequestException {
        long facultyId = super.getRepo().findById(nodeId).get().getFacultyId();

        if (!nodeManagementService.verifyFaculty(facultyId)) {
            throw new FacultyDoesNotExistException(nodeId);
        }
        return super.checkNext(nodeId);
    }
}
