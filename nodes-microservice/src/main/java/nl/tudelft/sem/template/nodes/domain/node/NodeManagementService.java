package nl.tudelft.sem.template.nodes.domain.node;

import nl.tudelft.sem.template.nodes.authentication.JwtRequestFilter;
import nl.tudelft.sem.template.nodes.domain.resources.Resources;
import nl.tudelft.sem.template.nodes.models.FacultyInteractionResponseModel;
import nl.tudelft.sem.template.nodes.models.NodeInformationToFacultyModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * A DDD service for registering a new user.
 */
@Service
public class NodeManagementService {

    private final transient NodeRepository repo;

    private final transient RestTemplate restTemplate;

    /**
     * Instantiates a new NodeManagementService.
     *
     * @param repo the RpFaculty repository
     * @param restTemplate the RestTemplate used to send API-requests
     */
    public NodeManagementService(NodeRepository repo, RestTemplate restTemplate) {
        this.repo = repo;
        this.restTemplate = restTemplate;
    }


    /**
     * Register a node in the repository.
     *
     * @param name     the name of the node
     * @param url      the url of the node
     * @param ownerNetId the netId of the owner of the node
     * @param facultyId the id of the faculty the node should be contributed to
     * @param token    the token of the node
     * @param resources the resources of the node
     * @return the node that has been registered
     * @throws Exception an exception
     */
    public Node registerNode(Name name, NodeUrl url, String ownerNetId,
                             long facultyId, Token token, Resources resources) throws Exception {
        if (repo.existsByName(name)) {
            throw new NameAlreadyInUseException(name);
        }
        if (repo.existsByUrl(url)) {
            throw new UrlAlreadyInUseException(url);
        }
        if (repo.existsByToken(token)) {
            throw new TokenAlreadyInUseException(token);
        }
        if (!checkResourceRequirements(resources)) {
            throw new ResourcesInvalidException(resources);
        }
        interactWithFaculty("contributeNode", facultyId, resources);
        Node node = new Node(name, url, ownerNetId, facultyId, token, resources);
        repo.save(node);
        return node;
    }

    /**
     * Delete a node from the repository.
     *
     * @param nodeId the id of the node to be deleted
     * @param ownerNetId the id of the owner of the node
     * @throws Exception if the node id can't be found or the requester isn't the owner of the node
     */
    public void deleteNode(long nodeId, String ownerNetId) throws Exception {
        if (!repo.existsById(nodeId)) {
            throw new NodeIdNotFoundException(nodeId);
        }
        Node node = repo.findById(nodeId).get();
        if (!node.getOwnerNetId().equals(ownerNetId)) {
            throw new InvalidOwnerException(ownerNetId);
        }
        interactWithFaculty("deleteNode", node.getFacultyId(), node.getResource());
        repo.deleteById(nodeId);
    }

    /**
     * Check resource requirements boolean.
     *
     * @param resource the resource
     * @return the boolean
     */
    public boolean checkResourceRequirements(Resources resource) {
        int cpu = resource.getCpu();
        int gpu = resource.getGpu();
        int memory = resource.getMemory();
        return gpu >= 0 && memory >= 0  && cpu >= gpu && cpu >= memory;
    }

    /**
     * Sends an API-request to the resource pool microservice to either contribute or delete a node to/from a faculty.
     *
     * @param method the last part of the url to indicate the required action
     * @param facultyId the id of the faculty the node should be contributed to
     * @param resources the amount of resources the node contains
     * @throws Exception if the request has failed because the faculty id was invalid
     */
    public void interactWithFaculty(String method, long facultyId, Resources resources) throws Exception {
        String url = "http://localhost:8085/" + method;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(JwtRequestFilter.token);

        HttpEntity<NodeInformationToFacultyModel> entity = new HttpEntity<>(new NodeInformationToFacultyModel(facultyId,
                                    resources.getCpu(), resources.getGpu(), resources.getMemory()), headers);

        ResponseEntity<FacultyInteractionResponseModel> result = restTemplate.postForEntity(
                                                                url, entity, FacultyInteractionResponseModel.class);
        if (result == null) {
            return;
        }
        if (!result.getStatusCode().is2xxSuccessful()) {
            throw new Exception(result.getStatusCode().getReasonPhrase());
        }
    }
}
