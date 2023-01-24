package nl.tudelft.sem.template.users.domain.promotion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import nl.tudelft.sem.template.users.domain.FacultyAccountService;
import nl.tudelft.sem.template.users.domain.FacultyException;
import nl.tudelft.sem.template.users.domain.FacultyVerificationService;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
import nl.tudelft.sem.template.users.domain.PromotionAndEmploymentService;
import nl.tudelft.sem.template.users.domain.RegistrationService;
import nl.tudelft.sem.template.users.domain.Sysadmin;
import nl.tudelft.sem.template.users.domain.SysadminRepository;
import nl.tudelft.sem.template.users.domain.UserServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

public class PromotionAndEmploymentUnitTest {
    private transient SysadminRepository sysadminRepository;
    private transient EmployeeRepository employeeRepository;
    private transient FacultyAccountRepository facultyAccountRepository;
    private transient RestTemplate restTemplate;
    private transient RegistrationService registrationService;

    private transient FacultyAccountService facultyAccountService;
    private transient AuthorizationManager authorization;
    private transient MockRestServiceServer mockRestServiceServer;

    private transient UserServices userServices;
    private transient PromotionAndEmploymentService sut;

    private Sysadmin admin;
    private Employee employee;
    private FacultyAccount facultyAccount;
    private final String adminNetId = "admin";
    private final String employeeNetId = "ivo";
    private final String facultyNetId = "professor";
    private final String facultyName = "math";
    private final long facultyId = 6L;

    @Autowired
    private FacultyVerificationService facultyVerificationService;

    private final String sampleToken = "1234567";

    @Captor
    private ArgumentCaptor<Employee> employeeArgumentCaptor;

    @BeforeEach
    void setup() throws Exception {
        sysadminRepository = mock(SysadminRepository.class);
        employeeRepository = mock(EmployeeRepository.class);
        facultyAccountRepository = mock(FacultyAccountRepository.class);
        restTemplate = new RestTemplate();
        registrationService = mock(RegistrationService.class);
        facultyAccountService = mock(FacultyAccountService.class);
        facultyVerificationService = mock(FacultyVerificationService.class);
        authorization = mock(AuthorizationManager.class);
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);

        userServices = new UserServices(facultyAccountService, facultyVerificationService, registrationService);
        sut = new PromotionAndEmploymentService(employeeRepository, userServices, authorization);

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
    void removeFacultyFromEmployeeNotEmployed() {
        when(employeeRepository.findByNetId(employeeNetId)).thenReturn(Optional.of(employee));
        assertThrows(EmploymentException.class, () -> sut.removeEmployeeFromFaculty(employeeNetId, facultyId));
    }

    @Test
    void assignFacultyToEmployeeDuplicate() throws NoSuchUserException, EmploymentException {
        when(employeeRepository.findByNetId(employeeNetId)).thenReturn(Optional.of(employee));
        sut.assignFacultyToEmployee(employeeNetId, facultyId);
        assertThrows(EmploymentException.class, () -> sut.assignFacultyToEmployee(employeeNetId, facultyId));
    }

    @Test
    void authorizeEmploymentRequestFacultyNotFound() throws FacultyException {
        when(userServices.getFacultyVerificationService().verifyFaculty(facultyId, sampleToken))
                .thenThrow(FacultyException.class);
        assertThrows(FacultyException.class,
                () -> sut.authorizeEmploymentAssignmentRequest(facultyNetId, employeeNetId, Set.of(facultyId), sampleToken));
    }

    @Test
    void authorizeTerminationRequestFacultyNotFound() throws FacultyException {
        when(userServices.getFacultyVerificationService().verifyFaculty(facultyId, sampleToken))
                .thenThrow(FacultyException.class);
        assertThrows(FacultyException.class,
                () -> sut.authorizeEmploymentRemovalRequest(facultyNetId, employeeNetId, Set.of(facultyId), sampleToken));
    }

    @Test
    void authorizationHiringEmployeesSuccessful() throws FacultyException,
            NoSuchUserException, UnauthorizedException, EmploymentException {
        when(userServices.getFacultyVerificationService().verifyFaculty(facultyId, sampleToken)).thenReturn(true);
        when(employeeRepository.findByNetId(employeeNetId)).thenReturn(Optional.of(employee));
        when(userServices.getFacultyAccountService().getFacultyAssignedId(facultyNetId)).thenReturn(facultyId);
        Set<Long> expectedResult = Set.of(facultyId);
        Set<Long> result = sut.authorizeEmploymentAssignmentRequest(
                facultyNetId, employeeNetId, Set.of(facultyId), sampleToken);
        assertThat(result).isEqualTo(expectedResult);
        verify(employeeRepository).saveAndFlush(employeeArgumentCaptor.capture());
        assertThat(employeeArgumentCaptor.getValue().getParentFacultyIds()).isEqualTo(result);
    }

