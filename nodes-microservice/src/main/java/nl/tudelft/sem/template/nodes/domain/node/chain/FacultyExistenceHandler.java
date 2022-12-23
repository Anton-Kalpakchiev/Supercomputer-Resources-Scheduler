package nl.tudelft.sem.template.nodes.domain.node.chain;

import nl.tudelft.sem.template.nodes.domain.node.NodeManagementService;
import nl.tudelft.sem.template.nodes.domain.node.NodeRepository;


public class FacultyExistenceHandler extends BaseHandler {

    private final transient NodeManagementService nodeManagementService;

    public FacultyExistenceHandler(NodeRepository repo, NodeManagementService nodeManagementService) {
        super(repo);
        this.nodeManagementService = nodeManagementService;
    }

    @Override
    public boolean handle(long nodeId) throws InvalidRequestException {
        long facultyId = super.getRepo().findById(nodeId).get().getFacultyId();

        if (!nodeManagementService.verifyFaculty(facultyId)) {
            throw new FacultyDoesNotExistException(nodeId);
        }
        return super.checkNext(nodeId);
    }
}
