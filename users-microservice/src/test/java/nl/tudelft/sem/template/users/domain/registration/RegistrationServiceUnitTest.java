package nl.tudelft.sem.template.users.domain.registration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.users.domain.Employee;
import nl.tudelft.sem.template.users.domain.EmployeeRepository;
import nl.tudelft.sem.template.users.domain.FacultyAccount;
import nl.tudelft.sem.template.users.domain.FacultyAccountRepository;
import nl.tudelft.sem.template.users.domain.RegistrationService;
import nl.tudelft.sem.template.users.domain.Sysadmin;
import nl.tudelft.sem.template.users.domain.SysadminRepository;
import nl.tudelft.sem.template.users.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegistrationServiceUnitTest {
    private SysadminRepository sysadminRepository;
    private EmployeeRepository employeeRepository;
    private FacultyAccountRepository facultyAccountRepository;
    private RegistrationService sut;
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
        sut = new RegistrationService(sysadminRepository,
                employeeRepository, facultyAccountRepository);

        admin = new Sysadmin(adminNetId);
        employee = new Employee(employeeNetId);
        facultyAccount = new FacultyAccount(facultyNetId, facultyNumber);
    }

    @Test
    public void registerAdminTest() {
        Sysadmin expected = new Sysadmin(adminNetId);
        User registered = sut.registerUser(adminNetId);

        verify(sysadminRepository).save(expected);
        assertThat(registered).isEqualTo(expected);
    }

    @Test
    public void registerEmployee() {
        Employee expected = new Employee(employeeNetId);
        User registered = sut.registerUser(employeeNetId);

        verify(employeeRepository).save(expected);
        assertThat(registered).isEqualTo(expected);
    }

    @Test
    public void addSysadminTest() {
        Sysadmin res = sut.addSysadmin(adminNetId);
        verify(sysadminRepository).save(admin);
        assertThat(res).isEqualTo(admin);
    }

    @Test
    public void addEmployeeTest() {
        Employee res = sut.addEmployee(employeeNetId);
        verify(employeeRepository).save(employee);
        assertThat(res).isEqualTo(employee);
    }

    @Test
    public void addFacultyAccountTest() {
        FacultyAccount res = sut.addFacultyAccount(facultyNetId, facultyNumber);
        verify(facultyAccountRepository).save(facultyAccount);
        assertThat(res).isEqualTo(facultyAccount);
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
}
