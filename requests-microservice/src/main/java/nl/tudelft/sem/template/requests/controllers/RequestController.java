package nl.tudelft.sem.template.requests.controllers;

import java.io.IOException;
import java.util.Calendar;
import nl.tudelft.sem.template.requests.authentication.AuthManager;
import nl.tudelft.sem.template.requests.domain.RegistrationService;
import nl.tudelft.sem.template.requests.domain.Resources;
import nl.tudelft.sem.template.requests.domain.StatusService;
import nl.tudelft.sem.template.requests.models.RegistrationRequestModel;
import nl.tudelft.sem.template.requests.models.SetStatusModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
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
    private final transient StatusService statusService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager         Spring Security component used to authenticate and authorize the user
     * @param registrationService The service that will allow requests to be saved to the database
     */
    @Autowired
    public RequestController(AuthManager authManager, RegistrationService registrationService,
                             StatusService statusService) {
        this.authManager = authManager;
        this.registrationService = registrationService;
        this.statusService = statusService;
    }

    /**
     * The registration process for a request.
     *
     * @param request The request model.
     * @return The response entity status.
     * @throws Exception When requests are made with insufficient gpu compared to cpu.
     */
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegistrationRequestModel request) throws Exception {

        try {
            final String description = request.getDescription();
            final Resources resources = new Resources(request.getMem(), request.getCpu(), request.getGpu());
            final String owner = authManager.getNetId();
            final String facultyName = request.getFacultyName();
            final Resources availableResources = getFacultyResourcesByName(facultyName);
            final Resources availableFreePoolResources = getFacultyResourcesByName("Free pool");

            String deadlineStr = request.getDeadline(); //convert to Calendar immediately
            Calendar deadline = Calendar.getInstance();
            deadline.set(Calendar.YEAR, Integer.parseInt(deadlineStr.split("-")[2]));
            deadline.set(Calendar.MONTH, Integer.parseInt(deadlineStr.split("-")[1]));
            deadline.set(Calendar.DAY_OF_MONTH, Integer.parseInt(deadlineStr.split("-")[0]));

            registrationService.registerRequest(description, resources, owner,
                    facultyName, availableResources, deadline, availableFreePoolResources);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    /**
     * Requests the available resources from the RP MS.
     *
     * @param facultyName name of the faculty.
     *
     * @return the available resources
     *
     * @throws IOException when post for object fails
     */
    public Resources getFacultyResourcesByName(String facultyName) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String request = facultyName;
        Resources availableResources = restTemplate.postForObject("http://localhost:8085/resources", request, Resources.class);
        return availableResources;
    }

    /**
     * Gets the status of a request.
     *
     * @return the status of the request found in the database with the given id
     */
    @GetMapping("/status")
    public ResponseEntity<Integer> getStatus(@RequestBody long id) {
        return ResponseEntity.ok(statusService.getStatus(id));
    }

    /**
     * Gets the status of a request.
     *
     * @return the status of the request found in the database with the given id
     */
    @PostMapping("/status")
    public ResponseEntity setStatus(@RequestBody SetStatusModel setStatusModel) throws ResponseStatusException {
        long id = setStatusModel.getId();
        int status = setStatusModel.getStatus();

        try {
            statusService.setStatus(id, status);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity.ok().build();
    }


}
