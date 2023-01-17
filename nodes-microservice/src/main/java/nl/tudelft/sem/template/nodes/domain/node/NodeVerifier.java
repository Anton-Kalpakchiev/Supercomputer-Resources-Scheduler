package nl.tudelft.sem.template.nodes.domain.node;

import nl.tudelft.sem.template.nodes.domain.resources.Resources;
import org.springframework.beans.factory.annotation.Autowired;

public class NodeVerifier {
    private transient NodeRepository repo;
    private final transient Name name;
    private final transient NodeUrl url;
    private final transient Token token;
    private final transient Resources resources;

    @Autowired
    public void setRepo(NodeRepository repo) {
        this.repo = repo;
    }

    public Name getName() {
        return name;
    }

    public NodeUrl getUrl() {
        return url;
    }

    public Token getToken() {
        return token;
    }

    public Resources getResources() {
        return resources;
    }

    /**
     * Constructor.
     *
     * @param name the name
     * @param url the url
     * @param token the token
     * @param resources the resources
     */
    public NodeVerifier(Name name, NodeUrl url, Token token, Resources resources) {
        this.name = name;
        this.url = url;
        this.token = token;
        this.resources = resources;
    }

    /**
     * Check if name exists.
     *
     * @param name the name
     * @throws NameAlreadyInUseException if the name is already in the repo
     */
    public void checkNameExistence(Name name) throws NameAlreadyInUseException {
        if (repo.existsByName(name)) {
            throw new NameAlreadyInUseException(name);
        }
    }

    /**
     * Check if url exists.
     *
     * @param url the url
     * @throws UrlAlreadyInUseException if the url is already in the repo
     */
    public void checkUrlExistence(NodeUrl url) throws UrlAlreadyInUseException {
        if (repo.existsByUrl(url)) {
            throw new UrlAlreadyInUseException(url);
        }
    }

    /**
     * Check if token exists.
     *
     * @param token the token
     * @throws TokenAlreadyInUseException if the token is already in the repo
     */
    public void checkTokenExistence(Token token) throws TokenAlreadyInUseException {
        if (repo.existsByToken(token)) {
            throw new TokenAlreadyInUseException(token);
        }
    }

    /**
     * Check resource requirements.
     *
     * @param resource the resource
     * @throws ResourcesInvalidException if the resources are negative or insufficient
     */
    public void checkResourceRequirements(Resources resource) throws ResourcesInvalidException {
        int cpu = resource.getCpu();
        int gpu = resource.getGpu();
        int memory = resource.getMemory();
        if (!(gpu >= 0 && memory >= 0  && cpu >= gpu && cpu >= memory)) {
            throw new ResourcesInvalidException(resource);
        }
    }

    /**
     * Calls all the verification methods with their respective attributes.
     *
     * @throws NameAlreadyInUseException if name is used
     * @throws UrlAlreadyInUseException if url is used
     * @throws TokenAlreadyInUseException if token is used
     * @throws ResourcesInvalidException if resources are invalid
     */
    public void verify() throws NameAlreadyInUseException, UrlAlreadyInUseException,
            TokenAlreadyInUseException, ResourcesInvalidException {
        checkNameExistence(name);
        checkUrlExistence(url);
        checkTokenExistence(token);
        checkResourceRequirements(resources);
    }
}
