package nl.tudelft.sem.template.requests.domain;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

    public UserService(){
        
    }
    
    public Long getFacultyIdForManager(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + token);

        HttpEntity<String> request = new HttpEntity<>("", headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Long> response = restTemplate.postForEntity("http://localhost:8086/get-faculty-id-for-manager", request, Long.class);
        return response.getBody();
    }

    }
