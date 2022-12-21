package nl.tudelft.sem.template.resourcepool.domain.resourcepool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;



@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RpManagementServiceTests {

    @MockBean
    private transient RpFacultyRepository mockRepo;

    private transient RpManagementService rpManagementService;

    private String facultyName;

    private String managerNetId;

    private Long facultyId;

    @Captor
    private ArgumentCaptor<Faculty> argumentCaptor;

    @BeforeEach
    void setup() {
        argumentCaptor = ArgumentCaptor.forClass(Faculty.class);
        managerNetId = "managerNetId";
        facultyName = "math";
        mockRepo = mock(RpFacultyRepository.class);
        rpManagementService = new RpManagementService(mockRepo);
        facultyId = 6L;

    }

    @Test
    void createFacultyTestSuccessful() throws Exception {
        when(mockRepo.existsByName(facultyName)).thenReturn(false);
        when(mockRepo.existsByManagerNetId(managerNetId)).thenReturn(false);

        rpManagementService.createFaculty(facultyName, managerNetId);
        verify(mockRepo).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getManagerNetId()).isEqualTo(managerNetId);
        assertThat(argumentCaptor.getValue().getName()).isEqualTo(facultyName);
    }

    @Test
    void createFacultyManagerAlreadyAssigned() {
        when(mockRepo.existsByName(facultyName)).thenReturn(false);
        when(mockRepo.existsByManagerNetId(managerNetId)).thenReturn(true);

        assertThrows(ManagerNetIdAlreadyAssignedException.class,
                () -> rpManagementService.createFaculty(facultyName, managerNetId));
    }

    @Test
    void facultyNameAlreadyInUse() {
        when(mockRepo.existsByName(facultyName)).thenReturn(true);
        when(mockRepo.existsByManagerNetId(managerNetId)).thenReturn(false);

        assertThrows(NameAlreadyInUseException.class,
                () -> rpManagementService.createFaculty(facultyName, managerNetId));
    }

    @Test
    void verifyFacultySuccessful() {
        when(mockRepo.existsById(facultyId)).thenReturn(true);
        assertThat(rpManagementService.verifyFaculty(facultyId)).isTrue();
    }

    @Test
    void verifyFacultyUnsuccessful() {
        when(mockRepo.existsById(facultyId)).thenReturn(false);
        assertThat(rpManagementService.verifyFaculty(facultyId)).isFalse();
    }

    @Test
    void findResourceByNameSuccessful() throws FacultyNotFoundException {
        when(mockRepo.existsByName(facultyName)).thenReturn(true);

        ResourcePool resourcePool = new ResourcePool(facultyName);
        Resources resources = new Resources(100, 100, 100);
        resourcePool.setAvailableResources(resources);

        when(mockRepo.findByName(facultyName)).thenReturn(Optional.of(resourcePool));

        assertThat(rpManagementService.findResourcesByName(facultyName)).isEqualTo(resources);
    }

    @Test
    void findResourceByNameUnsuccessful() {
        when(mockRepo.existsByName(facultyName)).thenReturn(false);
        assertThrows(FacultyNotFoundException.class,
                () -> rpManagementService.findResourcesByName(facultyName));

    }

}
