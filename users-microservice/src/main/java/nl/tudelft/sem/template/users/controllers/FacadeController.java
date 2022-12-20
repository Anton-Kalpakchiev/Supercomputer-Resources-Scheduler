package nl.tudelft.sem.template.users.controllers;

import lombok.AllArgsConstructor;
import nl.tudelft.sem.template.users.authentication.AuthManager;
import nl.tudelft.sem.template.users.authentication.JwtRequestFilter;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.EmployeeService;
import nl.tudelft.sem.template.users.domain.FacultyAccountService;
import nl.tudelft.sem.template.users.domain.PromotionAndEmploymentService;
import nl.tudelft.sem.template.users.domain.RegistrationService;
import nl.tudelft.sem.template.users.domain.RequestSenderService;
import nl.tudelft.sem.template.users.models.facade.DistributionModel;
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
            String response = requestSenderService
                    .getCurrentDistributionRequest(authentication.getNetId(), JwtRequestFilter.token);
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
            requestSenderService.addDistributionRequest(authentication.getNetId(), JwtRequestFilter.token,
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
            String result = requestSenderService.statusDistributionRequest(
                    authentication.getNetId(), JwtRequestFilter.token);
            return ResponseEntity.ok(result);
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
