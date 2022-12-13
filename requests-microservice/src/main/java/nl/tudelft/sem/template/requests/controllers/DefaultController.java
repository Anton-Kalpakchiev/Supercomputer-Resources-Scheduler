package nl.tudelft.sem.template.requests.controllers;

import com.fasterxml.classmate.types.ResolvedInterfaceType;
import nl.tudelft.sem.template.requests.authentication.AuthManager;
import nl.tudelft.sem.template.requests.domain.InvalidResourcesException;
import nl.tudelft.sem.template.requests.domain.RegistrationService;
import nl.tudelft.sem.template.requests.domain.Resources;
import nl.tudelft.sem.template.requests.models.RegistrationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;

import static nl.tudelft.sem.template.requests.authentication.JwtRequestFilter.AUTHORIZATION_HEADER;

/**
 * Hello World requests controller.
 * <p>
 * This controller shows how you can extract information from the JWT token.
 * </p>
 */
@RestController
public class DefaultController {

    private final transient AuthManager authManager;
    private final transient RegistrationService registrationService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager         Spring Security component used to authenticate and authorize the user
     * @param registrationService The service that will allow requests to be saved to the database
     */
    @Autowired
    public DefaultController(AuthManager authManager, RegistrationService registrationService) {
        this.authManager = authManager;
        this.registrationService = registrationService;
    }

    /**
     * The registration process for a request.
     *
     * @param request The request model.
     * @return The response entity status.
     * @throws Exception When requests are made with insufficient gpu compared to cpu.
     */
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegistrationRequestModel request, HttpServletRequest requested) throws Exception {

        try {
            String description = request.getDescription();
            Resources resources = new Resources(request.getMem(), request.getCpu(), request.getGpu());
            String owner = authManager.getNetId();
            String facultyName = request.getFacultyName();

            String authorizationHeader = requested.getHeader(AUTHORIZATION_HEADER);
            String token = authorizationHeader.split(" ")[1];

            Resources availableResources = getFacultyResourcesByName(facultyName, token);
            Resources availableResourcesFRP = getFacultyResourcesByName("Free pool", token);

            String deadlineStr = request.getDeadline();//convert to Calendar immediately
            Calendar deadline = Calendar.getInstance();
            deadline.set(Calendar.YEAR, Integer.parseInt(deadlineStr.split("-")[2]));
            deadline.set(Calendar.MONTH, Integer.parseInt(deadlineStr.split("-")[1]));
            deadline.set(Calendar.DAY_OF_MONTH, Integer.parseInt(deadlineStr.split("-")[0]));

            registrationService.registerRequest(description, resources, owner, facultyName, availableResources, deadline, availableResourcesFRP);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

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

        String[] responseArr = response.getBody().split("-");
        Resources resources = new Resources(Integer.parseInt(responseArr[0]), Integer.parseInt(responseArr[1]), Integer.parseInt(responseArr[2]));

//        Resources availableResources = restTemplate.postForObject("", request, );
        return resources;
    }

    /**
     * Gets requests by id.
     *
     * @return the requests found in the database with the given id
     */
    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello " + authManager.getNetId());
    }


}
