package nl.tudelft.sem.template.nodes.domain.node;

import nl.tudelft.sem.template.nodes.authentication.JwtRequestFilter;
import nl.tudelft.sem.template.nodes.domain.resources.Resources;
import nl.tudelft.sem.template.nodes.models.ContributeToFacultyModel;
import nl.tudelft.sem.template.nodes.models.NodeContributionModel;
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
        contributeNodeToFaculty(facultyId, resources);
        Node node = new Node(name, url, token, resources);
        repo.save(node);
        return node;
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
     * Sends an API-request to the resource pool microservice to contribute a node to a faculty.
     *
     * @param facultyId the id of the faculty the node should be contributed to
     * @param resources the amount of resources the node contains
     * @throws Exception if the request has failed because the faculty id was invalid
     */
    public void contributeNodeToFaculty(long facultyId, Resources resources) throws Exception {
        String url = "http://localhost:8085/contributeNode";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(JwtRequestFilter.token);

        HttpEntity<ContributeToFacultyModel> entity = new HttpEntity<>(new ContributeToFacultyModel(facultyId,
                                    resources.getCpu(), resources.getGpu(), resources.getMemory()), headers);

        ResponseEntity<NodeContributionModel> result = restTemplate.postForEntity(url, entity, NodeContributionModel.class);
        if (result == null) {
            return;
        }
        if (!result.getStatusCode().is2xxSuccessful()) {
            throw new Exception(result.getStatusCode().getReasonPhrase());
        }
    }
}
