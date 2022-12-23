package nl.tudelft.sem.template.users.facade;

import java.util.*;

import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.AccountType;
import nl.tudelft.sem.template.users.domain.EmployeeRepository;
import nl.tudelft.sem.template.users.domain.EmployeeService;
import nl.tudelft.sem.template.users.domain.FacultyAccountRepository;
import nl.tudelft.sem.template.users.domain.FacultyAccountService;
import nl.tudelft.sem.template.users.domain.InnerRequestFailedException;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
import nl.tudelft.sem.template.users.domain.RegistrationService;
import nl.tudelft.sem.template.users.domain.SysadminRepository;
import nl.tudelft.sem.template.users.models.facade.DistributionModel;
import nl.tudelft.sem.template.users.models.facade.ManualApprovalModel;
import nl.tudelft.sem.template.users.models.facade.NodeContributionRequestModel;
import nl.tudelft.sem.template.users.models.facade.ScheduleRequestModel;
import nl.tudelft.sem.template.users.models.facade.ScheduleResponseModel;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * A DDD for sending requests to other microservice.
 */
@Service
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class RequestSenderService {
    private final transient FacultyAccountService facultyAccountService;
    private final transient SysadminRepository sysadminRepository;
    private final transient EmployeeRepository employeeRepository;
    private final transient FacultyAccountRepository facultyAccountRepository;
    private final transient EmployeeService employeeService;

    private final transient RegistrationService registrationService;
    private final transient AuthorizationManager authorization;

    private final transient RestTemplate restTemplate;

    /**
     * Constructor for the request sender service.
     *
     * @param sysadminRepository       the injected sysadmin repository
     * @param employeeRepository       the injected employee repository.
     * @param facultyAccountRepository the injected faculty account repo.
     * @param registrationService the injected registration service
     * @param authorization the authorization manager
     * @param restTemplate the provided rest template.
     * @param employeeService the provided employee service
     */
    public RequestSenderService(SysadminRepository sysadminRepository, EmployeeRepository employeeRepository,
                                FacultyAccountRepository facultyAccountRepository,
                                RegistrationService registrationService,
                                AuthorizationManager authorization,
                                RestTemplate restTemplate,
                                FacultyAccountService facultyAccountService, EmployeeService employeeService) {
        this.sysadminRepository = sysadminRepository;
        this.employeeRepository = employeeRepository;
        this.facultyAccountRepository = facultyAccountRepository;
        this.registrationService = registrationService;
        this.authorization = authorization;
        this.restTemplate = restTemplate;
        this.facultyAccountService = facultyAccountService;
        this.employeeService = employeeService;
    }

    /**
     * Sends a request to the specified url in the Resource pool microservice.
     *
     * @param url         the url of the request
     * @param authorNetId the netId of the author of the request.
     * @param token       the token of the request
     * @param model       the distribution model.
     * @throws Exception if the author is not a SYSADMIN or the request failed.
     */
    public void addDistributionRequest(String url, String authorNetId, String token, DistributionModel model)
        throws Exception {
        if (authorization.isOfType(authorNetId, AccountType.SYSADMIN)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<DistributionModel> entity = new HttpEntity<>(model, headers);

            try {
                restTemplate.postForEntity(url, entity, Void.class);
            } catch (Exception e) {
                throw new InnerRequestFailedException(innerRequestFailedExceptionString(url));
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId + ") is not a Sysadmin => can not add a distribution");
        }
    }

    /**
     * Sends a post request to the specified url and checks if the author is a Sysadmin.
     *
     * @param url         the url to send the request to.
     * @param authorNetId the netId of the author
     * @param token       the token of the author.
     * @throws Exception if the user is unauthorized or the inner request failed.
     */
    public void postRequestFromSysadmin(String url, String authorNetId, String token) throws Exception {
        if (authorization.isOfType(authorNetId, AccountType.SYSADMIN)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            try {
                restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            } catch (Exception e) {
                throw new InnerRequestFailedException(innerRequestFailedExceptionString(url));
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId + ") is not a Sysadmin");
        }
    }

    /**
     * Sends a get request to the specified url and checks if the author is a Sysadmin.
     *
     * @param url         the url to send the request to.
     * @param authorNetId the netId of the author
     * @param token       the token of the author.
     * @return the request response.
     * @throws Exception if the user is unauthorized or the inner request failed.
     */
    public String getRequestFromSysadmin(String url, String authorNetId, String token) throws Exception {
        if (authorization.isOfType(authorNetId, AccountType.SYSADMIN)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            try {
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                return response.getBody();
            } catch (Exception e) {
                throw new InnerRequestFailedException(innerRequestFailedExceptionString(url));
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId + ") is not a Sysadmin => can not check the status.");
        }
    }

    /**
     * Sends a get request to the specified url and checks if the author is a faculty account.
     *
     * @param url         the url to send the request to.
     * @param authorNetId the netId of the author
     * @param token       the token of the author.
     * @return the request response.
     * @throws NoSuchUserException         if the user is non-existing
     * @throws UnauthorizedException       if the user is unauthorized
     * @throws InnerRequestFailedException if the inner request failed
     */
    public String getRequestFromFacultyAccount(String url, String authorNetId, String token)
        throws NoSuchUserException, UnauthorizedException, InnerRequestFailedException {
        if (authorization.isOfType(authorNetId, AccountType.FAC_ACCOUNT)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            try {
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                return response.getBody();
            } catch (Exception e) {
                throw new InnerRequestFailedException(innerRequestFailedExceptionString(url));
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId + ") is not a Faculty account.");
        }
    }

    /**
     * Creates the String for the InnerRequestFailedException.
     *
     * @param url the Url
     * @return the String for the InnerRequestFailedException
     */
    public String innerRequestFailedExceptionString(String url) {
        return "Request to " + url + " failed.";
    }

    /**
     * Routes the request to the correct method.
     *
     * @param authorNetId - the netId of the sender.
     * @param token - the token with which the user is authenticated.
     * @return the response
     * @throws NoSuchUserException if the user cannot be found
     * @throws InnerRequestFailedException if the request fails
     * @throws UnauthorizedException if the user is unauthorized
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public String getScheduleRequestRouter(String authorNetId, String token)
            throws NoSuchUserException, UnauthorizedException, InnerRequestFailedException {
        String sysadminUrl = "http://localhost:8085/getAllSchedules";
        String facManagerUrl = "http://localhost:8085/getFacultySchedules";

        if (authorization.isOfType(authorNetId, AccountType.SYSADMIN)) {
            return getScheduleSysadmin(sysadminUrl, token);
        } else if (authorization.isOfType(authorNetId, AccountType.FAC_ACCOUNT)) {
            return getScheduleFacultyManager(facManagerUrl, authorNetId, token);
        } else if (authorization.isOfType(authorNetId, AccountType.EMPLOYEE)) {
            throw new UnauthorizedException("Employees cannot view schedules");
        } else {
            throw new UnauthorizedException("Request to view schedules failed.");
        }
    }

    /**
     * Gets all available schedules on all days for all faculties.
     *
     * @param url - the url of the end point
     * @param token - the authentication token of the user
     * @return the response
     */
    public String getScheduleSysadmin(String url, String token) throws InnerRequestFailedException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ScheduleResponseModel> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, ScheduleResponseModel.class);
            if (response.hasBody()) {
                return prettifyScheduleResponse(Objects.requireNonNull(response.getBody()));
            } else {
                return "No schedules were found";
            }
        } catch (Exception e) {
            throw new InnerRequestFailedException(innerRequestFailedExceptionString(url));
        }
    }

    /**
     * Gets all available schedules on all days for a given faculty.
     *
     * @param url - the url of the end point
     * @param authorNetId - the netId of the user that made the request
     * @param token - the authentication token of the user
     * @return the response
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public String getScheduleFacultyManager(String url, String authorNetId, String token)
            throws InnerRequestFailedException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        long facultyId;
        try {
            facultyId = facultyAccountService.getFacultyAssignedId(authorNetId);
        } catch (NoSuchUserException exception) {
            throw new InnerRequestFailedException(innerRequestFailedExceptionString(url));
        }

        HttpEntity<ScheduleRequestModel> entity = new HttpEntity<>(new ScheduleRequestModel(facultyId), headers);

        try {
            ResponseEntity<ScheduleResponseModel> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, ScheduleResponseModel.class);
            if (response.hasBody()) {
                return prettifyScheduleResponse(Objects.requireNonNull(response.getBody()));
            } else {
                return "No schedules were found for faculty: " + facultyId;
            }
        } catch (Exception e) {
            throw new InnerRequestFailedException(innerRequestFailedExceptionString(url));
        }
    }


    /**
     * Returns the list of schedules and faculties in a human-readable format.
     *
     * @param response the response of the request
     * @return a String with all the faculties and schedules
     */
    public static String prettifyScheduleResponse(ScheduleResponseModel response) {
        Map<String, List<String>> schedules = response.getSchedules();
        StringBuilder stringBuilder = new StringBuilder();
        for (String faculty : schedules.keySet()) {
            stringBuilder.append(faculty).append(":");
            for (String schedule : schedules.get(faculty)) {
                stringBuilder.append("\n\t:").append(schedule);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Send a request to the Request MS to manually approve/reject a request.
     *
     * @param url the respective URL to the Request MS
     * @param authorNetId the netID of the facManager
     * @param model model containing the scheduled day of execution if approved,
     *              the id of the request and whether it is approved or rejected
     * @param token the user token
     * @return whether the approval/rejection went through
     * @throws NoSuchUserException when no such user exists
     * @throws InnerRequestFailedException when the Request MS is not responding
     * @throws UnauthorizedException when the user submitting this request is not a faculty manager
     */
    public boolean approveRejectRequest(String url, String authorNetId, ManualApprovalModel model, String token)
            throws NoSuchUserException, InnerRequestFailedException, UnauthorizedException {
        if (authorization.isOfType(authorNetId, AccountType.FAC_ACCOUNT)
                && facultyAccountService.getFacultyAssignedId(authorNetId) == model.getRequestId()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<ManualApprovalModel> entity = new HttpEntity<>(model, headers);
            try {
                ResponseEntity<Boolean> response = restTemplate.exchange(url, HttpMethod.POST, entity, Boolean.class);
                return response.getBody();
            } catch (Exception e) {
                throw new InnerRequestFailedException(innerRequestFailedExceptionString(url));
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId
                    + ") is not authorized to approve or reject this request");
        }
    }

    /**
     * Gets the status of a given request.
     *
     * @param url the url of the request MS
     * @param authorNetId the netId of the user wanting to check the status of the request
     * @param requestId the id of the request
     * @param token the JWT token
     * @return the status of the request
     * @throws NoSuchUserException if no such user exists
     * @throws UnauthorizedException if the user is not authorized to check the status of this request
     * @throws InnerRequestFailedException if something goes wrong with the api call to the request MS
     */
    public String getStatusOfRequest(String url, String authorNetId, long requestId, String token)
            throws NoSuchUserException, UnauthorizedException, InnerRequestFailedException {
        boolean authorized = authorization.isOfType(authorNetId, AccountType.EMPLOYEE);
        List<Long> ids = getRequestIdsByNetId(authorNetId, token);
        if (authorized && ids.contains(requestId)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Long> entity = new HttpEntity<>(requestId, headers);
            try {
                ResponseEntity<Integer> response = restTemplate.exchange(url, HttpMethod.POST, entity, Integer.class);
                int status = response.getBody();
                return "Request has status " + status;
            } catch (Exception e) {
                throw new InnerRequestFailedException(url);
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId
                    + ") is not authorized to approve or reject this request");
        }
    }


    /**
     * Gets the IDs of all requests submitted by a given user.
     *
     * @param netId the netId of the given user
     * @param token the JWT token
     * @return the set of the IDs of all requests submitted by this user
     * @throws InnerRequestFailedException if the request MS does not respond
     */
    public List<Long> getRequestIdsByNetId(String netId, String token) throws InnerRequestFailedException {
        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        String url = "http://localhost:8084/getRequestIds";
        HttpEntity<String> entity = new HttpEntity<>(netId, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            List<Long> answers = new ArrayList<>();
            for (String id : response.getBody().split("/")) {
                answers.add(Long.parseLong(id));
            }
            return answers;
        } catch (Exception e) {
            throw new InnerRequestFailedException("Request to " + url + " failed");
        }
    }


    /** Returns the correct message to show the user when a request is successfully approved/rejected.
     *
     * @param approved whether the request is approved
     * @return the correct message to show the user when the request is approved/rejected
     */
    public String getRequestAnswer(boolean approved) {
        if (approved) {
            return "Request was successfully approved";
        } else {
            return "Request was successfully rejected";
        }
    }

    /**
     * Returns the correct message to show the user when they try to register a request.
     *
     * @param requestId the id of the submitted request
     * @return the correct message to show the user when the request is registered
     */
    public String registerRequestMessage(long requestId) {
        if (requestId == -1) {
            return "Request was not submitted successfully";
        }
        return "Request was successfully submitted with ID " + requestId;
    }

    /**
     * Register a resource request.
     *
     * @param url the url of the request MS
     * @param authorNetId the netID of the author of the request
     * @param requestModel the requestModel containing the information about the request
     * @param token the JWT token
     * @return whether the request went through
     * @throws NoSuchUserException if no such user was found
     * @throws InnerRequestFailedException if the request MS did not respond
     * @throws UnauthorizedException if the employee submitting the request is not employeed at the respective faculty
     */
    public Long registerRequest(String url, String authorNetId, RegistrationRequestModel requestModel, String token)
            throws NoSuchUserException, InnerRequestFailedException, UnauthorizedException {
        if (authorization.isOfType(authorNetId, AccountType.EMPLOYEE)) { //TODO check if faculty has employeed the employee
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<RegistrationRequestModel> entity = new HttpEntity<>(requestModel, headers);
            try {
                ResponseEntity<Long> response = restTemplate.exchange(url, HttpMethod.POST, entity, Long.class);
                return response.getBody();
            } catch (Exception e) {
                throw new InnerRequestFailedException(url);
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId
                    + ") is not employed by " + requestModel.getFacultyName());
        }
    }

    /**
     * Sends a request to contribute a node.
     *
     * @param url the url of the request
     * @param authorNetId the netId of the author of the request
     * @param token the token of the request
     * @param nodeInfo the model with all the information for the new node
     * @throws Exception if the author is not an EMPLOYEE at the requested faculty or the request failed
     */
    public long contributeNodeRequest(String url, String authorNetId, String token, NodeContributionRequestModel nodeInfo)
            throws Exception {
        Set<Long> facultyIds = employeeRepository.findByNetId(authorNetId).get().getParentFacultyIds();
        if (authorization.isOfType(authorNetId, AccountType.EMPLOYEE)
                && facultyIds.contains(nodeInfo.getFacultyId())) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);

                HttpEntity<NodeContributionRequestModel> entity = new HttpEntity<>(nodeInfo, headers);

                ResponseEntity<Long> response = restTemplate.postForEntity(url, entity, Long.class);
                return response.getBody();
            } catch (Exception e) {
                throw new InnerRequestFailedException(innerRequestFailedExceptionString(url));
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId + ") is not an Employee at the requested faculty");
        }
    }

    /**
     * Sends a request to delete a node.
     *
     * @param url the url of the request
     * @param authorNetId the netId of the author of the request
     * @param token the token of the request
     * @param nodeId the id of the node to be deleted
     * @throws Exception if the author is not an EMPLOYEE or the request failed
     */
    public String deleteNodeRequest(String url, String authorNetId, String token, long nodeId)
            throws Exception {
        if (authorization.isOfType(authorNetId, AccountType.EMPLOYEE)) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);

                HttpEntity<Long> entity = new HttpEntity<>(nodeId, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
                return response.getBody();
            } catch (Exception e) {
                throw new InnerRequestFailedException(innerRequestFailedExceptionString(url));
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId + ") is not an Employee");
        }
    }
}
