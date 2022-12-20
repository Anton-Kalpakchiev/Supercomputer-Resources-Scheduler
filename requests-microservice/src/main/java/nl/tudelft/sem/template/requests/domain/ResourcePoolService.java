package nl.tudelft.sem.template.requests.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Calendar;
import nl.tudelft.sem.template.requests.models.ResourcesDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ResourcePoolService {

    /**
     * Approves the request. Tells the RP MS to schedule the request for the given day.
     *
     * @param day the day the request has to be scheduled on.
     * @param requestId the id of the to be scheduled request.
     * @param token the jwtToken.
     * @return true when the request is succesfully scheduled.
     */
    public ResponseEntity<Boolean> approval(Calendar day, long requestId, String token) {
        System.out.println(requestId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + token);

        int month = day.get(Calendar.MONTH) + 1;
        String dayString = day.get(Calendar.DAY_OF_MONTH) + "-" + month + "-" + day.get(Calendar.YEAR);
        String requestBody = "{\"day\": \"" + dayString + "\",\"requestId\": \"" + requestId + "\"}";
        System.out.println(requestBody);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Boolean> response = restTemplate.postForEntity("http://localhost:8085/automaticApproval", request, Boolean.class);

        return response;
    }

}
