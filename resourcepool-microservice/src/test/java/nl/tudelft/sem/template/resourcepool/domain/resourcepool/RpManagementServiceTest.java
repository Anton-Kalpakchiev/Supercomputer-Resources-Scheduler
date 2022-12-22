package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockPasswordEncoder"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)

class RpManagementServiceTest {

    @Autowired
    private transient RpManagementService rpManagementService;

    @Autowired
    private transient RpFacultyRepository rpFacultyRepository;

    @Test
    public void createFaculty_withValidData_worksCorrectly() throws Exception {
        // Arrange
        final String name = "CSE";
        final String managerNetId = "cse_manager";

        // Act
        Faculty faculty = rpManagementService.createFaculty(name, managerNetId);

        // Assert
        Faculty savedFaculty = (Faculty) rpFacultyRepository
                .findById(faculty.getId()).orElseThrow();

        assertEquals(savedFaculty.getName(), name);
        assertEquals(savedFaculty.getManagerNetId(), managerNetId);
    }

    @Test
    public void createFaculty_withExistingName_throwsException() throws Exception {
        // Arrange
        final String name = "CSE";
        final String managerNetId = "cse_manager";
        final String differentMangerNetId = "different_manager";

        // Act
        Faculty faculty = rpManagementService.createFaculty(name, managerNetId);

        // Assert
        assertThrows(NameAlreadyInUseException.class, () -> {
            rpManagementService.createFaculty(name, differentMangerNetId);
        });
    }

    @Test
    public void createFaculty_withExistingManagerNetId_throwsException() throws Exception {
        // Arrange
        final String name = "CSE";
        final String managerNetId = "cse_manager";
        final String differentName = "NOT CSE";

        // Act
        Faculty faculty = rpManagementService.createFaculty(name, managerNetId);

        // Assert
        assertThrows(ManagerNetIdAlreadyAssignedException.class, () -> {
            rpManagementService.createFaculty(differentName, managerNetId);
        });
    }
}