    @Test
    void authorizationFiringEmployeesSuccessful() throws FacultyException,
            NoSuchUserException, UnauthorizedException, EmploymentException {
        when(userServices.getFacultyVerificationService().verifyFaculty(facultyId, sampleToken)).thenReturn(true);
        Employee employedEmployee = new Employee(employeeNetId, Set.of(facultyId));
        when(employeeRepository.findByNetId(employeeNetId)).thenReturn(Optional.of(employedEmployee));
        when(userServices.getFacultyAccountService().getFacultyAssignedId(facultyNetId)).thenReturn(facultyId);

        Set<Long> result = sut.authorizeEmploymentRemovalRequest(
                facultyNetId, employeeNetId, Set.of(facultyId), sampleToken);

        verify(employeeRepository).saveAndFlush(employeeArgumentCaptor.capture());
        assertThat(employeeArgumentCaptor.getValue().getParentFacultyIds()).isEqualTo(Set.of());
        assertThat(result).isEqualTo(Set.of(facultyId));
    }

    @Test
    void authorizationHiringEmployeesWrongFacManager() throws FacultyException, NoSuchUserException {
        when(userServices.getFacultyVerificationService().verifyFaculty(facultyId, sampleToken)).thenReturn(true);
        when(userServices.getFacultyAccountService().getFacultyAssignedId(facultyNetId)).thenReturn(3L);
        assertThrows(EmploymentException.class,
                () -> sut.authorizeEmploymentAssignmentRequest(facultyNetId, employeeNetId, Set.of(facultyId), sampleToken));
    }

    @Test
    void authorizationFiringEmployeesWrongFacManager() throws FacultyException, NoSuchUserException {
        when(userServices.getFacultyVerificationService().verifyFaculty(facultyId, sampleToken)).thenReturn(true);
        when(userServices.getFacultyAccountService().getFacultyAssignedId(facultyNetId)).thenReturn(3L);
        assertThrows(EmploymentException.class,
                () -> sut.authorizeEmploymentRemovalRequest(facultyNetId, employeeNetId, Set.of(facultyId), sampleToken));
    }

    @Test
    void authorizationHiringEmployeesMultiple() throws NoSuchUserException, FacultyException {
        when(userServices.getFacultyVerificationService().verifyFaculty(facultyId, sampleToken)).thenReturn(true);
        when(userServices.getFacultyAccountService().getFacultyAssignedId(facultyNetId)).thenReturn(facultyId);
        assertThrows(EmploymentException.class,
                () -> sut.authorizeEmploymentAssignmentRequest(
                        facultyNetId, employeeNetId, Set.of(facultyId, 1L), sampleToken));
    }

    @Test
    void authorizationFiringEmployeesMultiple() throws NoSuchUserException, FacultyException {
        when(userServices.getFacultyVerificationService().verifyFaculty(facultyId, sampleToken)).thenReturn(true);
        when(userServices.getFacultyAccountService().getFacultyAssignedId(facultyNetId)).thenReturn(facultyId);
        assertThrows(EmploymentException.class,
                () -> sut.authorizeEmploymentRemovalRequest(
                        facultyNetId, employeeNetId, Set.of(facultyId, 1L), sampleToken));
    }

    @Test
    void authorizationHiringEmployeeByEmployee() throws FacultyException {
        when(userServices.getFacultyVerificationService().verifyFaculty(facultyId, sampleToken)).thenReturn(true);
        assertThrows(UnauthorizedException.class,
                () -> sut.authorizeEmploymentAssignmentRequest(
                        employeeNetId, employeeNetId, Set.of(facultyId, 1L), sampleToken));
    }

    @Test
    void authorizationFiringEmployeeByEmployee() throws FacultyException {
        when(userServices.getFacultyVerificationService().verifyFaculty(facultyId, sampleToken)).thenReturn(true);
        assertThrows(UnauthorizedException.class,
                () -> sut.authorizeEmploymentRemovalRequest(
                        employeeNetId, employeeNetId, Set.of(facultyId, 1L), sampleToken));
    }

}
