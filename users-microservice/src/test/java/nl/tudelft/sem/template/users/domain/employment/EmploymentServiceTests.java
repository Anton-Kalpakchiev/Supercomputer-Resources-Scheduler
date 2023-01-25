package nl.tudelft.sem.template.users.domain.employment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.Set;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.domain.Employee;
import nl.tudelft.sem.template.users.domain.EmployeeRepository;
import nl.tudelft.sem.template.users.domain.EmployeeService;
import nl.tudelft.sem.template.users.domain.FacultyAccountRepository;
import nl.tudelft.sem.template.users.domain.FacultyAccountService;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
import nl.tudelft.sem.template.users.domain.SysadminRepository;
import nl.tudelft.sem.template.users.models.ResourcesDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class EmploymentServiceTests {

    private EmployeeRepository employeeRepository;
    private EmployeeService employeeService;
    private RestTemplate mockRestTemplate;

    @BeforeEach
    void setup() {
        SysadminRepository sysadminRepository = mock(SysadminRepository.class);
        employeeRepository = mock(EmployeeRepository.class);
        FacultyAccountRepository facultyAccountRepository = mock(FacultyAccountRepository.class);
        FacultyAccountService facultyAccountService = new FacultyAccountService(facultyAccountRepository);
        AuthorizationManager authorizationManager = new AuthorizationManager(
                sysadminRepository, employeeRepository, facultyAccountRepository);
        mockRestTemplate = mock(RestTemplate.class);
        employeeService = new EmployeeService(employeeRepository, authorizationManager, facultyAccountService);
    }

    @Test
    void testGetParentFacultyIdEmpty() throws NoSuchUserException {
        Employee employee = new Employee("Mayte");
        when(employeeRepository.findByNetId("Mayte")).thenReturn(Optional.of(employee));
        assertThat(employeeService.getParentFacultyId("Mayte")).isEqualTo(Set.of());
    }

    @Test
    void testGetParentFacultyIdNoSuchUser() {
        when(employeeRepository.findByNetId("Mayte")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> {
            employeeService.getParentFacultyId("Mayte");
        }).isInstanceOf(NoSuchUserException.class);
    }

    @Test
    void testGetParentFacultyEmployee() throws NoSuchUserException {
        Employee employee = new Employee("Mayte", Set.of(6L));
        when(employeeRepository.findByNetId("Mayte")).thenReturn(Optional.of(employee));
        assertThat(employeeService.getParentFacultyId("Mayte")).isEqualTo(Set.of(6L));
    }

    @Test
    void getEmployeeTest() throws NoSuchUserException {
        Employee employee = new Employee("Mayte", Set.of(6L));
        when(employeeRepository.findByNetId("Mayte")).thenReturn(Optional.of(employee));
        assertThat(employeeService.getEmployee("Mayte")).isEqualTo(employee);
    }

    @Test
    void getEmployeeNonExistent() {
        when(employeeRepository.findByNetId("Mayte")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> {
            employeeService.getEmployee("Mayte");
        }).isInstanceOf(NoSuchUserException.class);
    }

    @Test
    void getResourcesForTomorrowTest() throws NoSuchUserException, JsonProcessingException {
        String token = "Blablabla";

        Employee employee = new Employee("Mayte", Set.of(6L));
        when(employeeRepository.findByNetId("Mayte")).thenReturn(Optional.of(employee));

        Set<Long> facultyIds = Set.of(6L);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + token);

        String requestBody = "{\"resourcePoolId\": \"" + facultyIds + "\"}";
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResourcesDto expected = new ResourcesDto(50, 50, 50);
        ObjectMapper objectMapper = new ObjectMapper();


        when(mockRestTemplate.postForEntity("http://localhost:8085/availableFacultyResources",
                request, String.class))
                .thenReturn(ResponseEntity.of(Optional.of(objectMapper.writeValueAsString(expected))));

        assertThat(employeeService
                .getResourcesForTomorrow("Mayte", token, mockRestTemplate)).isEqualTo(expected);

        verify(mockRestTemplate).postForEntity("http://localhost:8085/availableFacultyResources",
                request, String.class);
    }
}
