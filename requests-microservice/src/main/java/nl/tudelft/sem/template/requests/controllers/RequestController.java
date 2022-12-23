package nl.tudelft.sem.template.requests.controllers;

import static nl.tudelft.sem.template.requests.authentication.JwtRequestFilter.AUTHORIZATION_HEADER;

import java.util.Calendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.requests.authentication.AuthManager;
import nl.tudelft.sem.template.requests.domain.AppRequest;
import nl.tudelft.sem.template.requests.domain.InvalidResourcesException;
import nl.tudelft.sem.template.requests.domain.RegistrationService;
import nl.tudelft.sem.template.requests.domain.ResourcePoolService;
import nl.tudelft.sem.template.requests.domain.Resources;
import nl.tudelft.sem.template.requests.domain.StatusService;
import nl.tudelft.sem.template.requests.domain.UserService;
import nl.tudelft.sem.template.requests.models.ManualApprovalModel;
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
 * This controller shows how you can extract information from the request controller.
 */
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
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
     * @param request   The request model.
     * @param requested To get the token
     * @return The response entity status.
     * @throws Exception When requests are made with insufficient gpu compared to cpu.
     */
    @PostMapping("/register")
    public ResponseEntity<Long> register(@RequestBody RegistrationRequestModel request, HttpServletRequest requested)
            throws InvalidResourcesException {
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
            deadline.set(Calendar.MONTH, Integer.parseInt(deadlineStr.split("-")[1]) - 1);
            deadline.set(Calendar.DAY_OF_MONTH, Integer.parseInt(deadlineStr.split("-")[0]));

            try {
                long requestId = registrationService.registerRequest(description, resources, owner,
                        facultyName, availableResources, deadline, availableFreePoolResources, token).getId();
                return ResponseEntity.ok(requestId);
            } catch (InvalidResourcesException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }


    /**
     * Gets the status of a request.
     *
     * @return the status of the request found in the database with the given id
     */
    @PostMapping("/getStatus")
    public ResponseEntity<Integer> getStatus(@RequestBody long id) {
        return ResponseEntity.ok(statusService.getStatus(id));
    }

    /**
     * Sets the status of a request.
     *
     * @return whether the status was successfully set
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

    /**
     * Gets the pending requests for that faculty.
     *
     * @param requested for the jwtToken.
     * @return the list of requests
     */
    @GetMapping("/pendingRequests")
    public ResponseEntity<List<AppRequest>> getPendingRequests(HttpServletRequest requested) {
        String authorizationHeader = requested.getHeader(AUTHORIZATION_HEADER);
        String token = authorizationHeader.split(" ")[1];
        Long facultyId = userService.getFacultyIdForManager(token);
        String facultyName = resourcePoolService.getFacultyNameForFacultyId(facultyId, token);
        return ResponseEntity.ok(registrationService.getPendingRequestsForFacultyName(facultyName));
    }

    /**
     * The faculty manager manually accepts or rejects a request left for manual approval/rejection.
     *
     * @param model the manualApprovalModel that contains the requestID,
     *              whether it is approved or rejected, and the scheduled day of execution
     * @param requested to get the token
     * @return whether the request is properly accepted/rejected
     * @throws ResponseStatusException if request does not exist
     */
    @PostMapping("/manualSchedule")
    public ResponseEntity<Boolean> approveRejectRequest(@RequestBody ManualApprovalModel model, HttpServletRequest requested)
            throws ResponseStatusException {
        //TODO could check if the facultyManager is the manager of the faculty that the request is sent to
        long id = model.getRequestId();
        boolean approved = model.isApproved();
        Calendar dayOfExecution;
        if (model.getDayOfExecution().contains("/")) {
            String[] dayOfExecutionArr = model.getDayOfExecution().split("/");
            dayOfExecution = Calendar.getInstance();
            dayOfExecution.set(Calendar.YEAR, Integer.parseInt(dayOfExecutionArr[2]));
            dayOfExecution.set(Calendar.MONTH, Integer.parseInt(dayOfExecutionArr[1]));
            dayOfExecution.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayOfExecutionArr[0]));
        } else {
            dayOfExecution = Calendar.getInstance();
        }
        String token = requested.getHeader(AUTHORIZATION_HEADER).split(" ")[1];

        try {
            if (approved) {
                statusService.setStatus(id, 1);
                resourcePoolService.approval(dayOfExecution, id, false, token);
            } else {
                statusService.setStatus(id, 2);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity.ok(true);
    }

    /**
     * Gets the set of IDs of all resource requests made by a given user.
     *
     * @param netId the netID of the given user
     * @return the set of IDs encoded in a String
     */
    @PostMapping("/getRequestIds")
    public ResponseEntity<String> getRequestIdsByNetId(@RequestBody String netId) {
        return ResponseEntity.ok(registrationService.getRequestIdsByNetId(netId));
    }



    /**
     * Gets the requested resources given the requestId.
     *
     * @param requestId the id of the request
     * @return the resources that this request requests
     */
    @PostMapping("/resourcesById")
    public ResponseEntity<Resources> getResourcesById(@RequestBody long requestId) {
        return ResponseEntity.ok(registrationService.getResourcesForId(requestId));
    }
}
