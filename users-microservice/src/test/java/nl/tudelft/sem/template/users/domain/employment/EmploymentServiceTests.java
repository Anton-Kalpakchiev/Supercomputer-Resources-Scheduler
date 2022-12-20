package nl.tudelft.sem.template.users.domain.employment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EmploymentServiceTests {

    private EmployeeRepository employeeRepository;
    private EmployeeService employeeService;

    @BeforeEach
    void setup() {
        SysadminRepository sysadminRepository = mock(SysadminRepository.class);
        employeeRepository = mock(EmployeeRepository.class);
        FacultyAccountRepository facultyAccountRepository = mock(FacultyAccountRepository.class);
        FacultyAccountService facultyAccountService = new FacultyAccountService(facultyAccountRepository);
        AuthorizationManager authorizationManager = new AuthorizationManager(
                sysadminRepository, employeeRepository, facultyAccountRepository);
        employeeService = new EmployeeService(employeeRepository, authorizationManager, facultyAccountService);
    }

    @Test
    void testGetParentFacultyIdEmpty() throws NoSuchUserException {
        Employee employee = new Employee("Mayte");
        when(employeeRepository.findByNetId("Mayte")).thenReturn(Optional.of(employee));
        assertThat(employeeService.getParentFacultyId("Mayte")).isEqualTo(Set.of());
    }

    @Test
    void testGetParentFacultyEmployee() throws NoSuchUserException {
        Employee employee = new Employee("Mayte", Set.of(6L));
        when(employeeRepository.findByNetId("Mayte")).thenReturn(Optional.of(employee));
        assertThat(employeeService.getParentFacultyId("Mayte")).isEqualTo(Set.of(6L));
    }

}
