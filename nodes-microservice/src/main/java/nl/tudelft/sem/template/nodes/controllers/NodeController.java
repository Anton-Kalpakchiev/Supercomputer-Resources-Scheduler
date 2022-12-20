package nl.tudelft.sem.template.nodes.controllers;

import nl.tudelft.sem.template.nodes.authentication.AuthManager;
import nl.tudelft.sem.template.nodes.domain.node.Name;
import nl.tudelft.sem.template.nodes.domain.node.Node;
import nl.tudelft.sem.template.nodes.domain.node.NodeManagementService;
import nl.tudelft.sem.template.nodes.domain.node.NodeUrl;
import nl.tudelft.sem.template.nodes.domain.node.Token;
import nl.tudelft.sem.template.nodes.domain.resources.Resources;
import nl.tudelft.sem.template.nodes.models.NodeContributionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Hello World nodes controller.
 * <p>
 * This controller shows how you can extract information from the JWT token.
 * </p>
 */
@RestController
public class NodeController {

    private final transient AuthManager authManager;

    private final transient NodeManagementService nodeManagementService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     * @param nodeManagementService The service which will handle the business logic for node management
     */
    @Autowired
    public NodeController(AuthManager authManager, NodeManagementService nodeManagementService) {
        this.authManager = authManager;
        this.nodeManagementService = nodeManagementService;
    }

    /**
     * Contributes a node to a specific faculty.
     *
     * @param nodeInfo the needed information to create a node
     * @return 200 OK if the contribution was successful
     * @throws Exception if some information is invalid
     */
    @PostMapping("/contributeNode")
    public ResponseEntity contributeNode(@RequestBody NodeContributionModel nodeInfo) throws Exception {
        try {
            long facultyId = nodeInfo.getFacultyId();
            Name name = new Name(nodeInfo.getName());
            NodeUrl url = new NodeUrl(nodeInfo.getUrl());
            Token token = new Token(nodeInfo.getToken());
            Resources resources = new Resources(nodeInfo.getCpu(), nodeInfo.getGpu(), nodeInfo.getMemory());
            Node node = nodeManagementService.registerNode(facultyId, name, url, token, resources);
            return ResponseEntity.ok(node.getId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
