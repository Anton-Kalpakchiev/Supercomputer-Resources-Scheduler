package nl.tudelft.sem.template.users.domain;

import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.models.facade.DistributionModel;
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

    private final transient RegistrationService registrationService;
    private final transient AuthorizationManager authorization;

    private final transient RestTemplate restTemplate;

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
                                RestTemplate restTemplate) {
        this.sysadminRepository = sysadminRepository;
        this.employeeRepository = employeeRepository;
        this.facultyAccountRepository = facultyAccountRepository;
        this.registrationService = registrationService;
        this.authorization = authorization;
        this.restTemplate = restTemplate;
    }

    /**
     * Sends a request to "resource pool -> /distribution/current" in the Resource pool microservice
     * to fetch the current distribution.
     *
     * @param authorNetId the author of the request
     * @param token the authentication token
     * @return the result of the call
     * @throws Exception if the user is unauthorized or the request fails.
     */
    public String getCurrentDistributionRequest(String authorNetId, String token) throws Exception {
        if (authorization.isOfType(authorNetId, AccountType.SYSADMIN)) {
            String url = "http://localhost:8085/distribution/current";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new InnerRequestFailedException("Request to " + url + " failed.");
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId + ") is not a Sysadmin => can not check distribution");
        }
    }

    /**
     * Sends a request to "resource pool -> /distribution/add" in the Resource pool microservice.
     *
     * @param authorNetId the netId of the author of the request.
     * @param token the token of the request
     * @param model the distribution model.
     * @throws Exception if the author is not a SYSADMIN or the request failed.
     */
    public void addDistributionRequest(String authorNetId, String token, DistributionModel model) throws Exception {
        if (authorization.isOfType(authorNetId, AccountType.SYSADMIN)) {
            String url = "http://localhost:8085/distribution/add";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<DistributionModel> entity = new HttpEntity<>(model, headers);
            ResponseEntity<Void> response = restTemplate.postForEntity(url, entity, Void.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return;
            } else {
                throw new InnerRequestFailedException("Request to " + url + " failed.");
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId + ") is not a Sysadmin => can not add a distribution");
        }
    }

    /**
     * Sends a request to "resource pool -> /distribution/status" in the Resource pool microservice.
     *
     * @param authorNetId the netId of the author
     * @param token the token of the author
     * @return the request response
     * @throws Exception if the author is not a Sysadmin
     */
    public String statusDistributionRequest(String authorNetId, String token) throws Exception {
        if (authorization.isOfType(authorNetId, AccountType.SYSADMIN)) {
            String url = "http://localhost:8085/distribution/status";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new InnerRequestFailedException("Request to " + url + " failed.");
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId + ") is not a Sysadmin => can not check the status.");
        }
    }
}
