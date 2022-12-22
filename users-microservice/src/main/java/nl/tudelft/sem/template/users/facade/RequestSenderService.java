package nl.tudelft.sem.template.users.facade;

import java.util.Set;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.AccountType;
import nl.tudelft.sem.template.users.domain.EmployeeRepository;
import nl.tudelft.sem.template.users.domain.FacultyAccountRepository;
import nl.tudelft.sem.template.users.domain.InnerRequestFailedException;
import nl.tudelft.sem.template.users.domain.RegistrationService;
import nl.tudelft.sem.template.users.domain.SysadminRepository;
import nl.tudelft.sem.template.users.models.facade.DistributionModel;
import nl.tudelft.sem.template.users.models.facade.NodeContributionRequestModel;
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
                throw new InnerRequestFailedException("Request to " + url + " failed.");
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
                throw new InnerRequestFailedException("Request to " + url + " failed.");
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
                throw new InnerRequestFailedException("Request to " + url + " failed.");
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId + ") is not a Sysadmin => can not check the status.");
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
                throw new InnerRequestFailedException("Request to " + url + " failed.");
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
                throw new InnerRequestFailedException("Request to " + url + " failed.");
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId + ") is not an Employee");
        }
    }
}
