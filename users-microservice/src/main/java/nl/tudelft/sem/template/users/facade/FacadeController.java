package nl.tudelft.sem.template.users.facade;

import java.util.Calendar;
import lombok.AllArgsConstructor;
import nl.tudelft.sem.template.users.authentication.AuthManager;
import nl.tudelft.sem.template.users.authentication.JwtRequestFilter;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.FacultyException;
import nl.tudelft.sem.template.users.domain.InnerRequestFailedException;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
import nl.tudelft.sem.template.users.models.FacultyCreationRequestModel;
import nl.tudelft.sem.template.users.models.ResourcesDto;
import nl.tudelft.sem.template.users.models.facade.DistributionModel;
import nl.tudelft.sem.template.users.models.facade.ManualApprovalModel;
import nl.tudelft.sem.template.users.models.facade.NodeContributionRequestModel;
import nl.tudelft.sem.template.users.models.facade.NodeDeletionRequestModel;
import nl.tudelft.sem.template.users.models.facade.ReleaseResourcesRequestModel;
import nl.tudelft.sem.template.users.models.facade.RequestStatusModel;
import nl.tudelft.sem.template.users.models.facade.RequestTomorrowResourcesRequestModel;
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
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
@RestController
@AllArgsConstructor
public class FacadeController {
    private final transient AuthManager authentication;
    private final transient RequestSenderService requestSenderService;
    private final transient NodesRequestService nodesRequestService;
    private final transient ResourcePoolRequestService resourcePoolRequestService;
    private final transient RequestsRequestService requestsRequestService;
    private final transient SchedulingRequestsService schedulingRequestsService;

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
            resourcePoolRequestService.addDistributionRequest(url, authentication.getNetId(), JwtRequestFilter.token,
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
            requestSenderService.postRequestFromSysadmin(
                    url, authentication.getNetId(), JwtRequestFilter.token);
            return ResponseEntity.ok("Distribution was cleared.");
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getCause().toString());
        }
    }

    /**
<<<<<<< HEAD
     * Allows the user to view the schedules they are authorized to view.
     * SYSADMINS - all schedules for all available days per faculty.
     * Faculty Managers - all schedules for all available days of their faculty.
     * Employees cannot view the schedules of any faculties.
     *
     * @return the schedules of the authorized faculties.
     */
    @GetMapping("/schedules/viewSchedules")
    public ResponseEntity<String> viewSchedule() {
        try {
            String response = schedulingRequestsService.getScheduleRequestRouter(
                    authentication.getNetId(), JwtRequestFilter.token);
            return ResponseEntity.ok(response);
        } catch (InnerRequestFailedException | NoSuchUserException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getCause().toString());
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    /**
     * Post request from a faculty manager to manually approve/reject a request.
     *
     * @param approvalModel contains the requestID, whether it is accepted or rejected,
     *                      and if accepted, its day of execution
     * @return a message to the user informing them the request is successfully approved/rejected
     */
    @PostMapping("/request/manualSchedule")
    public ResponseEntity<String> approveRejectRequest(@RequestBody ManualApprovalModel approvalModel) {
        try {
            boolean approved = approvalModel.isApproved();
            long requestId = approvalModel.getRequestId();
            //if request is getting rejected, this field does not matter
            Calendar dayOfExecution = Calendar.getInstance();
            if (approvalModel.getDayOfExecution().contains("/")) {
                String[] dayOfExecutionArr = approvalModel.getDayOfExecution().split("/");
                dayOfExecution = Calendar.getInstance();
                dayOfExecution.set(Calendar.YEAR, Integer.parseInt(dayOfExecutionArr[2]));
                dayOfExecution.set(Calendar.MONTH, Integer.parseInt(dayOfExecutionArr[1]));
                dayOfExecution.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayOfExecutionArr[0]));
            }

            String url = "http://localhost:8084/manualSchedule";
            requestsRequestService.approveRejectRequest(url, authentication.getNetId(),
                    approvalModel, JwtRequestFilter.token);
            String answer = requestsRequestService.getRequestAnswer(approved);
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getCause().toString());
        }
    }

    /**
     * Gets the status of a request.
     *
     * @return the status of the request found in the database with the given id
     */
    @GetMapping("/request/status")
    public ResponseEntity<String> getStatus(@RequestBody RequestStatusModel idModel) {
        try {
            String url = "http://localhost:8084/getStatus";
            long requestId = idModel.getRequestId();
            String answer = requestsRequestService.getStatusOfRequest(
                    url, authentication.getNetId(), requestId, JwtRequestFilter.token);
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getCause().toString());
        }
    }

    /**
     * Endpoint for Post API call that registers a resource request.
     *
     * @param request the model of the request containing all the important information
     * @return message to the user that tells them whether their request was successfully submitted
     */
    @PostMapping("/request/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequestModel request) {
        try {
            String url = "http://localhost:8084/register";
            long requestId = requestsRequestService.registerRequest(
                    url, authentication.getNetId(), request, JwtRequestFilter.token);
            String answer = requestsRequestService.registerRequestMessage(requestId);
            return ResponseEntity.ok(answer);
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
            long nodeId = nodesRequestService.contributeNodeRequest(
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
            String nodeName = nodesRequestService.deleteNodeRequest(
                    url, authentication.getNetId(), JwtRequestFilter.token, nodeId.getNodeId());
            return ResponseEntity.ok("The node with the name \"" + nodeName + "\" has been successfully deleted.");
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getCause().toString());
        }
    }

    /**
     * Release resources for a particular faculty on a particular day.
     *
     * @param request the request body containing the faculty id and day
     * @return a string which indicates whether the request was successful
     */
    @PostMapping("/releaseResources")
    public ResponseEntity<String> releaseResources(@RequestBody ReleaseResourcesRequestModel request) {
        try {
            String url = "http://localhost:8085/releaseResources";
            String facultyName = schedulingRequestsService.releaseResourcesRequest(
                    url, authentication.getNetId(), JwtRequestFilter.token, request);
            return ResponseEntity.ok("The resources for " + facultyName
                    + " have successfully been released to the free resource pool of that day");
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Request the available resources for the next day for a particular faculty.
     *
     * @param request the request body containing the facultyId
     * @return a String of the available resources for tomorrow.
     */
    @PostMapping("/availableResourcesForTomorrow")
    public ResponseEntity<String> getResourcesForTomorrow(@RequestBody RequestTomorrowResourcesRequestModel request) {
        try {
            String url = "http://localhost:8085/availableFacultyResources";

            ResourcesDto resourcesTomorrow = schedulingRequestsService.getResourcesTomorrow(
                    url, authentication.getNetId(), JwtRequestFilter.token, request.getResourcePoolId());
            return ResponseEntity.ok("The resources for tomorrow for resource pool id " + request.getResourcePoolId()
                    + " are: <CPU: "
                    + resourcesTomorrow.getCpu() + ", GPU: "
                    + resourcesTomorrow.getGpu() + ", Memory: "
                    + resourcesTomorrow.getMemory() + ">");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Returns a string with the pending requests for that faculty.
     * Only accessible for a Faculty account.
     *
     * @return ResponseEntity containing a String with the pending request for that faculty
     */
    @GetMapping("/pendingRequests")
    public ResponseEntity<String> getPendingRequests() {
        try {
            String url = "http://localhost:8084/pendingRequests";
            String result = requestSenderService.getRequestFromFacultyAccount(url,
                    authentication.getNetId(), JwtRequestFilter.token);
            return ResponseEntity.ok(result);
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
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
        try {
            long facId = resourcePoolRequestService.createFaculty(authorNetId, managerNetId, facultyName, token);
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
