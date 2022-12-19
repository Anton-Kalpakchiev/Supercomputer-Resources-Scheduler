package nl.tudelft.sem.template.users.domain.employment;

import nl.tudelft.sem.template.users.authorization.AuthorizationManager;
import nl.tudelft.sem.template.users.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmploymentServiceTests {

    private SysadminRepository sysadminRepository;
    private EmployeeRepository employeeRepository;
    private FacultyAccountRepository facultyAccountRepository;
    private EmployeeService employeeService;
    private FacultyAccountService facultyAccountService;
    private AuthorizationManager authorizationManager;
    private Sysadmin admin;
    private Employee employee;
    private FacultyAccount facultyAccount;
    private final String adminNetId = "admin";
    private final String facultyManagerNetId = "manager";
    private final String employeeNetId = "mayte";
    private final long facultyId = 6L;

    @BeforeEach
    void setup() {
        sysadminRepository = mock(SysadminRepository.class);
        employeeRepository = mock(EmployeeRepository.class);
        facultyAccountRepository = mock(FacultyAccountRepository.class);
        employeeService = new EmployeeService(employeeRepository, authorizationManager, facultyAccountService);
        facultyAccountService = new FacultyAccountService(facultyAccountRepository);
        authorizationManager = new AuthorizationManager(sysadminRepository, employeeRepository, facultyAccountRepository);

        admin = new Sysadmin(adminNetId);
        employee = new Employee(employeeNetId);
        facultyAccount = new FacultyAccount(facultyManagerNetId, facultyId);

        when(employeeRepository.findByNetId(employeeNetId)).thenReturn(Optional.ofNullable(employee));
        when(facultyAccountRepository.findByNetId(facultyManagerNetId)).thenReturn(Optional.ofNullable(facultyAccount));
    }

    @Test
    void testGetParentFacultyIdEmpty() throws NoSuchUserException {
        assertThat(employeeService.getParentFacultyId(employeeNetId)).isEqualTo(Set.of());
    }

    @Test
    void jsonParsedCorrectly() throws EmploymentException {
        String facultyIds = "facultyIds: 6, 7, 8";
        assertThat(employeeService.parseJsonFacultyIds(facultyIds)).isEqualTo(Set.of(6L, 7L, 8L));
    }

    @Test
    void jsonParsedIncorrectly() {
        String facultyIds = "facultyIds 6, 7, 8";
        assertThrows(EmploymentException.class, () -> employeeService.parseJsonFacultyIds(facultyIds));
    }

    @Test
    void jsonParsedIncorrectlyNone() {
        String facultyIds = "facultyIds: None";
        assertThrows(EmploymentException.class, () -> employeeService.parseJsonFacultyIds(facultyIds));
    }


}
