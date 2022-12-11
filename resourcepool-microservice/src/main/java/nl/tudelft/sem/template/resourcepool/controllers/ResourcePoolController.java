package nl.tudelft.sem.template.resourcepool.controllers;

import nl.tudelft.sem.template.resourcepool.authentication.AuthManager;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.Faculty;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.NameAlreadyInUseException;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.RpFacultyRepository;
import nl.tudelft.sem.template.resourcepool.models.FacultyRegistrationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Hello World resourcepool controller.
 * <p>
 * This controller shows how you can extract information from the JWT token.
 * </p>
 */
@RestController
public class ResourcePoolController {

    private final transient AuthManager authManager;
    private final RpFacultyRepository rpFacultyRepository;

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public ResourcePoolController(AuthManager authManager,
                                  RpFacultyRepository rpFacultyRepository) {
        this.authManager = authManager;
        this.rpFacultyRepository = rpFacultyRepository;
    }

    /**
     * Gets resourcepool by id.
     *
     * @return the resourcepool found in the database with the given id
     */
    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello " + authManager.getNetId());
    }

    /**
     * Endpoint for creating a faculty.
     *
     * @param request The faculty registration model
     * @return 200 OK if the creation is successful
     * @throws Exception if a faculty with this name already exists
     */
    @PostMapping("/createFaculty")
    public ResponseEntity createFaculty(@RequestBody FacultyRegistrationRequestModel request) throws Exception {
        try {
            String name = request.getName();
            long managerId = request.getManagerId();
            if (rpFacultyRepository.existsByName(name)) {
                throw new NameAlreadyInUseException(name);
            }
            Faculty faculty = new Faculty(1L, name, managerId);
            rpFacultyRepository.save(faculty);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }
}
