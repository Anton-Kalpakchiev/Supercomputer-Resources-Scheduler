package nl.tudelft.sem.template.nodes.domain.node.chain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nl.tudelft.sem.template.nodes.domain.node.Name;
import nl.tudelft.sem.template.nodes.domain.node.Node;
import nl.tudelft.sem.template.nodes.domain.node.NodeManagementService;
import nl.tudelft.sem.template.nodes.domain.node.NodeRepository;
import nl.tudelft.sem.template.nodes.domain.node.NodeUrl;
import nl.tudelft.sem.template.nodes.domain.node.Token;
import nl.tudelft.sem.template.nodes.domain.resources.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BaseHandlerTest {

    @Mock
    private transient NodeRepository repo;

    @Mock
    private transient NodeManagementService nodeManagementService;

    @BeforeEach
    void setup() {
        when(repo.existsById(0L)).thenReturn(true);
        when(nodeManagementService.verifyFaculty(0L)).thenReturn(true);
    }

    @Test
    void getRepo() {
        BaseHandler handler = new NodeExistenceHandler(repo);
        assertEquals(handler.getRepo(), repo);
    }

    @Test
    void nodeExistenceTest() throws InvalidRequestException {
        BaseHandler handler = new NodeExistenceHandler(repo);
        boolean result = handler.handle(0L);
        assertThat(result == true);
    }

    @Test
    void nodeNonExistenceTest() throws InvalidRequestException {
        BaseHandler handler = new NodeExistenceHandler(repo);
        assertThrows(NodeIdNotFoundException.class, () -> {
            handler.handle(1L);
        });
    }

    @Test
    void facultyExistenceTest() throws InvalidRequestException {
        Name name = new Name("name");
        NodeUrl nodeUrl = new NodeUrl("url");
        String owner = "owner";
        long facultyId = 0L;
        Token token = new Token("token");
        Resources resources = new Resources(100, 100, 100);

        Node node = new Node(name, nodeUrl, owner, facultyId, token, resources);

        when(repo.findById(anyLong())).thenReturn(Optional.of(node));

        BaseHandler handler = new FacultyExistenceHandler(repo, nodeManagementService);
        boolean result = handler.handle(0L);
        assertThat(result == true);
    }

    @Test
    void facultyNonExistenceTest() throws InvalidRequestException {
        Name name = new Name("name");
        NodeUrl nodeUrl = new NodeUrl("url");
        String owner = "owner";
        long facultyId = 1L;
        Token token = new Token("token");
        Resources resources = new Resources(100, 100, 100);

        Node node = new Node(name, nodeUrl, owner, facultyId, token, resources);

        when(repo.findById(anyLong())).thenReturn(Optional.of(node));

        BaseHandler handler = new FacultyExistenceHandler(repo, nodeManagementService);
        assertThrows(FacultyDoesNotExistException.class, () -> {
            handler.handle(0L);
        });
    }

    @Test
    void ownerExistenceTest() throws InvalidRequestException {
        Name name = new Name("name");
        NodeUrl nodeUrl = new NodeUrl("url");
        String owner = "owner";
        long facultyId = 0L;
        Token token = new Token("token");
        Resources resources = new Resources(100, 100, 100);

        Node node = new Node(name, nodeUrl, owner, facultyId, token, resources);

        when(repo.findById(anyLong())).thenReturn(Optional.of(node));

        BaseHandler handler = new ValidOwnerHandler(repo, "owner");
        boolean result = handler.handle(0L);
        assertThat(result == true);
    }

    @Test
    void ownerNonExistenceTest() throws InvalidRequestException {
        Name name = new Name("name");
        NodeUrl nodeUrl = new NodeUrl("url");
        String owner = "owner";
        long facultyId = 0L;
        Token token = new Token("token");
        Resources resources = new Resources(100, 100, 100);

        Node node = new Node(name, nodeUrl, owner, facultyId, token, resources);

        when(repo.findById(anyLong())).thenReturn(Optional.of(node));

        BaseHandler handler = new ValidOwnerHandler(repo, "not owner");
        assertThrows(InvalidOwnerException.class, () -> {
            handler.handle(0L);
        });
    }


}