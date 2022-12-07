package nl.tudelft.sem.template.nodes.domain;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.sem.template.nodes.domain.node.Name;
import nl.tudelft.sem.template.nodes.domain.node.Node;
import nl.tudelft.sem.template.nodes.domain.node.NodeRepository;
import nl.tudelft.sem.template.nodes.domain.node.NodeUrl;
import nl.tudelft.sem.template.nodes.domain.node.RegistrationService;
import nl.tudelft.sem.template.nodes.domain.node.Token;
import nl.tudelft.sem.template.nodes.domain.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RegistrationServiceTests {

    @Autowired
    private transient RegistrationService registrationService;
    @Autowired
    private transient NodeRepository nodeRepository;

    @Test
    public void createNode_withValidData_worksCorrectly() throws Exception {
        // Arrange
        final Name name = new Name("Mayte");
        final NodeUrl url = new NodeUrl("url");
        final Token token = new Token("Token");
        final Resource resource = new Resource(420, 400, 400);

        // Act
        registrationService.registerNode(name, url, token, resource);

        // Assert
        Node savedNode = nodeRepository.findByName(name).orElseThrow();

        assertThat(savedNode.getName()).isEqualTo(name);
        assertThat(savedNode.getResource()).isEqualTo(resource);
        assertThat(savedNode.getUrl()).isEqualTo(url);
        assertThat(savedNode.getToken()).isEqualTo(token);
    }
}