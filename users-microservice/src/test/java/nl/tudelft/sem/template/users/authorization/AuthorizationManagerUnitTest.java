package nl.tudelft.sem.template.users.authorization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.users.domain.AccountType;
import nl.tudelft.sem.template.users.domain.Employee;
import nl.tudelft.sem.template.users.domain.EmployeeRepository;
import nl.tudelft.sem.template.users.domain.FacultyAccount;
import nl.tudelft.sem.template.users.domain.FacultyAccountRepository;
import nl.tudelft.sem.template.users.domain.NoSuchUserException;
import nl.tudelft.sem.template.users.domain.Sysadmin;
import nl.tudelft.sem.template.users.domain.SysadminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthorizationManagerUnitTest {
    private SysadminRepository sysadminRepository;
    private EmployeeRepository employeeRepository;
    private FacultyAccountRepository facultyAccountRepository;
    private AuthorizationManager sut;
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
        sut = new AuthorizationManager(sysadminRepository, employeeRepository, facultyAccountRepository);

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
    public void checkAccessNormalFlowTest() {
        try {
            assertThat(sut.checkAccess(adminNetId)).isEqualTo(AccountType.SYSADMIN);
            assertThat(sut.checkAccess(employeeNetId)).isEqualTo(AccountType.EMPLOYEE);
            assertThat(sut.checkAccess(facultyNetId)).isEqualTo(AccountType.FAC_ACCOUNT);
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
    public void isOfTypeTest() {
        try {
            assertThat(sut.isOfType(adminNetId, AccountType.SYSADMIN)).isTrue();
            assertThat(sut.isOfType(adminNetId, AccountType.EMPLOYEE)).isFalse();
        } catch (Exception e) {
            fail("An exception was thrown");
        }
    }
}
