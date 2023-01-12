package nl.tudelft.sem.template.users.facade;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.AccountType;
import nl.tudelft.sem.template.users.domain.EmployeeRepository;
import nl.tudelft.sem.template.users.domain.EmploymentException;
import nl.tudelft.sem.template.users.domain.FacultyAccountService;
import nl.tudelft.sem.template.users.domain.FacultyException;
import nl.tudelft.sem.template.users.domain.FacultyVerificationService;
import nl.tudelft.sem.template.users.domain.InnerRequestFailedException;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
import nl.tudelft.sem.template.users.domain.RegistrationService;
import nl.tudelft.sem.template.users.models.FacultyCreationResponseModel;
import nl.tudelft.sem.template.users.models.ResourcesDto;
import nl.tudelft.sem.template.users.models.TemporaryRequestModel;
import nl.tudelft.sem.template.users.models.facade.DistributionModel;
import nl.tudelft.sem.template.users.models.facade.ReleaseResourcesRequestModel;
import nl.tudelft.sem.template.users.models.facade.RequestTomorrowResourcesRequestModel;
import nl.tudelft.sem.template.users.models.facade.ScheduleRequestModel;
import nl.tudelft.sem.template.users.models.facade.ScheduleResponseModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ResourcePoolRequestService extends RequestSenderService {
    private final transient RegistrationService registrationService;
    private final transient FacultyVerificationService facultyVerificationService;
    private final transient FacultyAccountService facultyAccountService;
    private final transient EmployeeRepository employeeRepository;

    /**
     * Constructor for a request sender service that interacts with the resource pool microservice.
     *
     * @param authorization inherited from request sender service
     * @param restTemplate inherited from request sender service
     * @param registrationService the user registration service
     * @param facultyVerificationService the faculty verification service
     * @param facultyAccountService the faculty account service
     * @param employeeRepository the employee repository
     */
    public ResourcePoolRequestService(AuthorizationManager authorization, RestTemplate restTemplate,
                                      RegistrationService registrationService,
                                      FacultyVerificationService facultyVerificationService,
                                      FacultyAccountService facultyAccountService, EmployeeRepository employeeRepository) {
        super(authorization, restTemplate);
        this.registrationService = registrationService;
        this.facultyVerificationService = facultyVerificationService;
        this.facultyAccountService = facultyAccountService;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Authenticate a faculty manager.
     *
     * @param netId the netId of the user
     * @param providedFacultyId the provided faculty id
     * @return whether the faculty manager is validated.
     * @throws NoSuchUserException when no user is found
     * @throws FacultyException when the faculty doesn't exist
     */
    public boolean authenticateFacultyManager(String netId, long providedFacultyId, String token)
            throws NoSuchUserException, FacultyException {
        if (super.authorization.isOfType(netId, AccountType.FAC_ACCOUNT)) {
            if (facultyAccountService.getFacultyAssignedId(netId) == providedFacultyId) {
                return facultyVerificationService.verifyFaculty(providedFacultyId, token);
            }
        }
        return false;
    }

    /**
     * Authenticates a request to view the available resources for tomorrow.
     *
     * @param authorNetId the netId of the author of the request
     * @param token the authentication token
     * @param facultyId the provided faculty id
     * @return whether the user is authenticated
     * @throws FacultyException the faculty could not be found
     * @throws NoSuchUserException the user could not be found
     * @throws UnauthorizedException the user was not authorized
     * @throws EmploymentException the user was not employed at the relevant faculty
     */
    public boolean authenticateFacultyRequest(String authorNetId, String token, long facultyId)
            throws FacultyException, NoSuchUserException, UnauthorizedException, EmploymentException {
        try {
            facultyVerificationService.verifyFaculty(facultyId, token);
        } catch (FacultyException e) {
            throw new FacultyException("The faculty does not exist!");
        }
        if (authorization.isOfType(authorNetId, AccountType.SYSADMIN)) {
            return true;
        } else if (authorization.isOfType(authorNetId, AccountType.FAC_ACCOUNT)) {
            return facultyAccountService.getFacultyAssignedId(authorNetId) == facultyId;
        } else if (authorization.isOfType(authorNetId, AccountType.EMPLOYEE)) {
            if (employeeRepository.findByNetId(authorNetId).isPresent()) {
                Set<Long> faculties = employeeRepository.findByNetId(authorNetId).get().getParentFacultyIds();
                return faculties.contains(facultyId);
            } else {
                throw new EmploymentException("Employee was not employed at this faculty");
            }
        } else {
            throw new UnauthorizedException("Request to view schedules failed.");
        }
    }

    /**
     * Request to get the available resources for the next day.
     *
     * @param url the provided url
     * @param authorNetId the netId of the user
     * @param token the authentication token
     * @param facultyId the provided faculty id
     * @return the available resources for tomorrow
     * @throws InnerRequestFailedException thrown when the request is not processed correctly.
     */
    public ResourcesDto getResourcesTomorrow(String url, String authorNetId, String token, long facultyId)
            throws InnerRequestFailedException {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        tomorrow.setTime(tomorrow.getTime());

        try {
            authenticateFacultyRequest(authorNetId, token, facultyId);
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            RequestTomorrowResourcesRequestModel model = new RequestTomorrowResourcesRequestModel(facultyId);
            HttpEntity<RequestTomorrowResourcesRequestModel> entity = new HttpEntity<>(model, headers);

            ResponseEntity<ResourcesDto> response = super.restTemplate.postForEntity(url, entity, ResourcesDto.class);
            if (response.hasBody()) {
                return response.getBody();
            } else {
                throw new InnerRequestFailedException("No schedule was found");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new InnerRequestFailedException(innerRequestFailedExceptionString(url));
        }
    }


    /**
     * Method for creating a faculty by calling the microservice Resource Pool.
     *
     * @param authorNetId the netId of the author of the request.
     * @param managerNetId the netid of the manager of the request.
     * @param facultyName the new faculty name.
     * @param token the token of the request.
     * @return the id of the new faculty
     * @throws Exception if a user is unauthorized or does not exist
     */
    public long createFaculty(String authorNetId, String managerNetId, String facultyName, String token)
            throws FacultyException, NoSuchUserException, UnauthorizedException {
        if (authorization.isOfType(authorNetId, AccountType.SYSADMIN)) {
            if (authorization.isOfType(managerNetId, AccountType.EMPLOYEE)) {
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

    /**
     * Releases the resources of a faculty for a given day.
     *
     * @param url the url to route the request to
     * @param authorNetId the netId of the user making the request
     * @param token the authentication token
     * @param request the requestBody
     * @return a response from the resource-pool microservice
     * @throws NoSuchUserException when the user does not exist
     * @throws InnerRequestFailedException when the request could not be processed correctly
     * @throws UnauthorizedException when the user is not authorized
     * @throws FacultyException when the faculty does not exist
     */
    public String releaseResourcesRequest(String url, String authorNetId, String token, ReleaseResourcesRequestModel request)
            throws NoSuchUserException, InnerRequestFailedException, UnauthorizedException, FacultyException {
        if (authenticateFacultyManager(authorNetId, request.getFacultyId(), token)) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);

                HttpEntity<ReleaseResourcesRequestModel> entity = new HttpEntity<>(request, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

                return response.getBody();
            } catch (Exception e) {
                throw new InnerRequestFailedException(innerRequestFailedExceptionString(url));
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId + ") is not an faculty manager");
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
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public String getScheduleRequestRouter(String authorNetId, String token)
            throws NoSuchUserException, UnauthorizedException, InnerRequestFailedException {
        String sysadminUrl = "http://localhost:8085/getAllSchedules";
        String facManagerUrl = "http://localhost:8085/getFacultySchedules";

        if (super.authorization.isOfType(authorNetId, AccountType.SYSADMIN)) {
            return getScheduleSysadmin(sysadminUrl, token);
        } else if (super.authorization.isOfType(authorNetId, AccountType.FAC_ACCOUNT)) {
            return getScheduleFacultyManager(facManagerUrl, authorNetId, token);
        } else if (super.authorization.isOfType(authorNetId, AccountType.EMPLOYEE)) {
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
            ResponseEntity<ScheduleResponseModel> response = super.restTemplate.exchange(
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
}
