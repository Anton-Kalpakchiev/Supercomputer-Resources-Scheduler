package nl.tudelft.sem.template.users.controllers;

import nl.tudelft.sem.template.users.authentication.AuthManager;
import nl.tudelft.sem.template.users.domain.AuthorizationService;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
import nl.tudelft.sem.template.users.domain.RegistrationService;
import nl.tudelft.sem.template.users.domain.Sysadmin;
import nl.tudelft.sem.template.users.domain.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.User;
import nl.tudelft.sem.template.users.models.CheckAccessResponseModel;
import nl.tudelft.sem.template.users.models.PromotionRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Hello World users controller.
 * <p>
 * This controller shows how you can extract information from the JWT token.
 * </p>
 */
@RestController
public class DefaultController {

    private final transient AuthManager authManager;
    private final transient RegistrationService registrationService;
    private final transient AuthorizationService authorizationService;

    /**
     * Constructor for the controller.
     *
     * @param authManager injected authentication manager.
     * @param registrationService injected registration service.
     * @param authorizationService injected authorization service.
     */
    @Autowired
    public DefaultController(AuthManager authManager,
                             RegistrationService registrationService,
                             AuthorizationService authorizationService) {
        this.authManager = authManager;
        this.registrationService = registrationService;
        this.authorizationService = authorizationService;
    }


    /**
     * Gets users by id.
     *
     * @return the users found in the database with the given id
     */
    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello " + authManager.getNetId());

    }

    /**
     * Adds a new user as an admin if their netId is "admin", else
     * Adds the user as an Employee.
     *
     * @return whether the request was successful
     */
    @GetMapping("/newUser")
    public ResponseEntity<String> newUserCreated() {
        User added = registrationService.registerUser(authManager.getNetId());
        if (added.getClass().equals(Sysadmin.class)) {
            return ResponseEntity.ok("User " + authManager.getNetId() + " was added as a Sysadmin.");
        }
        return ResponseEntity.ok("User " + authManager.getNetId() + " was added as an Employee.");
    }

    /**
     * Promotes an Employee to a Sysadmin.
     *
     * @param request the promotion request body
     * @return whether the request was successful
     * @throws Exception if the promoter is unauthorized or such employee does not exist
     */
    @PostMapping("/promoteToSysadmin")
    public ResponseEntity<String> promoteEmployeeToSysadmin(@RequestBody PromotionRequestModel request)
            throws Exception {
        try {
            String toBePromoted = request.getNetId();
            String promoter = authManager.getNetId();
            authorizationService.promoteEmployeeToSysadmin(promoter, toBePromoted);
            return ResponseEntity.ok("User (" + toBePromoted + ") was promoted to a Sysadmin");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (e.getClass().equals(UnauthorizedException.class)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Request for checking the access of a User.
     *
     * @return whether the request was successful
     * @throws Exception if a user has multiple roles.
     */
    @GetMapping("/checkAccess")
    public ResponseEntity<CheckAccessResponseModel> checkUserAccess() throws Exception {
        try {
            String netId = authManager.getNetId();
            String result = authorizationService.checkAccess(netId);
            return ResponseEntity.ok(new CheckAccessResponseModel(result));
        } catch (NoSuchUserException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
