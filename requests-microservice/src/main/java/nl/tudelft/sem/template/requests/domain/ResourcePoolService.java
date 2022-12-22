package nl.tudelft.sem.template.requests.domain;

import java.util.Calendar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.requests.models.ResourcesDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ResourcePoolService {

    /**
     * Approves the request. Tells the RP MS to schedule the request for the given day.
     *
     * @param day the day the request has to be scheduled on.
     * @param requestId the id of the to be scheduled request.
     * @param token the jwtToken.
     * @return true when the request is successfully scheduled.
     */
    public ResponseEntity<Boolean> approval(Calendar day, long requestId, String token) {
        System.out.println(requestId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + token);
        int month = day.get(Calendar.MONTH);
        String dayString = day.get(Calendar.DAY_OF_MONTH) + "-" + month + "-" + day.get(Calendar.YEAR);
        String requestBody = "{\"day\": \"" + dayString + "\",\"requestId\": \"" + requestId + "\"}";

        System.out.println(requestBody);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Boolean> response = restTemplate.postForEntity("http://localhost:8085/automaticApproval", request, Boolean.class);

        return response;
    }

    public String getFacultyNameForFacultyId(long facultyId, String token) throws ResponseStatusException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<Long> request = new HttpEntity<>(facultyId, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8085/get-faculty-name", request, String.class);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return response.getBody();
    }

    public long getIdByName(String facultyName, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>(facultyName, headers);

        RestTemplate restTemplate = new RestTemplate();
        long facultyId = restTemplate.postForObject("http://localhost:8085/get-faculty-id", request, Long.class);
        return facultyId;
    }

    /**
     * Requests the available resources from the RP MS.
     *
     * @param facultyId id of the faculty.
     *
     * @return the available resources
     */
    public Resources getFacultyResourcesById(long facultyId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<Long> request = new HttpEntity<>(facultyId, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response =
                restTemplate.postForEntity("http://localhost:8085/availableFacultyResources",
                        request, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ResourcesDto availableResources = objectMapper.readValue(response.getBody(), ResourcesDto.class);
            return new Resources(availableResources.getCpu(), availableResources.getGpu(), availableResources.getMemory());
        } catch(JsonProcessingException j) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
