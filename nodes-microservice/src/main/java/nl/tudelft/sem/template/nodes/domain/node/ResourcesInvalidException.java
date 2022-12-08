package nl.tudelft.sem.template.nodes.domain.node;

import nl.tudelft.sem.template.nodes.domain.resources.Resources;

public class ResourcesInvalidException extends Exception {
    static final long serialVersionUID = -2387516993524229948L;

    public ResourcesInvalidException(Resources resource) {
        super(resource.toString());
    }
}
