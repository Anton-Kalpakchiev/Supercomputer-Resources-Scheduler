package nl.tudelft.sem.template.requests.controllers;

import static nl.tudelft.sem.template.requests.authentication.JwtRequestFilter.AUTHORIZATION_HEADER;

import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.requests.authentication.AuthManager;
import nl.tudelft.sem.template.requests.domain.RegistrationService;
import nl.tudelft.sem.template.requests.domain.ResourcePoolService;
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
    private final transient ResourcePoolService resourcePoolService;
    private final transient UserService userService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager         Spring Security component used to authenticate and authorize the user
     * @param registrationService The service that will allow requests to be saved to the database
     */
    @Autowired
    public RequestController(AuthManager authManager, RegistrationService registrationService,
                             StatusService statusService, ResourcePoolService resourcePoolService,
                             UserService userService) {
        this.authManager = authManager;
        this.registrationService = registrationService;
        this.statusService = statusService;
        this.resourcePoolService = resourcePoolService;
        this.userService = userService;
    }

    /**
     * The registration process for a request.
     *
     * @param request The request model.
     * @param requested To get the token
     * @return The response entity status.
     * @throws Exception When requests are made with insufficient gpu compared to cpu.
     */
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegistrationRequestModel request, HttpServletRequest requested)
             {
        try {
            String authorizationHeader = requested.getHeader(AUTHORIZATION_HEADER);
            String token = authorizationHeader.split(" ")[1];
            final String description = request.getDescription();
            final Resources resources = new Resources(request.getCpu(), request.getGpu(), request.getMemory());
            final String owner = authManager.getNetId();
            final String facultyName = request.getFacultyName();
            final long facultyId = resourcePoolService.getIdByName(facultyName, token);
            final Resources availableResources = resourcePoolService.getFacultyResourcesById(facultyId, token);
            final Resources availableFreePoolResources = resourcePoolService.getFacultyResourcesById(1L, token);

            String deadlineStr = request.getDeadline(); //convert to Calendar immediately
            Calendar deadline = Calendar.getInstance();
            deadline.set(Calendar.YEAR, Integer.parseInt(deadlineStr.split("-")[2]));
            deadline.set(Calendar.MONTH, Integer.parseInt(deadlineStr.split("-")[1])-1);
            deadline.set(Calendar.DAY_OF_MONTH, Integer.parseInt(deadlineStr.split("-")[0]));

            registrationService.registerRequest(description, resources, owner,
                    facultyName, availableResources, deadline, availableFreePoolResources, token);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity.ok().build();
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

    @GetMapping("/pending-requests")
    public ResponseEntity<List<AppRequest>> getPendingRequests(HttpServletRequest requested) {
        String authorizationHeader = requested.getHeader(AUTHORIZATION_HEADER);
        String token = authorizationHeader.split(" ")[1];
        Long facultyId = userService.getFacultyIdForManager(token);
        String facultyName = resou  rcePoolService.getFacultyNameForFacultyId(facultyId, token);
        return ResponseEntity.ok(registrationService.getPendingRequestsForFacultyName(facultyName));
    }

    @PostMapping("/resourcesById")
    public ResponseEntity<Resources> getResourcesById(@RequestBody long requestId){
        System.out.println(requestId);
        return ResponseEntity.ok(registrationService.getResourcesForId(requestId));
    }
}
