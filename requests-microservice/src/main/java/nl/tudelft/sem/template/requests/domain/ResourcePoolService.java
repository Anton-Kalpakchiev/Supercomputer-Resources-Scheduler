package nl.tudelft.sem.template.requests.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.requests.models.ResourcesDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Calendar;

@Component
public class ResourcePoolService {

    /**
     * Requests the available resources from the RP MS
     * @param facultyName name of the faculty
     * @return the available resources
     * @throws IOException
     */
    public Resources getFacultyResourcesByName(String facultyName, String token) throws IOException, InvalidResourcesException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + token);

        String requestBody = "{\"facultyName\": \"" + facultyName + "\"}";
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8085/resources", request, String.class);

//        String[] responseArr = response.getBody().split("-");
//        Resources resources = new Resources(Integer.parseInt(responseArr[0]), Integer.parseInt(responseArr[1]), Integer.parseInt(responseArr[2]));


        ObjectMapper objectMapper = new ObjectMapper();
//        System.out.println(response.getBody());
        ResourcesDto availableResources = objectMapper.readValue(response.getBody(), ResourcesDto.class);
        return new Resources(availableResources.getCpu(), availableResources.getGpu(), availableResources.getMemory());
    }

    public ResponseEntity<Boolean> automaticApproval(Calendar day, long requestId, String token) throws IOException, InvalidResourcesException {
        System.out.println(requestId);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + token);

        int month = day.get(Calendar.MONTH) + 1;
        String dayString = day.get(Calendar.DAY_OF_MONTH) + "-" + month + "-" + day.get(Calendar.YEAR);
        String requestBody = "{\"day\": \"" + dayString + "\",\"requestId\": \"" + requestId + "\"}";
        System.out.println(requestBody);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Boolean> response = restTemplate.postForEntity("http://localhost:8085/automaticApproval", request, Boolean.class);

        return response;
    }

}
