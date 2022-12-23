package nl.tudelft.sem.template.users.facade;

import lombok.AllArgsConstructor;
import nl.tudelft.sem.template.users.authentication.AuthManager;
import nl.tudelft.sem.template.users.authentication.JwtRequestFilter;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.EmployeeService;
import nl.tudelft.sem.template.users.domain.FacultyAccountService;
import nl.tudelft.sem.template.users.domain.FacultyException;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
import nl.tudelft.sem.template.users.domain.PromotionAndEmploymentService;
import nl.tudelft.sem.template.users.domain.RegistrationService;
import nl.tudelft.sem.template.users.models.FacultyCreationRequestModel;
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
     * Request for creating a new faculty.
     *
     * @param request the faculty creation request
     * @return whether the request was successful.
     */
    @PostMapping("/createFaculty")
    public ResponseEntity<String> createFaculty(@RequestBody FacultyCreationRequestModel request) {
        String authorNetId = authentication.getNetId();
        String managerNetId = request.getManagerNetId();
        String facultyName = request.getName();
        String token = JwtRequestFilter.token;
        System.out.println(token);
        try {
            long facId = requestSenderService.createFaculty(authorNetId, managerNetId, facultyName, token);
            System.out.println("Faculty \"" + facultyName + "\" with id " + facId + " was created. "
                    + "Managed by: (" + managerNetId + ").");
            return ResponseEntity.ok("Faculty \"" + facultyName
                    + "\", managed by (" + managerNetId + "), was created.");
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (FacultyException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NoSuchUserException e) {
            throw new RuntimeException(e);
        }
    }
}
