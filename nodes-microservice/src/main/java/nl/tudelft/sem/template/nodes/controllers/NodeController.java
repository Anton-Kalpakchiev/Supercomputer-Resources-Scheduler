package nl.tudelft.sem.template.nodes.controllers;

import nl.tudelft.sem.template.nodes.authentication.AuthManager;
import nl.tudelft.sem.template.nodes.domain.node.Name;
import nl.tudelft.sem.template.nodes.domain.node.Node;
import nl.tudelft.sem.template.nodes.domain.node.NodeManagementService;
import nl.tudelft.sem.template.nodes.domain.node.NodeUrl;
import nl.tudelft.sem.template.nodes.domain.node.Token;
import nl.tudelft.sem.template.nodes.domain.resources.Resources;
import nl.tudelft.sem.template.nodes.models.NodeContributionRequestModel;
import nl.tudelft.sem.template.nodes.models.NodeDeletionRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for endpoints related to nodes.
 */
@RestController
public class NodeController {

    private final transient NodeManagementService nodeManagementService;

    private final transient AuthManager authManager;

    /**
     * Instantiates a new controller.
     *
     * @param nodeManagementService The service which will handle the business logic for node management
     */
    @Autowired
    public NodeController(NodeManagementService nodeManagementService, AuthManager authManager) {
        this.nodeManagementService = nodeManagementService;
        this.authManager = authManager;
    }

    /**
     * Contributes a node to a specific faculty.
     *
     * @param nodeInfo the needed information to create a node
     * @return 200 OK if the contribution was successful
     * @throws Exception if some information is invalid
     */
    @PostMapping("/contributeNode")
    public ResponseEntity contributeNode(@RequestBody NodeContributionRequestModel nodeInfo) throws Exception {
        try {
            Name name = new Name(nodeInfo.getName());
            NodeUrl url = new NodeUrl(nodeInfo.getUrl());
            String ownerNetId = authManager.getNetId();
            long facultyId = nodeInfo.getFacultyId();
            Token token = new Token(nodeInfo.getToken());
            Resources resources = new Resources(nodeInfo.getCpu(), nodeInfo.getGpu(), nodeInfo.getMemory());
            Node node = nodeManagementService.registerNode(name, url, ownerNetId, facultyId, token, resources);
            return ResponseEntity.ok(node.getId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Deletes a node.
     *
     * @param nodeId the id of the node to be deleted
     * @return 200 OK if the deletion was successful
     * @throws Exception if the node id couldn't be found
     */
    @PostMapping("/deleteNode")
    public ResponseEntity deleteNode(@RequestBody NodeDeletionRequestModel nodeId) throws Exception {
        try {
            nodeManagementService.deleteNode(nodeId.getNodeId(), authManager.getNetId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }
}
