package nl.tudelft.sem.template.users.controllers;

import nl.tudelft.sem.template.users.authentication.AuthManager;
import nl.tudelft.sem.template.users.domain.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * Constructor for the controller.
     *
     * @param authManager injected authentication manager.
     * @param registrationService injected registration service.
     */
    @Autowired
    public DefaultController(AuthManager authManager, RegistrationService registrationService) {
        this.authManager = authManager;
        this.registrationService = registrationService;
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

    @GetMapping("/newUser")
    public ResponseEntity<String> newUserCreated() {
        registrationService.registerUser(authManager.getNetId());
        return ResponseEntity.ok("User " + authManager.getNetId() + " was added as a User.");
    }

}
