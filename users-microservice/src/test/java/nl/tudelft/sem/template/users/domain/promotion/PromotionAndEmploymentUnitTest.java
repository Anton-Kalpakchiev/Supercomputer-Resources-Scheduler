package nl.tudelft.sem.template.users.domain.promotion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Optional;
import java.util.Set;
import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.authorization.UnauthorizedException;
import nl.tudelft.sem.template.users.domain.AccountType;
import nl.tudelft.sem.template.users.domain.Employee;
import nl.tudelft.sem.template.users.domain.EmployeeRepository;
import nl.tudelft.sem.template.users.domain.EmploymentException;
import nl.tudelft.sem.template.users.domain.FacultyAccount;
import nl.tudelft.sem.template.users.domain.FacultyAccountRepository;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
import nl.tudelft.sem.template.users.domain.PromotionAndEmploymentService;
import nl.tudelft.sem.template.users.domain.RegistrationService;
import nl.tudelft.sem.template.users.domain.Sysadmin;
import nl.tudelft.sem.template.users.domain.SysadminRepository;
import nl.tudelft.sem.template.users.models.FacultyCreationResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

public class PromotionAndEmploymentUnitTest {
    private SysadminRepository sysadminRepository;
    private EmployeeRepository employeeRepository;
    private FacultyAccountRepository facultyAccountRepository;
    private RestTemplate restTemplate;
    private RegistrationService registrationService;
    private AuthorizationManager authorization;
    private MockRestServiceServer mockRestServiceServer;
    private PromotionAndEmploymentService sut;

    private Sysadmin admin;
    private Employee employee;
    private FacultyAccount facultyAccount;
    private final String adminNetId = "admin";
    private final String employeeNetId = "ivo";
    private final long facultyId = 6L;
    private final String facultyNetId = "professor";
    private final String facultyName = "math";

    @Captor
    private ArgumentCaptor<Employee> employeeArgumentCaptor;


    @BeforeEach
    void setup() throws Exception {
        sysadminRepository = mock(SysadminRepository.class);
        employeeRepository = mock(EmployeeRepository.class);
        facultyAccountRepository = mock(FacultyAccountRepository.class);
        restTemplate = new RestTemplate();
        registrationService = mock(RegistrationService.class);
        authorization = mock(AuthorizationManager.class);
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);

        sut = new PromotionAndEmploymentService(sysadminRepository, employeeRepository,
                facultyAccountRepository);

