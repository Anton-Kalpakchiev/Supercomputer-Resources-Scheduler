package nl.tudelft.sem.template.resourcepool.controllers;

import java.util.Optional;
import nl.tudelft.sem.template.resourcepool.authentication.AuthManager;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.Faculty;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.ResourcePool;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.RpManagementService;
import nl.tudelft.sem.template.resourcepool.models.FacultyCreationModel;
import nl.tudelft.sem.template.resourcepool.models.FacultyCreationResponseModel;
import nl.tudelft.sem.template.resourcepool.models.NodeInteractionRequestModel;
import nl.tudelft.sem.template.resourcepool.models.NodeInteractionResponseModel;
import nl.tudelft.sem.template.resourcepool.models.VerifyFacultyRequestModel;
import nl.tudelft.sem.template.resourcepool.models.VerifyFacultyResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for endpoints related to the management of resource pools.
 */
@RestController
public class ResourcePoolController {

    private final transient AuthManager authManager;

    private final transient RpManagementService rpManagementService;

    /**
     * Instantiates a new ResourcePoolController.
     *
     * @param authManager         Spring Security component used to authenticate and authorize the user
     * @param rpManagementService The service which will handle the business logic for managing the faculties
     */
    @Autowired
    public ResourcePoolController(AuthManager authManager, RpManagementService rpManagementService) {
        this.authManager = authManager;
        this.rpManagementService = rpManagementService;
    }

    /**
     * Returns a string-representation of the resource pool found in the database with the given id.
     *
     * @return ResponseEntity containing a string of the resource pool found in the database with the given id
     */
    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello " + authManager.getNetId());
    }

    /**
     * Endpoint for creating a faculty.
     *
     * @param request The faculty registration model
     * @return 200 OK if the creation is successful
     * @throws Exception if a faculty with this name already exists
     */
    @PostMapping("/createFaculty")
    public ResponseEntity<FacultyCreationResponseModel> createFaculty(@RequestBody FacultyCreationModel request) {
        try {
            Faculty newFaculty = rpManagementService.createFaculty(request.getName(), request.getManagerNetId());
            return ResponseEntity.ok(new FacultyCreationResponseModel(newFaculty.getId()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Verifies whether a faculty exists or not.
     *
     * @param request the request to verify a faculty
     * @return a response indicating whether it exists
     * @throws Exception when request is incorrectly formatted
     */
    @PostMapping("/verifyFaculty")
    public ResponseEntity<VerifyFacultyResponseModel> verifyFaculty(@RequestBody VerifyFacultyRequestModel request)
        throws Exception {
        try {

            boolean result = rpManagementService.verifyFaculty(request.getFacultyId());
            return ResponseEntity.ok(new VerifyFacultyResponseModel(result));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    //    /**
    //     * Gets the resources of the faculty by name.
    //     *
    //     * @param facultyName the faculty name
    //     * @return the response
    //     */
    //    @PostMapping("/resources")
    //    public ResponseEntity<Resources> getFacultyResourcesByName(String facultyName) {
    //        try {
    //            return ResponseEntity.ok(rpManagementService.findResourcesByName(facultyName));
    //        } catch (Exception e) {
    //            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    //        }
    //    }

    /**
     * Endpoint for contributing a node.
     *
     * @param nodeInfo The information needed to add the resources of the node
     * @return 200 OK if the contribution is successful
     * @throws Exception if a faculty with the given id can't be found
     */
    @PostMapping("/contributeNode")
    public ResponseEntity<NodeInteractionResponseModel> contributeNode(@RequestBody NodeInteractionRequestModel nodeInfo)
        throws Exception {
        try {
            return ResponseEntity.ok(new NodeInteractionResponseModel(rpManagementService.contributeNode(nodeInfo)));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Endpoint for deleting a node.
     *
     * @param nodeInfo The information needed to delete the resources of the node
     * @return 200 OK if the deletion is successful
     * @throws Exception if a faculty with the given id can't be found or doesn't have enough resources
     */
    @PostMapping("/deleteNode")
    public ResponseEntity<NodeInteractionResponseModel> deleteNode(@RequestBody NodeInteractionRequestModel nodeInfo)
        throws Exception {
        try {
            return ResponseEntity.ok(new NodeInteractionResponseModel(rpManagementService.deleteNode(nodeInfo)));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Returns a string-representation of all the resource pools in the database.
     *
     * @return ResponseEntity containing a String with all the resource pools in the database
     */
    @GetMapping("/printDatabase")
    public ResponseEntity<String> printDatabase() {
        return ResponseEntity.ok(rpManagementService.printDatabase());
    }

    /**
     * Gets the facultyName given the id.
     *
     * @param facultyId the facultyId
     * @return the matching facultyName
     */
    @PostMapping("/getFacultyName")
    public ResponseEntity<String> getFacultyName(@RequestBody long facultyId) {
        Optional<ResourcePool> optional = rpManagementService.findById(facultyId);
        return optional.map(resourcePool -> ResponseEntity.ok(resourcePool.getName()))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Gets the facultyId given the name.
     *
     * @param facultyName the facultyName
     * @return the matching facultyId
     */
    @PostMapping("/getFacultyId")
    public ResponseEntity<Long> getFacultyName(@RequestBody String facultyName) {
        Optional<ResourcePool> optional = rpManagementService.findByName(facultyName);
        return optional.map(resourcePool -> ResponseEntity.ok(resourcePool.getId()))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
