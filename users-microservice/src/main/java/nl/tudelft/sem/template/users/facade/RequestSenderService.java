package nl.tudelft.sem.template.users.facade;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.AccountType;
import nl.tudelft.sem.template.users.domain.EmployeeService;
import nl.tudelft.sem.template.users.domain.FacultyAccountService;
import nl.tudelft.sem.template.users.domain.EmployeeRepository;
import nl.tudelft.sem.template.users.domain.FacultyAccountRepository;
import nl.tudelft.sem.template.users.domain.FacultyAccountService;
import nl.tudelft.sem.template.users.domain.InnerRequestFailedException;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
import nl.tudelft.sem.template.users.domain.RegistrationService;
import nl.tudelft.sem.template.users.domain.SysadminRepository;
import nl.tudelft.sem.template.users.models.facade.DistributionModel;
import nl.tudelft.sem.template.users.models.facade.ScheduleRequestModel;
import nl.tudelft.sem.template.users.models.facade.ScheduleResponseModel;
import nl.tudelft.sem.template.users.models.facade.ManualApprovalModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * A DDD for sending requests to other microservice.
 */
@Service
@AllArgsConstructor
public class RequestSenderService {
    private final transient FacultyAccountService facultyAccountService;
    private final transient SysadminRepository sysadminRepository;
    private final transient EmployeeRepository employeeRepository;
    private final transient FacultyAccountRepository facultyAccountRepository;
    private final transient FacultyAccountService facultyAccountService;


    private final transient RegistrationService registrationService;
    private final transient AuthorizationManager authorization;

    private final transient RestTemplate restTemplate;
    private final transient String requestTo = "Request to ";
    private final transient String failed = " failed";

    /**
     * Constructor for the request sender service.
     *
     * @param sysadminRepository the injected sysadmin repository
     * @param employeeRepository the injected employee repository.
     * @param facultyAccountRepository the injected faculty account repo.
     * @param registrationService the injected registration service
     * @param authorization the authorization manager
     * @param restTemplate the provided rest template.
     */
    public RequestSenderService(SysadminRepository sysadminRepository, EmployeeRepository employeeRepository,
                                FacultyAccountRepository facultyAccountRepository,
                                RegistrationService registrationService,
                                AuthorizationManager authorization,
                                RestTemplate restTemplate,
                                FacultyAccountService facultyAccountService) {
        this.sysadminRepository = sysadminRepository;
        this.employeeRepository = employeeRepository;
        this.facultyAccountRepository = facultyAccountRepository;
        this.registrationService = registrationService;
        this.authorization = authorization;
        this.restTemplate = restTemplate;
        this.facultyAccountService = facultyAccountService;
    }

    /**
     * Sends a request to the specified url in the Resource pool microservice.
     *
     * @param url the url of the request
     * @param authorNetId the netId of the author of the request.
     * @param token the token of the request
     * @param model the distribution model.
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
                throw new InnerRequestFailedException(requestTo + url + failed);
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId + ") is not a Sysadmin => can not add a distribution");
        }
    }

    /**
     * Sends a post request to the specified url and checks if the author is a Sysadmin.
     *
     * @param url the url to send the request to.
     * @param authorNetId the netId of the author
     * @param token the token of the author.
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
                throw new InnerRequestFailedException(requestTo + url + failed);
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId + ") is not a Sysadmin");
        }
    }

    /**
     * Sends a get request to the specified url and checks if the author is a Sysadmin.
     *
     * @param url the url to send the request to.
     * @param authorNetId the netId of the author
     * @param token the token of the author.
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
                throw new InnerRequestFailedException(requestTo + url + failed);
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId + ") is not a Sysadmin => can not check the status.");
        }
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
            throw new InnerRequestFailedException("Request to " + url + " failed.");
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
    public String getScheduleFacultyManager(String url, String authorNetId, String token) throws InnerRequestFailedException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        long facultyId;
        try {
            facultyId = facultyAccountService.getFacultyAssignedId(authorNetId);
        } catch (NoSuchUserException exception) {
            throw new InnerRequestFailedException("Request to " + url + " failed.");
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
            throw new InnerRequestFailedException("Request to " + url + " failed.");
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
                throw new InnerRequestFailedException(requestTo + url + failed);
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId
                    + ") is not a Faculty Manager => can not approve or reject requests.");
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
}
