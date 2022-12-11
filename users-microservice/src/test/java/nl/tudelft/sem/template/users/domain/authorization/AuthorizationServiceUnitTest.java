package nl.tudelft.sem.template.users.domain.authorization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.users.domain.AuthorizationService;
import nl.tudelft.sem.template.users.domain.Employee;
import nl.tudelft.sem.template.users.domain.EmployeeRepository;
import nl.tudelft.sem.template.users.domain.FacultyAccount;
import nl.tudelft.sem.template.users.domain.FacultyAccountRepository;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
import nl.tudelft.sem.template.users.domain.Sysadmin;
import nl.tudelft.sem.template.users.domain.SysadminRepository;
import nl.tudelft.sem.template.users.domain.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthorizationServiceUnitTest {
    private SysadminRepository sysadminRepository;
    private EmployeeRepository employeeRepository;
    private FacultyAccountRepository facultyAccountRepository;
    private AuthorizationService sut;
    private Sysadmin admin;
    private Employee employee;
    private FacultyAccount facultyAccount;
    private final String adminNetId = "admin";
    private final String employeeNetId = "ivo";
    private final String facultyNetId = "math";
    private final int facultyNumber = 0;

    @BeforeEach
    void setup() {
        sysadminRepository = mock(SysadminRepository.class);
        employeeRepository = mock(EmployeeRepository.class);
        facultyAccountRepository = mock(FacultyAccountRepository.class);
        sut = new AuthorizationService(sysadminRepository, employeeRepository, facultyAccountRepository);

        admin = new Sysadmin(adminNetId);
        employee = new Employee(employeeNetId);
        facultyAccount = new FacultyAccount(facultyNetId, facultyNumber);

        when(sysadminRepository.existsByNetId(adminNetId)).thenReturn(true);
        when(employeeRepository.existsByNetId(employeeNetId)).thenReturn(true);
        when(facultyAccountRepository.existsByNetId(facultyNetId)).thenReturn(true);
    }

    @Test
    public void isSysadminTest() {
        when(sysadminRepository.existsByNetId(adminNetId)).thenReturn(true);
        when(sysadminRepository.existsByNetId(employeeNetId)).thenReturn(false);
        assertThat(sut.isSysadmin(adminNetId)).isTrue();
        assertThat(sut.isSysadmin(employeeNetId)).isFalse();
    }

    @Test
    public void isEmployeeTest() {
        when(employeeRepository.existsByNetId(employeeNetId)).thenReturn(true);
        when(employeeRepository.existsByNetId(adminNetId)).thenReturn(false);
        assertThat(sut.isEmployee(employeeNetId)).isTrue();
        assertThat(sut.isEmployee(adminNetId)).isFalse();
    }

    @Test
    public void isFacultyAccountTest() {
        when(facultyAccountRepository.existsByNetId(facultyNetId)).thenReturn(true);
        when(facultyAccountRepository.existsByNetId(employeeNetId)).thenReturn(false);
        assertThat(sut.isFacultyAccount(facultyNetId)).isTrue();
        assertThat(sut.isFacultyAccount(employeeNetId)).isFalse();
    }

    @Test
    public void addSysadminTest() {
        sut.addSysadmin(adminNetId);
        verify(sysadminRepository).save(admin);
    }

    @Test
    public void addEmployeeTest() {
        sut.addEmployee(employeeNetId);
        verify(employeeRepository).save(employee);
    }

    @Test
    public void addFacultyAccountTest() {
        sut.addFacultyAccount(facultyNetId, facultyNumber);
        verify(facultyAccountRepository).save(facultyAccount);
    }

    @Test
    public void dropEmployeeTest() {
        //check for a non-existent employee
        when(employeeRepository.existsByNetId(adminNetId)).thenReturn(false);
        assertThat(sut.dropEmployee(adminNetId)).isFalse();
        verify(employeeRepository, never()).deleteByNetId(adminNetId);

        //check if an existing employee is deleted.
        when(employeeRepository.existsByNetId(employeeNetId)).thenReturn(true);
        assertThat(sut.dropEmployee(employeeNetId)).isTrue();
        verify(employeeRepository).deleteByNetId(employeeNetId);
    }

    @Test
    public void checkAccessNormalFlowTest() {
        try {
            assertThat(sut.checkAccess(adminNetId)).isEqualTo(Sysadmin.class.getSimpleName());
            assertThat(sut.checkAccess(employeeNetId)).isEqualTo(Employee.class.getSimpleName());
            assertThat(sut.checkAccess(facultyNetId)).isEqualTo(FacultyAccount.class.getSimpleName());
        } catch (Exception e) {
            fail("An exception was thrown");
        }

    }

    @Test
    public void checkAccessExceptionsTest() {
        when(sysadminRepository.existsByNetId(employeeNetId)).thenReturn(true);

        //multiple roles
        Exception result1 = assertThrows(Exception.class, () -> sut.checkAccess(employeeNetId));
        assertEquals("User with multiple roles!!!", result1.getMessage());

        //no user found
        String testNetId = "nonExistent";
        NoSuchUserException result2 = assertThrows(NoSuchUserException.class, () -> sut.checkAccess(testNetId));
        assertEquals("User (" + testNetId + ") was not registered.", result2.getMessage());
    }

    @Test
    public void atLeastTwoTest() {
        String test1 = "test1";
        when(sysadminRepository.existsByNetId(test1)).thenReturn(true);
        when(facultyAccountRepository.existsByNetId(test1)).thenReturn(true);

        Exception result1 = assertThrows(Exception.class, () -> sut.checkAccess(test1), "User with multiple roles!!!");
        assertEquals("User with multiple roles!!!", result1.getMessage());

        String test2 = "test2";
        when(sysadminRepository.existsByNetId(test2)).thenReturn(true);
        when(employeeRepository.existsByNetId(test2)).thenReturn(true);
        Exception result2 = assertThrows(Exception.class, () -> sut.checkAccess(test2), "User with multiple roles!!!");
        assertEquals("User with multiple roles!!!", result2.getMessage());


        String test3 = "test3";
        when(facultyAccountRepository.existsByNetId(test3)).thenReturn(true);
        when(employeeRepository.existsByNetId(test3)).thenReturn(true);
        Exception result3 = assertThrows(Exception.class, () -> sut.checkAccess(test3), "User with multiple roles!!!");
        assertEquals("User with multiple roles!!!", result3.getMessage());

        String test4 = "test4";
        when(sysadminRepository.existsByNetId(test4)).thenReturn(true);
        when(facultyAccountRepository.existsByNetId(test4)).thenReturn(true);
        when(employeeRepository.existsByNetId(test4)).thenReturn(true);
        Exception result4 = assertThrows(Exception.class, () -> sut.checkAccess(test4), "User with multiple roles!!!");
        assertEquals("User with multiple roles!!!", result4.getMessage());
    }

    @Test
    public void promoteEmployeeToSysadminNormalFlow() {
        try {
            sut.promoteEmployeeToSysadmin(adminNetId, employeeNetId);
            verify(employeeRepository).deleteByNetId(employeeNetId);

            Sysadmin promoted = new Sysadmin(employeeNetId);
            verify(sysadminRepository).save(promoted);
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
}