        admin = new Sysadmin(adminNetId);
        employee = new Employee(employeeNetId);
        facultyAccount = new FacultyAccount(facultyNetId, facultyId);
        employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);

        when(authorization.isOfType(adminNetId, AccountType.SYSADMIN)).thenReturn(true);
        when(authorization.isOfType(employeeNetId, AccountType.EMPLOYEE)).thenReturn(true);
        when(authorization.isOfType(facultyNetId, AccountType.FAC_ACCOUNT)).thenReturn(true);
    }

    @Test
    public void promoteEmployeeToSysadminNormalFlow() {
        try {
            sut.promoteEmployeeToSysadmin(adminNetId, employeeNetId);
            verify(registrationService).dropEmployee(employeeNetId);
            verify(registrationService).addSysadmin(employeeNetId);
        } catch (Exception e) {
            fail("An exception was thrown.");
        }
    }

    @Test
    public void promoteEmployeeToSysadminExceptions() {
        UnauthorizedException result3 = assertThrows(UnauthorizedException.class,
                () -> sut.promoteEmployeeToSysadmin(facultyNetId, employeeNetId));
        assertEquals("User (" + facultyNetId + ") is not a Sysadmin => can not promote", result3.getMessage());
        UnauthorizedException result4 = assertThrows(UnauthorizedException.class,
                () -> sut.promoteEmployeeToSysadmin(employeeNetId, employeeNetId));
        assertEquals("User (" + employeeNetId + ") is not a Sysadmin => can not promote", result4.getMessage());


        NoSuchUserException result1 = assertThrows(NoSuchUserException.class,
                () -> sut.promoteEmployeeToSysadmin(adminNetId, adminNetId));
        assertEquals("No such employee: " + adminNetId, result1.getMessage());

        NoSuchUserException result2 = assertThrows(NoSuchUserException.class,
                () -> sut.promoteEmployeeToSysadmin(adminNetId, facultyNetId));
        assertEquals("No such employee: " + facultyNetId, result2.getMessage());
    }

    @Test
    void jsonParsedCorrectly() throws EmploymentException {
        String facultyIds = "6, 7, 8";
        assertThat(sut.parseJsonFacultyIds(facultyIds)).isEqualTo(Set.of(6L, 7L, 8L));
    }

    @Test
    void jsonParsedIncorrectly() {
        String facultyIds = "6 7, 8";
        assertThrows(EmploymentException.class, () -> sut.parseJsonFacultyIds(facultyIds));
    }

    @Test
    void jsonParsedIncorrectlyNone() {
        String facultyIds = "None";
        assertThrows(EmploymentException.class, () -> sut.parseJsonFacultyIds(facultyIds));
    }

    @Test
    void assignFacultyToEmployeeNotFound() {
        when(employeeRepository.findByNetId(employeeNetId)).thenReturn(Optional.empty());
        assertThrows(NoSuchUserException.class, () -> sut.assignFacultyToEmployee(employeeNetId, facultyId));
    }

    @Test
    void removeFacultyFromEmployeeNotFound() {
        when(employeeRepository.findByNetId(employeeNetId)).thenReturn(Optional.empty());
        assertThrows(NoSuchUserException.class, () -> sut.removeEmployeeFromFaculty(employeeNetId, facultyId));
    }

    @Test
    void assignFacultyToEmployeeSuccessful() throws NoSuchUserException, EmploymentException {
        when(employeeRepository.findByNetId(employeeNetId)).thenReturn(Optional.of(employee));
        Set<Long> result = Set.of(6L);
        sut.assignFacultyToEmployee(employeeNetId, facultyId);
        verify(employeeRepository).saveAndFlush(employeeArgumentCaptor.capture());
        assertThat(employeeArgumentCaptor.getValue().getParentFacultyIds()).isEqualTo(result);
    }

    @Test
    void removeFacultyFromEmployeeSuccessful() throws NoSuchUserException, EmploymentException {
        Employee employedEmployee = new Employee(employeeNetId, Set.of(facultyId));
        when(employeeRepository.findByNetId(employeeNetId)).thenReturn(Optional.of(employedEmployee));
        Set<Long> result = Set.of();
        sut.removeEmployeeFromFaculty(employeeNetId, facultyId);
        verify(employeeRepository).saveAndFlush(employeeArgumentCaptor.capture());
        assertThat(employeeArgumentCaptor.getValue().getParentFacultyIds()).isEqualTo(result);
    }

    @Test
    void assignFacultyToEmployeeDuplicate() throws NoSuchUserException, EmploymentException {
        when(employeeRepository.findByNetId(employeeNetId)).thenReturn(Optional.of(employee));
        Set<Long> result = Set.of(6L);
        sut.assignFacultyToEmployee(employeeNetId, facultyId);
        assertThrows(EmploymentException.class, () -> sut.assignFacultyToEmployee(employeeNetId, facultyId));
    }


    @Test
    public void createFacultyNormalFlow() {
        try {
            mockRestServiceServer.expect(requestTo("http://localhost:8085/createFaculty"))
                    .andRespond(withSuccess("{\"facultyId\": \"" + facultyId + "\"}", MediaType.APPLICATION_JSON));

            long expected = sut.createFaculty(adminNetId, employeeNetId, facultyName, sampleToken);
            assertThat(expected).isEqualTo(facultyId);

            verify(registrationService).dropEmployee(employeeNetId);
            verify(registrationService).addFacultyAccount(employeeNetId, facultyId);
        } catch (Exception e) {
            fail("An exception was thrown.");
        }
    }

    @Test
    public void createFacultyExceptionsUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> {
            sut.createFaculty(facultyNetId, employeeNetId, facultyName, sampleToken);
        });
    }

    @Test
    public void createFacultyExceptionsNoSuchUser() {
        assertThrows(NoSuchUserException.class, () -> {
            sut.createFaculty(adminNetId, facultyNetId, facultyName, sampleToken);
        });
    }

    @Test
    public void createFacultyExceptionsCanNotSendRequest() {
        mockRestServiceServer.expect(requestTo("http://localhost:8085/createFaculty"))
                .andRespond(withBadRequest());
        assertThrows(Exception.class, () -> {
            sut.createFaculty(adminNetId, employeeNetId, facultyName, sampleToken);
        });
    }
}
