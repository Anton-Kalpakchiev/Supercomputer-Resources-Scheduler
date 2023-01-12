package nl.tudelft.sem.template.resourcepool.controllers;

import nl.tudelft.sem.template.resourcepool.authentication.AuthManager;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.distribution.DistributionService;
import nl.tudelft.sem.template.resourcepool.models.DistributionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for endpoints related to the distribution.
 */
@RestController
public class DistributionController {

    private final transient AuthManager authManager;

    private final transient DistributionService distributionService;

    /**
     * Instantiates a new DistributionController.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     * @param distributionService The service which will handle the business logic for distribution
     */
    @Autowired
    public DistributionController(AuthManager authManager, DistributionService distributionService) {
        this.authManager = authManager;
        this.distributionService = distributionService;
    }

    /**
     * Returns a string with the current distribution of the resources in the system.
     *
     * @return ResponseEntity containing a String with the current distribution of the resources in the system
     */
    @GetMapping("/distribution/current")
    public ResponseEntity<String> getCurrentDistribution() {
        return ResponseEntity.ok("This is the current distribution of the resources: \n"
                + distributionService.getCurrentDistribution());
    }

    /**
     * Adds a distribution for a faculty to the queue.
     *
     * @param distribution the wanted percentage of resources for a faculty
     * @return 200 OK if the adding is successful
     * @throws Exception if the faculty name is invalid
     */
    @PostMapping("/distribution/add")
    public ResponseEntity addDistribution(@RequestBody DistributionModel distribution) throws Exception {
        try {
            distributionService.addDistribution(distribution);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok("Distribution for " + distribution.getName() + " successfully added");
    }

    /**
     * Returns a string with the current distributions in the queue.
     *
     * @return ResponseEntity containing a String with the current distributions in the queue
     */
    @GetMapping("/distribution/status")
    public ResponseEntity<String> statusDistribution() {
        return ResponseEntity.ok("These are the currently submitted distributions: \n"
                + distributionService.statusDistribution());
    }

    /**
     * Saves all the current faculty distributions in the queue to the full system.
     *
     * @return 200 OK if the saving is successful
     * @throws Exception if there is a wrong amount of distributions or if the percentages don't add up
     */
    @PostMapping("/distribution/save")
    public ResponseEntity<String> saveDistribution() throws Exception {
        try {
            distributionService.saveDistribution();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok("The proposed distribution was successfully saved!");
    }

    /**
     * Clears the queue with all the current faculty distributions.
     *
     * @return 200 OK if the clearing is successful
     * @throws Exception if the list couldn't be cleared
     */
    @PostMapping("/distribution/clear")
    public ResponseEntity clearDistribution() throws Exception {
        try {
            distributionService.clearDistribution();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok("The proposed distribution was successfully cleared.");
    }
}
