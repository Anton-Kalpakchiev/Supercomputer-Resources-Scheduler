package nl.tudelft.sem.template.resourcepool.controllers;

import nl.tudelft.sem.template.resourcepool.authentication.AuthManager;
import nl.tudelft.sem.template.resourcepool.domain.dailyschedule.DailySchedule;
import nl.tudelft.sem.template.resourcepool.domain.dailyschedule.DailyScheduleService;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.RpManagementService;
import nl.tudelft.sem.template.resourcepool.models.AutomaticApprovalModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;

import static nl.tudelft.sem.template.resourcepool.authentication.JwtRequestFilter.AUTHORIZATION_HEADER;

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

    @PostMapping("/automaticApproval")
    public ResponseEntity<Boolean> automaticApproval(@RequestBody AutomaticApprovalModel request, HttpServletRequest requested) {
        String dayString = request.getDay();
        String[] dayArr = dayString.split("-");//convert to Calendar immediately
        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, Integer.parseInt(dayArr[2]));
        day.set(Calendar.MONTH, Integer.parseInt(dayArr[1]));
        day.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayArr[0]));
        System.out.println("Pretty far: " + day.get(Calendar.DAY_OF_MONTH) + "-" + day.get(Calendar.MONTH) + "-" + day.get(Calendar.YEAR));
        String authorizationHeader = requested.getHeader(AUTHORIZATION_HEADER);
        String token = authorizationHeader.split(" ")[1];
        try {
            dailyScheduleService.scheduleFP(day, request.getRequestId(), token);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(true);
    }

}
