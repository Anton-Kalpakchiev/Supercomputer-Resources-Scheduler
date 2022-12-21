package nl.tudelft.sem.template.users.domain;

public class FacultyException extends Exception {

    static final long serialVersionUID = -3375169931242299482L;

    public FacultyException(String reason) {
        super(reason);
    }

}
