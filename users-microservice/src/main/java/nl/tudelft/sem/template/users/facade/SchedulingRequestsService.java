package nl.tudelft.sem.template.users.facade;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.AccountType;
import nl.tudelft.sem.template.users.domain.FacultyException;
import nl.tudelft.sem.template.users.domain.InnerRequestFailedException;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
import nl.tudelft.sem.template.users.domain.RegistrationService;
import nl.tudelft.sem.template.users.models.ResourcesDto;
import nl.tudelft.sem.template.users.models.facade.ReleaseResourcesRequestModel;
import nl.tudelft.sem.template.users.models.facade.RequestTomorrowResourcesRequestModel;
import nl.tudelft.sem.template.users.models.facade.ScheduleRequestModel;
import nl.tudelft.sem.template.users.models.facade.ScheduleResponseModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SchedulingRequestsService extends RequestSenderService {
    private final transient VerificationService verificationService;

    /**
     * Constructor for request sending service to the requests microservice concerning scheduling logic.
     *
     * @param authorization inherited from request sender service
     * @param restTemplate inherited from request sender service
     * @param verificationService the verification service
     */
    public SchedulingRequestsService(AuthorizationManager authorization, RestTemplate restTemplate,
                                     VerificationService verificationService) {
        super(authorization, restTemplate);
        this.verificationService = verificationService;
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
            verificationService.authenticateFacultyRequest(authorNetId, token, facultyId);
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<RequestTomorrowResourcesRequestModel> entity = new HttpEntity<>(
                    new RequestTomorrowResourcesRequestModel(facultyId), headers);

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
        if (verificationService.authenticateFacultyManager(authorNetId, request.getFacultyId(), token)) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);

                ResponseEntity<String> response = restTemplate.postForEntity(url,
                        new HttpEntity<>(request, headers), String.class);

                return response.getBody();
            } catch (Exception e) {
                throw new InnerRequestFailedException(innerRequestFailedExceptionString(url));
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId + ") is not an faculty manager");
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
            facultyId = verificationService.retrieveFacultyId(authorNetId);
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
