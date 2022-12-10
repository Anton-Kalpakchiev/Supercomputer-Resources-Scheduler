package nl.tudelft.sem.template.users.domain.registration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import nl.tudelft.sem.template.users.domain.Employee;
import nl.tudelft.sem.template.users.domain.EmployeeRepository;
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
    private String admin;
    private String employee;

    @BeforeEach
    void setup() {
        sysadminRepository = mock(SysadminRepository.class);
        employeeRepository = mock(EmployeeRepository.class);
        facultyAccountRepository = mock(FacultyAccountRepository.class);
        sut = new RegistrationService(sysadminRepository,
                employeeRepository, facultyAccountRepository);

        admin = "admin";
        employee = "employee";
    }

    @Test
    public void registerAdminTest() {
        Sysadmin expected = new Sysadmin(admin);
        User registered = sut.registerUser(admin);

        verify(sysadminRepository).save(expected);
        assertThat(registered).isEqualTo(expected);
    }

    @Test
    public void registerEmployee() {
        Employee expected = new Employee(employee);
        User registered = sut.registerUser(employee);

        verify(employeeRepository).save(expected);
        assertThat(registered).isEqualTo(expected);
    }
}
