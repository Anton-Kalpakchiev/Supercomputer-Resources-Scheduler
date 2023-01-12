package nl.tudelft.sem.template.users.facade;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.AccountType;
import nl.tudelft.sem.template.users.domain.InnerRequestFailedException;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
import nl.tudelft.sem.template.users.models.facade.ManualApprovalModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RequestsRequestService extends RequestSenderService {

    /**
     * Constructor for request sending service that interacts with the requests microservice.
     *
     * @param authorization inherited from request sending service
     * @param restTemplate inherited from the request sending service
     */
    public RequestsRequestService(AuthorizationManager authorization, RestTemplate restTemplate) {
        super(authorization, restTemplate);
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
        if (authorization.isOfType(authorNetId, AccountType.FAC_ACCOUNT)) {
            //TODO make sure the request is to the faculty manager's faculty
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
        boolean authorized = super.authorization.isOfType(authorNetId, AccountType.EMPLOYEE);
        List<Long> ids = getRequestIdsByNetId(authorNetId, token);
        if (authorized && ids.contains(requestId)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Long> entity = new HttpEntity<>(requestId, headers);
            try {
                ResponseEntity<Integer> response = super.restTemplate.exchange(url, HttpMethod.POST, entity, Integer.class);
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
            ResponseEntity<String> response = super.restTemplate.postForEntity(url, entity, String.class);
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
        if (super.authorization.isOfType(authorNetId, AccountType.EMPLOYEE)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<RegistrationRequestModel> entity = new HttpEntity<>(requestModel, headers);
            try {
                ResponseEntity<Long> response = super.restTemplate.exchange(url, HttpMethod.POST, entity, Long.class);
                return response.getBody();
            } catch (Exception e) {
                throw new InnerRequestFailedException(url);
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId
                    + ") is not employed by " + requestModel.getFacultyName());
        }
    }
}
