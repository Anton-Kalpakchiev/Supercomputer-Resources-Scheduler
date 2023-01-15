package nl.tudelft.sem.template.users.facade;

import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.AccountType;
import nl.tudelft.sem.template.users.domain.FacultyException;
import nl.tudelft.sem.template.users.domain.InnerRequestFailedException;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
import nl.tudelft.sem.template.users.domain.RegistrationService;
import nl.tudelft.sem.template.users.models.FacultyCreationResponseModel;
import nl.tudelft.sem.template.users.models.TemporaryRequestModel;
import nl.tudelft.sem.template.users.models.facade.DistributionModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ResourcePoolRequestService extends RequestSenderService {
    private final transient RegistrationService registrationService;

    /**
     * Constructor for a request sender service that interacts with the resource pool microservice .
     *
     * @param authorization       inherited from request sender service
     * @param restTemplate        inherited from request sender service
     * @param registrationService the user registration service
     */
    public ResourcePoolRequestService(AuthorizationManager authorization, RestTemplate restTemplate,
                                      RegistrationService registrationService) {
        super(authorization, restTemplate);
        this.registrationService = registrationService;
    }

    private long createFaculty(String managerNetId, String facultyName, String token)
        throws FacultyException {
        String url = "http://localhost:8085/createFaculty";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<TemporaryRequestModel> entity = new HttpEntity<>(
            new TemporaryRequestModel(facultyName, managerNetId), headers);

        ResponseEntity<FacultyCreationResponseModel> result = restTemplate.postForEntity(url, entity,
            FacultyCreationResponseModel.class);

        if (result.getStatusCode().is2xxSuccessful()) {
            registrationService.dropEmployee(managerNetId);
            registrationService.addFacultyAccount(managerNetId, (int) result.getBody().getFacultyId());

            return result.getBody().getFacultyId();
        } else {
            throw new FacultyException(result.getStatusCode().getReasonPhrase());
        }
    }

    public long createFaculty(String authorNetId, String managerNetId, String facultyName, String token)
        throws FacultyException, NoSuchUserException, UnauthorizedException {
        if (authorization.isOfType(authorNetId, AccountType.SYSADMIN)) {
            if (authorization.isOfType(managerNetId, AccountType.EMPLOYEE)) {
                return createFaculty(managerNetId, facultyName, token);
            } else {
                throw new NoSuchUserException("No such employee: " + managerNetId);
            }
        } else {
            throw new UnauthorizedException("User (" + authorNetId + ") is not a Sysadmin => can not create a faculty");
        }
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
}
