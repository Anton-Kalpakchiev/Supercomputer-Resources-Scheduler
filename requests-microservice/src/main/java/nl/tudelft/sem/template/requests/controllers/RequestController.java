package nl.tudelft.sem.template.requests.controllers;

import static nl.tudelft.sem.template.requests.authentication.JwtRequestFilter.AUTHORIZATION_HEADER;

import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.requests.authentication.AuthManager;
import nl.tudelft.sem.template.requests.domain.RegistrationService;
import nl.tudelft.sem.template.requests.domain.ResourcePoolService;
import nl.tudelft.sem.template.requests.domain.Resources;
import nl.tudelft.sem.template.requests.models.RegistrationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Hello World requests controller.
 * <p>
 * This controller shows how you can extract information from the JWT token.
 * </p>
 */
@RestController
public class RequestController {

    private final transient AuthManager authManager;
    private final transient RegistrationService registrationService;
    private final transient ResourcePoolService resourcePoolService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager         Spring Security component used to authenticate and authorize the user
     * @param registrationService The service that will allow requests to be saved to the database
     */
    @Autowired
    public RequestController(AuthManager authManager, RegistrationService registrationService,
                             ResourcePoolService resourcePoolService) {
        this.authManager = authManager;
        this.registrationService = registrationService;
        this.resourcePoolService = resourcePoolService;
    }

    /**
     * The registration process for a request.
     *
     * @param request The request model.
     * @return The response entity status.
     * @throws Exception When requests are made with insufficient gpu compared to cpu.
     */
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegistrationRequestModel request, HttpServletRequest requested)
            throws Exception {

        try {

            String facultyName = request.getFacultyName();

            String authorizationHeader = requested.getHeader(AUTHORIZATION_HEADER);
            String token = authorizationHeader.split(" ")[1];

            String deadlineStr = request.getDeadline();
            String[] deadlineArr = deadlineStr.split("-"); //convert to Calendar immediately
            Calendar deadline = Calendar.getInstance();
            deadline.set(Calendar.YEAR, Integer.parseInt(deadlineArr[2]));
            deadline.set(Calendar.MONTH, Integer.parseInt(deadlineArr[1]) - 1);
            deadline.set(Calendar.DAY_OF_MONTH, Integer.parseInt(deadlineArr[0]));

            Resources availableResources = resourcePoolService.getFacultyResourcesByName(facultyName, token);
            Resources availableResourcesFrp = resourcePoolService.getFacultyResourcesByName("Free pool", token);
            Resources resources = new Resources(request.getCpu(), request.getGpu(), request.getMemory());
            registrationService.registerRequest(request.getDescription(), resources,
                    authManager.getNetId(), facultyName, availableResources, deadline, availableResourcesFrp, token);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    /**
     * Gets requests by id.
     *
     * @return the requests found in the database with the given id
     */
    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello " + authManager.getNetId());
    }


}
