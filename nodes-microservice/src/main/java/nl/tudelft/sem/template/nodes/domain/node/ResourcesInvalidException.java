package nl.tudelft.sem.template.nodes.domain.node;

import nl.tudelft.sem.template.nodes.domain.resource.Resource;

public class ResourcesInvalidException extends Exception {
    static final long serialVersionUID = -2387516993524229948L;

    public ResourcesInvalidException(Resource resource) {
        super(resource.toString());
    }
}
