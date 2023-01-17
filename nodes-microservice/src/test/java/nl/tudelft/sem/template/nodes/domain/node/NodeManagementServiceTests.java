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
public class NodeManagementServiceTests {

    @Autowired
    private transient NodeManagementService nodeManagementService;
    @Autowired
    private transient NodeRepository nodeRepository;
    @Autowired
    private transient RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        nodeRepository = mock(NodeRepository.class);
        restTemplate = mock(RestTemplate.class);
        nodeManagementService = new NodeManagementService(nodeRepository, restTemplate);
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
        NodeVerifier nodeVerifier = new NodeVerifier(name, url, token, resources);
        nodeVerifier.setRepo(nodeRepository);
        Node createdNode = nodeManagementService.registerNode(nodeVerifier, ownerNetId, facultyId);

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

        NodeVerifier nodeVerifier = new NodeVerifier(name, url, token, resources);
        nodeVerifier.setRepo(nodeRepository);

        assertThrows(ResourcesInvalidException.class, () ->
                nodeManagementService.registerNode(nodeVerifier, ownerNetId, facultyId));
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

        NodeVerifier nodeVerifier = new NodeVerifier(name, url, token, resources);
        NodeVerifier sameName = new NodeVerifier(name, new NodeUrl("url2"),
                new Token("token2"), resources);
        nodeVerifier.setRepo(nodeRepository);
        sameName.setRepo(nodeRepository);
        nodeManagementService.registerNode(nodeVerifier, ownerNetId, facultyId);
        when(nodeRepository.existsByName(name)).thenReturn(true);

        assertThrows(NameAlreadyInUseException.class, () ->
                nodeManagementService.registerNode(sameName, ownerNetId, facultyId));
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

        NodeVerifier nodeVerifier = new NodeVerifier(name, url, token, resources);
        NodeVerifier sameUrl = new NodeVerifier(new Name("name2"), url,
                new Token("token2"), resources);
        nodeVerifier.setRepo(nodeRepository);
        sameUrl.setRepo(nodeRepository);
        nodeManagementService.registerNode(nodeVerifier, ownerNetId, facultyId);
        when(nodeRepository.existsByUrl(url)).thenReturn(true);

        assertThrows(UrlAlreadyInUseException.class, () ->
                nodeManagementService.registerNode(sameUrl, ownerNetId, facultyId));
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

        NodeVerifier nodeVerifier = new NodeVerifier(name, url, token, resources);
        NodeVerifier sameToken = new NodeVerifier(new Name("name2"), new NodeUrl("url2"),
                token, resources);
        nodeVerifier.setRepo(nodeRepository);
        sameToken.setRepo(nodeRepository);
        nodeManagementService.registerNode(nodeVerifier, ownerNetId, facultyId);
        when(nodeRepository.existsByToken(token)).thenReturn(true);

        assertThrows(TokenAlreadyInUseException.class, () ->
                nodeManagementService.registerNode(sameToken, ownerNetId, facultyId));
    }
}