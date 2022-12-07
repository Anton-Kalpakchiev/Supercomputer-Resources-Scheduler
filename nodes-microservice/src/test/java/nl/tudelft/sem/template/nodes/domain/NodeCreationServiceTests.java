package nl.tudelft.sem.template.nodes.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import nl.tudelft.sem.template.nodes.domain.node.Name;
import nl.tudelft.sem.template.nodes.domain.node.NameAlreadyInUseException;
import nl.tudelft.sem.template.nodes.domain.node.Node;
import nl.tudelft.sem.template.nodes.domain.node.NodeCreationService;
import nl.tudelft.sem.template.nodes.domain.node.NodeRepository;
import nl.tudelft.sem.template.nodes.domain.node.NodeUrl;
import nl.tudelft.sem.template.nodes.domain.node.ResourcesInvalidException;
import nl.tudelft.sem.template.nodes.domain.node.Token;
import nl.tudelft.sem.template.nodes.domain.node.TokenAlreadyInUseException;
import nl.tudelft.sem.template.nodes.domain.node.UrlAlreadyInUseException;
import nl.tudelft.sem.template.nodes.domain.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class NodeCreationServiceTests {

    @Autowired
    private transient NodeCreationService nodeCreationService;
    @Autowired
    private transient NodeRepository nodeRepository;

    @Test
    public void createNode_withValidData_worksCorrectly() throws Exception {
        // Arrange
        final Name name = new Name("Mayte");
        final NodeUrl url = new NodeUrl("url");
        final Token token = new Token("Token");
        final Resource resource = new Resource(420, 400, 400);
        nodeCreationService.registerNode(name, url, token, resource);
        // Assert
        Node savedNode = nodeRepository.findByName(name).orElseThrow();

        assertThat(savedNode.getName()).isEqualTo(name);
        assertThat(savedNode.getResource()).isEqualTo(resource);
        assertThat(savedNode.getUrl()).isEqualTo(url);
        assertThat(savedNode.getToken()).isEqualTo(token);
    }

    @Test
    public void createNodeResourceException() throws Exception {
        // Arrange
        final Name name = new Name("Mayte");
        final NodeUrl url = new NodeUrl("url");
        final Token token = new Token("Token");
        final Resource resource = new Resource(420, 500, 400);
        nodeCreationService = mock(NodeCreationService.class);
        doThrow(new ResourcesInvalidException(resource)).when(nodeCreationService).registerNode(name, url, token, resource);
    }

    @Test
    public void createNodeNameException() throws Exception {
        // Arrange
        final Name name = new Name("Mayte");
        final NodeUrl url = new NodeUrl("url");
        final Token token = new Token("Token");
        final Resource resource = new Resource(420, 400, 400);
        nodeCreationService = mock(NodeCreationService.class);
        nodeCreationService.registerNode(name, url, token, resource);

        doThrow(new NameAlreadyInUseException(name)).when(nodeCreationService)
                .registerNode(name, new NodeUrl("url2"), new Token("token2"), resource);
    }

    @Test
    public void createNodeUrlException() throws Exception {
        // Arrange
        final Name name = new Name("Mayte");
        final NodeUrl url = new NodeUrl("url");
        final Token token = new Token("Token");
        final Resource resource = new Resource(420, 400, 400);
        nodeCreationService = mock(NodeCreationService.class);
        nodeCreationService.registerNode(name, url, token, resource);

        doThrow(new UrlAlreadyInUseException(url)).when(nodeCreationService)
                .registerNode(new Name("Ivo"), url, new Token("token2"), resource);
    }

    @Test
    public void createNodeTokenException() throws Exception {
        // Arrange
        final Name name = new Name("Mayte");
        final NodeUrl url = new NodeUrl("url");
        final Token token = new Token("Token");
        final Resource resource = new Resource(420, 400, 400);
        nodeCreationService = mock(NodeCreationService.class);
        nodeCreationService.registerNode(name, url, token, resource);

        doThrow(new TokenAlreadyInUseException(token)).when(nodeCreationService)
                .registerNode(new Name("Ivo"), new NodeUrl("url2"), token, resource);
    }
}