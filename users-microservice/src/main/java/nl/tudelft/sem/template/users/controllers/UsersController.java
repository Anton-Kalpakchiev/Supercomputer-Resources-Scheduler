package nl.tudelft.sem.template.users.controllers;

import java.util.Set;
import lombok.AllArgsConstructor;
import nl.tudelft.sem.template.users.authentication.AuthManager;
import nl.tudelft.sem.template.users.authentication.JwtRequestFilter;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.AccountType;
import nl.tudelft.sem.template.users.domain.EmployeeService;
import nl.tudelft.sem.template.users.domain.EmploymentException;
import nl.tudelft.sem.template.users.domain.FacultyAccountService;
import nl.tudelft.sem.template.users.domain.FacultyException;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
import nl.tudelft.sem.template.users.domain.PromotionAndEmploymentService;
import nl.tudelft.sem.template.users.domain.RegistrationService;
import nl.tudelft.sem.template.users.domain.Sysadmin;
import nl.tudelft.sem.template.users.domain.User;
import nl.tudelft.sem.template.users.models.CheckAccessResponseModel;
import nl.tudelft.sem.template.users.models.FacultyAssignmentRequestModel;
import nl.tudelft.sem.template.users.models.FacultyCreationRequestModel;
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

    private final transient FacultyAccountService facultyAccountService;

    /**
     * Adds a new user as an admin if their netId is "admin", else
     * Adds the user as an Employee.
     *
     * @return whether the request was successful
     */
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
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
    @SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.AvoidLiteralsInIfCondition"})
    @PostMapping("/hireEmployee")
    public ResponseEntity<String> assignFacultyToEmployee(@RequestBody FacultyAssignmentRequestModel request) {
        String employee = request.getNetId();
        String employer = authentication.getNetId();
        String token = JwtRequestFilter.token;
        try {
            Set<Long> facultyIds = promotionAndEmploymentService.parseJsonFacultyIds(request.getFacultyIds());
            Set<Long> assignedFaculties = promotionAndEmploymentService
                    .authorizeEmploymentAssignmentRequest(employer, employee, facultyIds, token);
            if (assignedFaculties.size() > 1) {
                return ResponseEntity.ok("User (" + employee
                        + ") was assigned to the following faculties: " + assignedFaculties);
            } else if (assignedFaculties.size() == 1) {
                return ResponseEntity.ok("User (" + employee
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
    @SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.AvoidLiteralsInIfCondition"})
    @PostMapping("/terminateEmployee")
    public ResponseEntity<String> removeFacultyFromEmployee(@RequestBody FacultyAssignmentRequestModel request) {
        String employee = request.getNetId();
        String employer = authentication.getNetId();
        String token = JwtRequestFilter.token;
        try {
            Set<Long> facultyIds = promotionAndEmploymentService.parseJsonFacultyIds(request.getFacultyIds());
            Set<Long> assignedFaculties = promotionAndEmploymentService
                    .authorizeEmploymentRemovalRequest(employer, employee, facultyIds, token);
            if (assignedFaculties.size() > 1) {
                return ResponseEntity.ok("User (" + employee
                        + ") was removed from the following faculties: " + assignedFaculties);
            } else if (assignedFaculties.size() == 1) {
                return ResponseEntity.ok("User (" + employee
                        + ") was removed from faculty: " + assignedFaculties);
            } else {
                throw new EmploymentException("Cannot return an empty set of removed faculties");
            }
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EmploymentException | NoSuchUserException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Promotes an Employee to a Sysadmin.
     *
     * @param request the promotion request body
     * @return whether the request was successful
     */
    @PostMapping("/promoteToSysadmin")
    public ResponseEntity<String> promoteEmployeeToSysadmin(@RequestBody PromotionRequestModel request) {
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
     */
    @GetMapping("/checkAccess")
    public ResponseEntity<CheckAccessResponseModel> checkUserAccess() {
        try {
            String netId = authentication.getNetId();
            AccountType result = authorization.checkAccess(netId);
            return ResponseEntity.ok(new CheckAccessResponseModel(result.getName()));
        } catch (NoSuchUserException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Request for checking the access of a User.
     *
     * @return whether the request was successful
     */
    @PostMapping("/getFacultyIdForManager")
    public ResponseEntity<Long> getFacultyIdForManager() {
        try {
            String netId = authentication.getNetId();
            Long result = facultyAccountService.getFacultyAssignedId(netId);
            return ResponseEntity.ok(result);
        } catch (NoSuchUserException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
