package nl.tudelft.sem.template.nodes.domain.node;

import nl.tudelft.sem.template.nodes.domain.resource.Resource;
import org.springframework.stereotype.Service;

/**
 * A DDD service for registering a new user.
 */
@Service
public class NodeCreationService {
    private final transient NodeRepository nodeRepository;

    public NodeCreationService(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }


    /**
     * Register a node in the repository.
     *
     * @param name     the name of the node
     * @param url      the url of the node
     * @param token    the token of the node
     * @param resource the resource
     * @return the node that has been registered
     * @throws Exception an exception
     */
    public Node registerNode(Name name, NodeUrl url, Token token, Resource resource) throws Exception {
        if (!checkNameIsUnique(name)) {
            throw new NameAlreadyInUseException(name);
        }
        if (!checkUrlIsUnique(url)) {
            throw new UrlAlreadyInUseException(url);
        }
        if (!checkTokenIsUnique(token)) {
            throw new TokenAlreadyInUseException(token);
        }
        if (!checkResourceRequirements(resource)) {
            throw new ResourcesInvalidException(resource);
        }
        Node node = new Node(name, url, token, resource);
        nodeRepository.save(node);
        return node;
    }

    /**
     * Check resource requirements boolean.
     *
     * @param resource the resource
     * @return the boolean
     */
    public boolean checkResourceRequirements(Resource resource) {
        return resource.getCpu() >= resource.getGpu() && resource.getCpu() >= resource.getMemory();
    }

    public boolean checkNameIsUnique(Name name) {
        return !nodeRepository.existsByName(name);
    }

    public boolean checkTokenIsUnique(Token token) {
        return !nodeRepository.existsByToken(token);
    }

    public boolean checkUrlIsUnique(NodeUrl url) {
        return !nodeRepository.existsByUrl(url);
    }
}
