package nl.tudelft.sem.template.resourcepool.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nl.tudelft.sem.template.resourcepool.authentication.AuthManager;
import nl.tudelft.sem.template.resourcepool.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.resourcepool.domain.RequestService;
import nl.tudelft.sem.template.resourcepool.domain.dailyschedule.DailySchedule;
import nl.tudelft.sem.template.resourcepool.domain.dailyschedule.DailyScheduleService;
import nl.tudelft.sem.template.resourcepool.domain.dailyschedule.ScheduleRepository;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.ResourcePool;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.RpFacultyRepository;
import nl.tudelft.sem.template.resourcepool.domain.resourcepool.RpManagementService;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import nl.tudelft.sem.template.resourcepool.models.RequestTomorrowResourcesRequestModel;
import nl.tudelft.sem.template.resourcepool.models.ScheduleRequestModel;
import nl.tudelft.sem.template.resourcepool.models.ScheduleResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class DailyScheduleControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    private transient DailyScheduleService dailyScheduleService;

    private transient RpManagementService rpManagementService;

    private transient RequestService requestService;

    @MockBean
    private transient ScheduleRepository mockScheduleRepo;

    @MockBean
    private transient RpFacultyRepository mockFacultyRepo;

    private ResourcePool faculty;

    private DailySchedule schedule1;

    private DailySchedule schedule2;

    private Calendar day;
    private Calendar day2;

    private ScheduleResponseModel expected;

    @BeforeEach
    void setup() {
        requestService = new RequestService();
        rpManagementService = new RpManagementService(mockFacultyRepo);
        dailyScheduleService = new DailyScheduleService(
                mockScheduleRepo, rpManagementService, requestService, mockFacultyRepo);
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void setupObjects() {
        faculty = new ResourcePool("Resource Pool 0");
        faculty.setBaseResources(new Resources(100, 100, 100));
        faculty.setNodeResources(new Resources(20, 20, 20));

        day = Calendar.getInstance();
        day.setTime(day.getTime());
        schedule1 = new DailySchedule(day, faculty.getId());
        schedule1.addRequest(1);
        schedule1.addRequest(2);
        schedule1.addRequest(3);
        schedule1.setTotalResources(new Resources(120, 120, 120));
        schedule1.setAvailableResources(new Resources(0, 0, 0));

        day2 = Calendar.getInstance();
        day2.add(Calendar.DATE, 1);
        day2.setTime(day2.getTime());

        schedule2 = new DailySchedule(day2, faculty.getId());
        schedule2.addRequest(1);
        schedule2.addRequest(2);
        schedule2.setTotalResources(new Resources(50, 50, 50));
        schedule2.setAvailableResources(new Resources(10, 10, 10));

        Map<String, List<String>> map = new HashMap<>();
        map.put(faculty.getName(),
                Stream.of(schedule1, schedule2).map(DailySchedule::toPrettyString).collect(Collectors.toList()));
        expected = new ScheduleResponseModel(map);
    }

    @Test
    void getAllSchedulesTest() throws Exception {
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockFacultyRepo.findAll()).thenReturn(List.of(faculty));
        when(mockFacultyRepo.findById(faculty.getId())).thenReturn(Optional.of(faculty));
        when(mockScheduleRepo.findAllByResourcePoolId(faculty.getId())).thenReturn(List.of(schedule1, schedule2));

        // Act
        ResultActions result = mockMvc.perform(get("/getAllSchedules")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));
        // Assert
        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo(objectMapper.writeValueAsString(expected));
    }


    @Test
    void getFacultySchedulesTest() throws Exception {
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockFacultyRepo.findAll()).thenReturn(List.of(faculty));
        when(mockFacultyRepo.findById(faculty.getId())).thenReturn(Optional.of(faculty));
        when(mockScheduleRepo.findAllByResourcePoolId(faculty.getId())).thenReturn(List.of(schedule1, schedule2));

        // Act
        ResultActions result = mockMvc.perform(post("/getFacultySchedules")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(objectMapper.writeValueAsString(new ScheduleRequestModel(0L))));

        // Assert
        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo(objectMapper.writeValueAsString(expected));
    }

    @Test
    void getAvailableResourcesForTomorrowTest() throws Exception {
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockFacultyRepo.findAll()).thenReturn(List.of(faculty));
        when(mockFacultyRepo.findById(faculty.getId())).thenReturn(Optional.of(faculty));
        when(mockScheduleRepo.existsByDayAndResourcePoolId(any(Calendar.class), eq(faculty.getId())))
                .thenReturn(true);
        when(mockScheduleRepo.findByDayAndResourcePoolId(any(Calendar.class), eq(faculty.getId())))
                .thenReturn(Optional.of(schedule2));

        String expectedResources = objectMapper.writeValueAsString(schedule2.getAvailableResources());
        ResultActions result = mockMvc.perform(post("/availableFacultyResources")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(objectMapper.writeValueAsString(
                            new RequestTomorrowResourcesRequestModel(faculty.getId()))));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo(expectedResources);

    }

}
