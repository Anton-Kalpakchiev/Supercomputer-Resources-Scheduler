package nl.tudelft.sem.template.resourcepool.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import nl.tudelft.sem.template.resourcepool.models.ResourcesByIdModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RequestService {

    /**
     * Requests the resources from the Request MS.
     *
     * @param requestId name of the faculty
     * @param token the jwtToken
     * @return the available resources
     * @throws IOException when the input is incorrectly formatted.
     */
    public Resources getRequestedResourcesById(long requestId, String token) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + token);

        String requestBody = "{\"requestId\": \"" + requestId + "\"}";
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8084/resourcesById", request, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        ResourcesByIdModel requestedResources = objectMapper.readValue(response.getBody(), ResourcesByIdModel.class);
        return new Resources(requestedResources.getCpu(), requestedResources.getGpu(), requestedResources.getMemory());
    }
}
