package nl.tudelft.sem.template.nodes.domain.node;

import nl.tudelft.sem.template.nodes.authentication.JwtRequestFilter;
import nl.tudelft.sem.template.nodes.domain.node.chain.FacultyExistenceHandler;
import nl.tudelft.sem.template.nodes.domain.node.chain.Handler;
import nl.tudelft.sem.template.nodes.domain.node.chain.InvalidRequestException;
import nl.tudelft.sem.template.nodes.domain.node.chain.NodeExistenceHandler;
import nl.tudelft.sem.template.nodes.domain.node.chain.ValidOwnerHandler;
import nl.tudelft.sem.template.nodes.domain.resources.Resources;
import nl.tudelft.sem.template.nodes.models.FacultyInteractionRequestModel;
import nl.tudelft.sem.template.nodes.models.VerifyFacultyRequestModel;
import nl.tudelft.sem.template.nodes.models.VerifyFacultyResponseModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
     * @param requesterNetId the netId of the employee which made the request
     * @throws Exception if the node id can't be found or the requester isn't the owner of the node
     */
    public String deleteNode(long nodeId, String requesterNetId) throws Exception {
        Handler nodeExistenceHandler = new NodeExistenceHandler(repo);
        Handler validOwnerHandler = new ValidOwnerHandler(repo, requesterNetId);
        Handler facultyExistenceHandler = new FacultyExistenceHandler(repo, this);

        nodeExistenceHandler.setNext(validOwnerHandler);
        validOwnerHandler.setNext(facultyExistenceHandler);

        try {
            nodeExistenceHandler.handle(nodeId);
        } catch (InvalidRequestException e) {
            return "Invalid request";
        }

        Node node = repo.findById(nodeId).get();
        interactWithFaculty("deleteNode", node.getFacultyId(), node.getResource());
        repo.deleteById(nodeId);
        return node.getNodeName().toString();
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

        HttpEntity<FacultyInteractionRequestModel> entity = new HttpEntity<>(new FacultyInteractionRequestModel(facultyId,
                                    resources.getCpu(), resources.getGpu(), resources.getMemory()), headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
        } catch (Exception e) {
            throw new InnerRequestFailedException("Request to " + url + " failed.");
        }
    }

    /**
     * Checks if the facultyId belongs to a faculty in the resourcePool microservice.
     *
     * @param facultyId the id of the faculty to be checked
     * @return whether the faculty exists or not
     */
    public boolean verifyFaculty(long facultyId) {
        String url = "http://localhost:8085/verifyFaculty";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(JwtRequestFilter.token);

        HttpEntity<VerifyFacultyRequestModel> entity = new HttpEntity<>(new VerifyFacultyRequestModel(facultyId), headers);
        try {
            return restTemplate.exchange(url, HttpMethod.POST, entity,
                    VerifyFacultyResponseModel.class).getBody().isVerified();
        } catch (Exception e) {
            return false;
        }
    }
}
