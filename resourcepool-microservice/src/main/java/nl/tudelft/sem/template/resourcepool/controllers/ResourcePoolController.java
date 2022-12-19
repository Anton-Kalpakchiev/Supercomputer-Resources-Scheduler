package nl.tudelft.sem.template.resourcepool.controllers;

import nl.tudelft.sem.template.resourcepool.authentication.AuthManager;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.Faculty;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.RpManagementService;
import nl.tudelft.sem.template.resourcepool.models.DistributionModel;
import nl.tudelft.sem.template.resourcepool.models.FacultyCreationModel;
import nl.tudelft.sem.template.resourcepool.models.FacultyCreationResponseModel;
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
     * @param authManager Spring Security component used to authenticate and authorize the user
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
    public ResponseEntity<FacultyCreationResponseModel> createFaculty(@RequestBody FacultyCreationModel request)
            throws Exception {
        try {
            Faculty newFaculty = rpManagementService.createFaculty(request.getName(), request.getManagerNetId());
            return ResponseEntity.ok(new FacultyCreationResponseModel(newFaculty.getId()));
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
}
