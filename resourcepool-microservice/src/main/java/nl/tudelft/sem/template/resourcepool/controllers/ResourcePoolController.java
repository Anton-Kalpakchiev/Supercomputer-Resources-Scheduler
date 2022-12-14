package nl.tudelft.sem.template.resourcepool.controllers;

import nl.tudelft.sem.template.resourcepool.authentication.AuthManager;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.RpManagementService;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import nl.tudelft.sem.template.resourcepool.models.DistributionModel;
import nl.tudelft.sem.template.resourcepool.models.FacultyCreationModel;
import nl.tudelft.sem.template.resourcepool.models.ResourceByNameModel;
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
    public ResponseEntity createFaculty(@RequestBody FacultyCreationModel request) throws Exception {
        try {
            rpManagementService.createFaculty(request.getName(), request.getManagerNetId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resources")
    public ResponseEntity<Resources> getFacultyResourcesByName(@RequestBody ResourceByNameModel request) {
        String facultyName = request.getFacultyName();
        Resources availableResources;
        try {
            availableResources = rpManagementService.findResourcesByName(facultyName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
//        String result = availableResources.getMemory() + "-" + availableResources.getCpu() + "-" + availableResources.getGpu();
        return ResponseEntity.ok(availableResources);
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
