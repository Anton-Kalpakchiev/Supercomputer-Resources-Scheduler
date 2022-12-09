package nl.tudelft.sem.template.nodes.domain.node;

public class UrlAlreadyInUseException extends Exception {
    static final long serialVersionUID = -3384516993524229948L;

    public UrlAlreadyInUseException(NodeUrl url) {
        super(url.toString());
    }
}
