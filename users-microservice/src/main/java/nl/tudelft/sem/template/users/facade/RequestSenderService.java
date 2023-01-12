package nl.tudelft.sem.template.users.facade;

import lombok.AllArgsConstructor;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.AccountType;
import nl.tudelft.sem.template.users.domain.InnerRequestFailedException;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
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
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class RequestSenderService {
    protected final transient AuthorizationManager authorization;
    protected final transient RestTemplate restTemplate;

    /**
     * Sends a post request to the specified url and checks if the author is a Sysadmin.
     *
     * @param url         the url to send the request to.
     * @param authorNetId the netId of the author
     * @param token       the token of the author.
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
                throw new InnerRequestFailedException(innerRequestFailedExceptionString(url));
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId + ") is not a Sysadmin");
        }
    }

    /**
     * Sends a get request to the specified url and checks if the author is a Sysadmin.
     *
     * @param url         the url to send the request to.
     * @param authorNetId the netId of the author
     * @param token       the token of the author.
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
                throw new InnerRequestFailedException(innerRequestFailedExceptionString(url));
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId + ") is not a Sysadmin => can not check the status.");
        }
    }

    /**
     * Sends a get request to the specified url and checks if the author is a faculty account.
     *
     * @param url         the url to send the request to.
     * @param authorNetId the netId of the author
     * @param token       the token of the author.
     * @return the request response.
     * @throws NoSuchUserException         if the user is non-existing
     * @throws UnauthorizedException       if the user is unauthorized
     * @throws InnerRequestFailedException if the inner request failed
     */
    public String getRequestFromFacultyAccount(String url, String authorNetId, String token)
        throws NoSuchUserException, UnauthorizedException, InnerRequestFailedException {
        if (authorization.isOfType(authorNetId, AccountType.FAC_ACCOUNT)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            try {
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                return response.getBody();
            } catch (Exception e) {
                throw new InnerRequestFailedException(innerRequestFailedExceptionString(url));
            }
        } else {
            throw new UnauthorizedException("(" + authorNetId + ") is not a Faculty account.");
        }
    }

    /**
     * Creates the String for the InnerRequestFailedException.
     *
     * @param url the Url
     * @return the String for the InnerRequestFailedException
     */
    public String innerRequestFailedExceptionString(String url) {
        return "Request to " + url + " failed.";
    }
}
