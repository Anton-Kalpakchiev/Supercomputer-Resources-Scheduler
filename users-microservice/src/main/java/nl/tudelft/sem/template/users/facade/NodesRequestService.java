package nl.tudelft.sem.template.users.facade;

import java.util.Set;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.AccountType;
import nl.tudelft.sem.template.users.domain.EmployeeRepository;
import nl.tudelft.sem.template.users.domain.InnerRequestFailedException;
import nl.tudelft.sem.template.users.models.facade.NodeContributionRequestModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NodesRequestService extends RequestSenderService {

    private final transient EmployeeRepository employeeRepository;

    /**
     * Constructor for a request sending service to the nodes microservice.
     *
     * @param authorization inherited from the request sender service
     * @param restTemplate inherited from the request sender service
     * @param employeeRepository an employee repository
     */
    public NodesRequestService(AuthorizationManager authorization, RestTemplate restTemplate,
                               EmployeeRepository employeeRepository) {
        super(authorization, restTemplate);
        this.employeeRepository = employeeRepository;
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
