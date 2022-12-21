package nl.tudelft.sem.template.nodes.domain.node;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.nodes.domain.resources.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class NodeCreationServiceTests {

    @Autowired
    private transient NodeManagementService nodeCreationService;
    @Autowired
    private transient NodeRepository nodeRepository;
    @Autowired
    private transient RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        nodeRepository = mock(NodeRepository.class);
        restTemplate = mock(RestTemplate.class);
        nodeCreationService = new NodeManagementService(nodeRepository, restTemplate);
    }

    @Test
    public void createNode_withValidData_worksCorrectly() throws Exception {
        final Name name = new Name("Mayte");
        final NodeUrl url = new NodeUrl("url");
        final String ownerNetId = "test";
        final long facultyId = 1L;
        final Token token = new Token("Token");
        final Resources resources = new Resources(420, 400, 400);
        Node node = new Node(name, url, ownerNetId, facultyId, token, resources);
        when(nodeRepository.save(node)).thenReturn(node);
        Node createdNode = nodeCreationService.registerNode(name, url, ownerNetId, facultyId, token, resources);

        assertThat(createdNode.getNodeName()).isEqualTo(name);
        assertThat(createdNode.getResource()).isEqualTo(resources);
        assertThat(createdNode.getUrl()).isEqualTo(url);
        assertThat(createdNode.getToken()).isEqualTo(token);
    }

    @Test
    public void createNodeResourceException() {
        // Arrange
        final Name name = new Name("Mayte");
        final NodeUrl url = new NodeUrl("url");
        final String ownerNetId = "test";
        final long facultyId = 1L;
        final Token token = new Token("Token");
        final Resources resources = new Resources(420, 500, 400);

        assertThrows(ResourcesInvalidException.class, () ->
                nodeCreationService.registerNode(name, url, ownerNetId, facultyId, token, resources));
    }

    @Test
    public void createNodeNameException() throws Exception {
        // Arrange
        final Name name = new Name("Mayte");
        final NodeUrl url = new NodeUrl("url");
        final String ownerNetId = "test";
        final long facultyId = 1L;
        final Token token = new Token("Token");
        final Resources resources = new Resources(420, 400, 400);
        nodeCreationService.registerNode(name, url, ownerNetId, facultyId, token, resources);
        when(nodeRepository.existsByName(name)).thenReturn(true);

        assertThrows(NameAlreadyInUseException.class, () ->
                nodeCreationService.registerNode(name, new NodeUrl("url2"), ownerNetId,
                                    facultyId, new Token("token2"), resources));
    }

    @Test
    public void createNodeUrlException() throws Exception {
        // Arrange
        final Name name = new Name("Mayte");
        final NodeUrl url = new NodeUrl("url");
        final String ownerNetId = "test";
        final long facultyId = 1L;
        final Token token = new Token("token");
        final Resources resources = new Resources(420, 400, 400);
        nodeCreationService.registerNode(name, url, ownerNetId, facultyId, token, resources);
        when(nodeRepository.existsByUrl(url)).thenReturn(true);

        assertThrows(UrlAlreadyInUseException.class, () ->
                nodeCreationService.registerNode(new Name("Ivo"), url, ownerNetId, facultyId,
                                                    new Token("token2"), resources));
    }

    @Test
    public void createNodeTokenException() throws Exception {
        // Arrange
        final Name name = new Name("Mayte");
        final NodeUrl url = new NodeUrl("url");
        final String ownerNetId = "test";
        final long facultyId = 1L;
        final Token token = new Token("Token");
        final Resources resources = new Resources(420, 400, 400);
        nodeCreationService.registerNode(name, url, ownerNetId, facultyId, token, resources);
        when(nodeRepository.existsByToken(token)).thenReturn(true);

        assertThrows(TokenAlreadyInUseException.class, () ->
                nodeCreationService.registerNode(new Name("Ivo"), new NodeUrl("url2"),
                                                    ownerNetId, facultyId, token, resources));
    }
}