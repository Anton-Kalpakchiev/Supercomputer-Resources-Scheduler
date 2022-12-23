package nl.tudelft.sem.template.resourcepool.controllers;

import static nl.tudelft.sem.template.resourcepool.authentication.JwtRequestFilter.AUTHORIZATION_HEADER;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.resourcepool.authentication.AuthManager;
import nl.tudelft.sem.template.resourcepool.domain.dailyschedule.DailyScheduleService;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import nl.tudelft.sem.template.resourcepool.models.AutomaticApprovalModel;
import nl.tudelft.sem.template.resourcepool.models.ReleaseResourcesRequestModel;
import nl.tudelft.sem.template.resourcepool.models.RequestTomorrowResourcesRequestModel;
import nl.tudelft.sem.template.resourcepool.models.ScheduleRequestModel;
import nl.tudelft.sem.template.resourcepool.models.ScheduleResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class DailyScheduleController {
    private final transient AuthManager authManager;

    private final transient DailyScheduleService dailyScheduleService;

    /**
     * Instantiates a new DailyScheduleController.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     * @param dailyScheduleService The service which will handle the business logic for managing the faculties
     */
    @Autowired
    public DailyScheduleController(AuthManager authManager, DailyScheduleService dailyScheduleService) {
        this.authManager = authManager;
        this.dailyScheduleService = dailyScheduleService;
    }

    /**
     * Automatically approves a request.
     *
     * @param request the body of the request.
     * @param requested to get the token.
     * @return true if the request was successfully approved.
     */
    @PostMapping("/automaticApproval")
    public ResponseEntity<Boolean> automaticApproval(@RequestBody AutomaticApprovalModel request,
                                                     HttpServletRequest requested) {
        String dayString = request.getDay();
        String[] dayArr = dayString.split("-"); //convert to Calendar immediately
        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, Integer.parseInt(dayArr[2]));
        day.set(Calendar.MONTH, Integer.parseInt(dayArr[1]));
        day.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayArr[0]));
        String authorizationHeader = requested.getHeader(AUTHORIZATION_HEADER);
        String token = authorizationHeader.split(" ")[1];
        try {
            dailyScheduleService.scheduleFp(day, request.getRequestId(), token);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(true);
    }

    /**
     * Retrieves the available resources for tomorrow of a given faculty.
     *
     * @param request the request body
     * @return the available resources of that faculty
     */
    @PostMapping("/availableFacultyResources")
    public ResponseEntity<Resources> getAvailableFacultyResources(
            @RequestBody RequestTomorrowResourcesRequestModel request) {
        long facultyId = request.getResourcePoolId();
        try {
            return ResponseEntity.ok(dailyScheduleService.getAvailableResourcesById(facultyId,
                    DailyScheduleService.getTomorrow()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Ends point to view all schedules available.
     *
     * @return a response with all the schedules
     */
    @GetMapping("/getAllSchedules")
    public ResponseEntity<ScheduleResponseModel> getAllSchedules() {
        try {
            ScheduleResponseModel response = new ScheduleResponseModel(dailyScheduleService.getAllSchedules());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    /**
     * Ends point to view all schedules available for a particular faculty.
     *
     * @return a response with all the schedules
     */
    @PostMapping("/getFacultySchedules")
    public ResponseEntity<ScheduleResponseModel> getFacultySchedules(@RequestBody ScheduleRequestModel request) {
        try {
            Map<String, List<String>> responseRaw = dailyScheduleService.getSchedulesPerFaculty(request.getFacultyId());
            System.out.println("it got to the other side");
            ScheduleResponseModel response = new ScheduleResponseModel(responseRaw);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * End point to release resources for a particular faculty and day into the free resource pool.
     *
     * @param request the request body
     * @return the response entity which is a string of the faculty name
     */
    @PostMapping("/releaseResources")
    public ResponseEntity<String> releaseResources(@RequestBody ReleaseResourcesRequestModel request) {
        try {
            System.out.println("we got to the other side!");
            dailyScheduleService.releaseResources(request.getDay(), request.getFacultyId());
            System.out.println("we managed to release the resources");
            String facultyName = dailyScheduleService.getFacultyName(request.getFacultyId());
            System.out.println("we returned the faculty name");
            return ResponseEntity.ok(facultyName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
