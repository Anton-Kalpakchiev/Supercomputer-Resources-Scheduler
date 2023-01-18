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

    /**
     * Method for creating a faculty by calling the microservice Resource Pool.
     *
     * @param authorNetId the netId of the author of the request.
     * @param managerNetId the netId of the manager of the request.
     * @param facultyName the new faculty name.
     * @param token the token of the request.
     * @return the id of the new faculty
     * @throws Exception if a user is unauthorized or does not exist
     */
    public long createFaculty(String authorNetId, String managerNetId, String facultyName, String token)
        throws FacultyException, NoSuchUserException, UnauthorizedException {
        if (authorization.isOfType(authorNetId, AccountType.SYSADMIN)) {
            checkEmployee(managerNetId);
            return sendCreateFacultyRequest(managerNetId, facultyName, token);
        } else {
            throw new UnauthorizedException("User (" + authorNetId + ") is not a Sysadmin => can not create a faculty");
        }
    }

    /**
     * Checks whether the given netId belongs to an employee.
     *
     * @param managerNetId the netId to check for
     * @throws NoSuchUserException if the netId does not belong to an employee
     */
    private void checkEmployee(String managerNetId) throws NoSuchUserException {
        if (!authorization.isOfType(managerNetId, AccountType.EMPLOYEE)) {
            throw new NoSuchUserException("No such employee: " + managerNetId);
        }
    }

    /**
     * Sends a request to the Resource Pool microservice to create a faculty.
     *
     * @param managerNetId the netId of the account that is going to be the faculty manager
     * @param facultyName the name that the faculty is going to have
     * @param token the token of the request
     * @return the facultyId of the created faculty
     * @throws FacultyException if the faculty could not be created
     */
    private long sendCreateFacultyRequest(String managerNetId, String facultyName, String token)
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
