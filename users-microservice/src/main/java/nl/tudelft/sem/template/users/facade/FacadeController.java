package nl.tudelft.sem.template.users.facade;

import lombok.AllArgsConstructor;
import nl.tudelft.sem.template.users.authentication.AuthManager;
import nl.tudelft.sem.template.users.authentication.JwtRequestFilter;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.EmployeeService;
import nl.tudelft.sem.template.users.domain.FacultyAccountService;
import nl.tudelft.sem.template.users.domain.PromotionAndEmploymentService;
import nl.tudelft.sem.template.users.domain.RegistrationService;
import nl.tudelft.sem.template.users.models.facade.DistributionModel;
import nl.tudelft.sem.template.users.models.facade.NodeContributionRequestModel;
import nl.tudelft.sem.template.users.models.facade.NodeDeletionRequestModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for the requests towards other microservices.
 */
@RestController
@AllArgsConstructor
public class FacadeController {
    private final transient AuthManager authentication;
    private final transient AuthorizationManager authorization;
    private final transient PromotionAndEmploymentService promotionAndEmploymentService;
    private final transient RegistrationService registrationService;
    private final transient EmployeeService employeeService;
    private final transient FacultyAccountService facultyAccountService;
    private final transient RequestSenderService requestSenderService;

    /**
     * Returns a string with the current distribution of the resources in the system.
     * Only accessible for a SYSADMIN.
     *
     * @return ResponseEntity containing a String with the current distribution of the resources in the system
     */
    @GetMapping("/distribution/current")
    public ResponseEntity<String> getCurrentDistribution() {
        try {
            String url = "http://localhost:8085/distribution/current";
            String response = requestSenderService
                    .getRequestFromSysadmin(url, authentication.getNetId(), JwtRequestFilter.token);
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Adds a distribution for a faculty to the queue.
     * Only accessible for a SYSADMIN.
     *
     * @param distribution the wanted percentage of resources for a faculty
     * @return 200 OK if the adding is successful
     */
    @PostMapping("/distribution/add")
    public ResponseEntity<String> addDistribution(@RequestBody DistributionModel distribution) {
        try {
            String url = "http://localhost:8085/distribution/add";
            requestSenderService.addDistributionRequest(url, authentication.getNetId(), JwtRequestFilter.token,
                    distribution);
            return ResponseEntity.ok("Distribution was added.");
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Returns a string with the current distributions in the queue.
     * Only accessible for a SYSADMIN.
     *
     * @return ResponseEntity containing a String with the current distributions in the queue
     */
    @GetMapping("/distribution/status")
    public ResponseEntity<String> statusDistribution() {
        try {
            String url = "http://localhost:8085/distribution/status";
            String result = requestSenderService.getRequestFromSysadmin(url,
                    authentication.getNetId(), JwtRequestFilter.token);
            return ResponseEntity.ok(result);
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Saves all the current faculty distributions in the queue to the full system.
     * Only accessible for a SYSADMIN
     *
     * @return 200 OK if the saving is successful
     */
    @PostMapping("/distribution/save")
    public ResponseEntity<String> saveDistribution() {
        try {
            String url = "http://localhost:8085/distribution/save";
            requestSenderService.postRequestFromSysadmin(url, authentication.getNetId(), JwtRequestFilter.token);
            return ResponseEntity.ok("Distribution was saved.");
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getCause().toString());
        }
    }

    /**
     * Clears the queue with all the current faculty distributions.
     * Only accessible for a SYSADMIN.
     *
     * @return 200 OK if the clearing is successful
     */
    @PostMapping("/distribution/clear")
    public ResponseEntity<String> clearDistribution() {
        try {
            String url = "http://localhost:8085/distribution/clear";
            requestSenderService.postRequestFromSysadmin(url, authentication.getNetId(), JwtRequestFilter.token);
            return ResponseEntity.ok("Distribution was cleared.");
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getCause().toString());
        }
    }

    /**
     * Contributes a node to a faculty.
     * Only accessible for an EMPLOYEE.
     *
     * @return 200 OK if the contribution was successful
     */
    @PostMapping("/contributeNode")
    public ResponseEntity<String> contributeNode(@RequestBody NodeContributionRequestModel nodeInfo) {
        try {
            String url = "http://localhost:8083/contributeNode";
            long nodeId = requestSenderService.contributeNodeRequest(
                    url, authentication.getNetId(), JwtRequestFilter.token, nodeInfo);
            return ResponseEntity.ok("The node with the name \"" + nodeInfo.getName()
                                    + "\" has been contributed. The ID of the node is: " + nodeId + '.');
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getCause().toString());
        }
    }

    /**
     * Deletes a node from a faculty.
     * Only accessible for an EMPLOYEE.
     *
     * @return 200 OK if the deletion was successful
     */
    @PostMapping("/deleteNode")
    public ResponseEntity<String> deleteNode(@RequestBody NodeDeletionRequestModel nodeId) {
        try {
            String url = "http://localhost:8083/deleteNode";
            String nodeName = requestSenderService.deleteNodeRequest(
                    url, authentication.getNetId(), JwtRequestFilter.token, nodeId.getNodeId());
            return ResponseEntity.ok("The node with the name \"" + nodeName + "\" has been successfully deleted.");
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getCause().toString());
        }
    }
}
