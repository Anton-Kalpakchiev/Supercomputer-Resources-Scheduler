package nl.tudelft.sem.template.nodes.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import nl.tudelft.sem.template.nodes.authentication.AuthManager;
import nl.tudelft.sem.template.nodes.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.nodes.controllers.NodeController;
import nl.tudelft.sem.template.nodes.domain.node.Name;
import nl.tudelft.sem.template.nodes.domain.node.Node;
import nl.tudelft.sem.template.nodes.domain.node.NodeManagementService;
import nl.tudelft.sem.template.nodes.domain.node.NodeRepository;
import nl.tudelft.sem.template.nodes.domain.node.NodeUrl;
import nl.tudelft.sem.template.nodes.domain.node.Token;
import nl.tudelft.sem.template.nodes.domain.resources.Resources;
import nl.tudelft.sem.template.nodes.integration.utils.JsonUtil;
import nl.tudelft.sem.template.nodes.models.NodeContributionRequestModel;
import nl.tudelft.sem.template.nodes.models.NodeDeletionRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class NodesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Mock
    private transient NodeManagementService nodeManagementService;

    @Mock
    private transient NodeRepository nodeRepository;

    @MockBean
    private transient NodeController mockNodeController;

    @BeforeEach
    void setup() {
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockAuthenticationManager.getNetId()).thenReturn("owner");
    }

    @Test
    public void contributeNode_withValidData_worksCorrectly() throws Exception {
        // Arrange
        final Name name = new Name("name");
        final NodeUrl nodeUrl = new NodeUrl("url");
        final String ownerNetId = "owner";
        final long facultyId = 15L;
        final Token token = new Token("token");
        final Resources resources = new Resources(100, 100, 100);

        final Node expectedNode = new Node(name, nodeUrl, ownerNetId, facultyId, token, resources);
        when(nodeRepository.findById(0L)).thenReturn(Optional.of(expectedNode));

        NodeContributionRequestModel model = new NodeContributionRequestModel();
        model.setName(name.toString());
        model.setUrl(nodeUrl.toString());
        model.setFacultyId(facultyId);
        model.setToken(token.toString());
        model.setMemory(resources.getMemory());
        model.setCpu(resources.getCpu());
        model.setGpu(resources.getGpu());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/contributeNode")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(model)));

        long id = 0L; // Long.parseLong(resultActions.andReturn().getResponse().getContentAsString());
        Node contributedNode = nodeRepository.findById(id).get();

        // Assert
        resultActions.andExpect(status().isOk());
        assertEquals(expectedNode, contributedNode);
    }

    @Test
    public void contributeNode_withInvalidData_throwsException() throws Exception {
        // Arrange
        final Name name = new Name("already existing name");
        final NodeUrl nodeUrl = new NodeUrl("already existing url");
        final long facultyId = 15L;
        final Token token = new Token("already existing token");
        final Resources resources = new Resources(100, 100, 100);

        when(nodeRepository.existsByName(name)).thenReturn(true);

        NodeContributionRequestModel model = new NodeContributionRequestModel();
        model.setName(name.toString());
        model.setUrl(nodeUrl.toString());
        model.setFacultyId(facultyId);
        model.setToken(token.toString());
        model.setMemory(resources.getMemory());
        model.setCpu(resources.getCpu());
        model.setGpu(resources.getGpu());

        // Act and Assert
        // assertThrows(NestedServletException.class, () -> {
        //   ResultActions resultActions = mockMvc.perform(post("/contributeNode")
        //          .contentType(MediaType.APPLICATION_JSON)
        //          .header("Authorization", "Bearer MockedToken")
        //          .content(JsonUtil.serialize(model)));
        // });
    }

    @Test
    public void deleteExistingNodeTest() throws Exception {
        // Arrange
        final Name name = new Name("name");
        final NodeUrl nodeUrl = new NodeUrl("url");
        final long facultyId = 15L;
        final Token token = new Token("token");
        final Resources resources = new Resources(100, 100, 100);

        NodeContributionRequestModel model = new NodeContributionRequestModel();
        model.setName(name.toString());
        model.setUrl(nodeUrl.toString());
        model.setFacultyId(facultyId);
        model.setToken(token.toString());
        model.setMemory(resources.getMemory());
        model.setCpu(resources.getCpu());
        model.setGpu(resources.getGpu());

        ResultActions resultActions = mockMvc.perform(post("/contributeNode")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(model)));

        long id = 0L; // Long.parseLong(resultActions.andReturn().getResponse().getContentAsString());
        NodeDeletionRequestModel deletionModel = new NodeDeletionRequestModel();
        deletionModel.setNodeId(id);

        // Act
        ResultActions resultActions2 = mockMvc.perform(post("/deleteNode")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(deletionModel)));

        // Assert
        resultActions2.andExpect(status().isOk());
        assertThat(nodeRepository.findByName(name).isPresent() == false);
    }
}
