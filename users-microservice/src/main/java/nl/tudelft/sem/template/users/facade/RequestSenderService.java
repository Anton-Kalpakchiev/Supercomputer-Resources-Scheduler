package nl.tudelft.sem.template.users.facade;

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
public class RequestSenderService {
    private final transient SysadminRepository sysadminRepository;
    private final transient EmployeeRepository employeeRepository;
    private final transient FacultyAccountRepository facultyAccountRepository;
    private final transient FacultyAccountService facultyAccountService;
    private final transient EmployeeService employeeService;


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
        if (authorization.isOfType(authorNetId, AccountType.EMPLOYEE)
            && employeeService.getRequestIdsByNetId(authorNetId, token).contains(requestId)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Long> entity = new HttpEntity<>(requestId, headers);
            try {
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                return response.getBody();
            } catch (Exception e) {
                throw new InnerRequestFailedException(requestTo + url + failed);
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId
                    + ") is not authorized to approve or reject this request");
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
     * @param wentThrough whether the request went through
     * @return the correct message to show the user when the request is registered
     */
    public String registerRequestMessage(boolean wentThrough) {
        if (wentThrough) {
            return "Request was successfully submitted";
        } else {
            return "Request was not successfully submitted";
        }
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
    public boolean registerRequest(String url, String authorNetId, RegistrationRequestModel requestModel, String token)
            throws NoSuchUserException, InnerRequestFailedException, UnauthorizedException {
        if (authorization.isOfType(authorNetId, AccountType.EMPLOYEE)) { //TODO check if faculty has employeed the employee
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<RegistrationRequestModel> entity = new HttpEntity<>(requestModel, headers);
            try {
                restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                return true;
            } catch (Exception e) {
                throw new InnerRequestFailedException(requestTo + url + failed);
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId
                    + ") is not employeed by " + requestModel.getFacultyName());
        }
    }
}
