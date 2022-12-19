package nl.tudelft.sem.template.users.controllers;

import java.util.Set;
import lombok.AllArgsConstructor;
import nl.tudelft.sem.template.users.authentication.AuthManager;
import nl.tudelft.sem.template.users.authentication.JwtRequestFilter;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.AccountType;
import nl.tudelft.sem.template.users.domain.EmployeeService;
import nl.tudelft.sem.template.users.domain.FacultyAccountService;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
import nl.tudelft.sem.template.users.domain.PromotionAndEmploymentService;
import nl.tudelft.sem.template.users.domain.RegistrationService;
import nl.tudelft.sem.template.users.domain.Sysadmin;
import nl.tudelft.sem.template.users.domain.User;
import nl.tudelft.sem.template.users.models.CheckAccessResponseModel;
import nl.tudelft.sem.template.users.models.FacultyCreationRequestModel;
import nl.tudelft.sem.template.users.models.FacultyAssignmentRequestModel;
import nl.tudelft.sem.template.users.models.PromotionRequestModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


/**
 * Controller for the user microservice.
 */
@RestController
@AllArgsConstructor
public class UsersController {

    private final transient AuthManager authentication;
    private final transient AuthorizationManager authorization;
    private final transient PromotionAndEmploymentService promotionAndEmploymentService;
    private final transient RegistrationService registrationService;

    private final transient EmployeeService employeeService;

    private final transient  FacultyAccountService facultyAccountService;

    /**
     * Adds a new user as an admin if their netId is "admin", else
     * Adds the user as an Employee.
     *
     * @return whether the request was successful
     */
    @GetMapping("/newUser")
    public ResponseEntity<String> newUserCreated() {
        User added = registrationService.registerUser(authentication.getNetId());
        if (added.getClass().equals(Sysadmin.class)) {
            return ResponseEntity.ok("User (" + authentication.getNetId() + ") was added as a Sysadmin.");
        }
        return ResponseEntity.ok("User (" + authentication.getNetId() + ") was added as an Employee.");
    }

    /**
     * Assign a user to a faculty.
     *
     * @param request the request body with a given model
     * @return a HTTP response
     */
    @PostMapping("/assignFaculty")
    public ResponseEntity<String> assignFacultyToEmployee(@RequestBody FacultyAssignmentRequestModel request) {
        String employer = request.getNetId();
        String employee = authentication.getNetId();
        try {
            Set<Long> facultyIds = employeeService.parseJsonFacultyIds(request.getFacultyIds());
            Set<Long> assignedFaculties = employeeService
                    .authenticateEmploymentAssignmentRequest(employer, employee, facultyIds);
            if (assignedFaculties.size() > 1) {
                return ResponseEntity.ok("User (" + employer
                        + ") was assigned to the following faculties: " + assignedFaculties);
            } else if (assignedFaculties.size() == 1) {
                return ResponseEntity.ok("User (" + employer
                        + ") was assigned to faculty: " + assignedFaculties);
            } else {
                throw new Exception("Cannot return an empty set of assigned faculties");
            }
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Remove a user from a faculty.
     *
     * @param request the request body with a given model
     * @return a HTTP response
     */
    @PostMapping("/removeFaculty")
    public ResponseEntity<String> removeFacultyEmployee(@RequestBody FacultyAssignmentRequestModel request) {
        String employer = request.getNetId();
        String employee = authentication.getNetId();
        try {
            Set<Long> facultyIds = employeeService.parseJsonFacultyIds(request.getFacultyIds());
            Set<Long> assignedFaculties = employeeService
                    .authenticateEmploymentAssignmentRequest(employer, employee, facultyIds);
            if (assignedFaculties.size() > 1) {
                return ResponseEntity.ok("User (" + employer
                        + ") was removed from the following faculties: " + assignedFaculties);
            } else if (assignedFaculties.size() == 1) {
                return ResponseEntity.ok("User (" + employer
                        + ") was removed from faculty: " + assignedFaculties);
            } else {
                throw new Exception("Cannot return an empty set of removed faculties");
            }
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Promotes an Employee to a Sysadmin.
     *
     * @param request the promotion request body
     * @return whether the request was successful
     * @throws Exception if the promoter is unauthorized or such employee does not exist
     */
    @PostMapping("/promoteToSysadmin")
    public ResponseEntity<String> promoteEmployeeToSysadmin(@RequestBody PromotionRequestModel request)
            throws Exception {
        try {
            String toBePromoted = request.getNetId();
            String promoter = authentication.getNetId();
            promotionAndEmploymentService.promoteEmployeeToSysadmin(promoter, toBePromoted);
            return ResponseEntity.ok("User (" + toBePromoted + ") was promoted to a Sysadmin");
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Request for checking the access of a User.
     *
     * @return whether the request was successful
     * @throws Exception if a user has multiple roles.
     */
    @GetMapping("/checkAccess")
    public ResponseEntity<CheckAccessResponseModel> checkUserAccess() throws Exception {
        try {
            String netId = authentication.getNetId();
            AccountType result = authorization.checkAccess(netId);
            return ResponseEntity.ok(new CheckAccessResponseModel(result.getName()));
        } catch (NoSuchUserException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Request for creating a new faculty.
     *
     * @param request the faculty creation request
     * @return whether the request was successful.
     */
    @PostMapping("/createFaculty")
    public ResponseEntity<String> createFaculty(@RequestBody FacultyCreationRequestModel request) {
        String authorNetId = authentication.getNetId();
        String managerNetId = request.getManagerNetId();
        String facultyName = request.getName();
        String token = JwtRequestFilter.token;
        System.out.println(token);
        try {
            promotionAndEmploymentService.createFaculty(authorNetId, managerNetId, facultyName, token);
            return ResponseEntity.ok("Faculty (" + facultyName + "), managed by (" + managerNetId + ") was created");
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
