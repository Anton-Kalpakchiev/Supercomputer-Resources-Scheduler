package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import nl.tudelft.sem.template.resourcepool.models.NodeInteractionRequestModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.AssertionFailedError;
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

    @Test
    public void checkEnoughResourcesRemainingTestTrue() {
        Resources resources = new Resources(100, 1, 0);
        assertTrue(rpManagementService.checkEnoughResourcesRemaining(resources));
    }

    @Test
    public void checkEnoughResourcesRemainingTestFalse() {
        Resources resources = new Resources(-1, 1, 0);
        assertFalse(rpManagementService.checkEnoughResourcesRemaining(resources));
    }

    @Test
    public void killContributeNodeMutant() throws Exception {
        Faculty faculty = rpManagementService.createFaculty("test", "id");
        Resources before = faculty.getNodeResources();
        assertEquals(new Resources(0, 0, 0), before);

        NodeInteractionRequestModel model = new NodeInteractionRequestModel(
                faculty.getId(), 100, 100, 100
        );

        rpManagementService.contributeNodeMutated(model);
        final Resources after = faculty.getNodeResources();
        assertThrows(AssertionFailedError.class, () -> {
            assertEquals(new Resources(100, 100, 100), after);
        });
    }
}