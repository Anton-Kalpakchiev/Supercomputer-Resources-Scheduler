package nl.tudelft.sem.template.requests.domain;

import java.util.Calendar;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A DDD entity representing an application user in our domain.
 */
@Entity
@Table(name = "requests")
@NoArgsConstructor
public class AppRequest extends HasEvents {
    /**
     * Identifier for the application request.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Column(name = "id", nullable = false)
    private long id;
    @Getter
    @Column(name = "description", nullable = false)
    private String description;

    @Getter
    @Column(name = "memory", nullable = false)
    private int mem;

    @Getter
    @Column(name = "cpu", nullable = false)
    private int cpu;

    @Getter
    @Column(name = "gpu", nullable = false)
    private int gpu;

    @Getter
    @Column(name = "owner", nullable = false)
    private String owner;

    @Getter
    @Column(name = "facultyName", nullable = false)
    private String facultyName;

    @Getter
    @Column(name = "deadline", nullable = false)
    private Calendar deadline;

    @Getter
    @Setter
    @Column(name = "status", nullable = false)
    private int status;
    /*
    0 for pending,
    1 for approved,
    2 for rejected,
    3 pending and waiting for the free RP to get resources at the 6h before end of day deadline
    */



    /**
     * Create new application request.
     *
     * @param description The description of the request
     * @param resources The resources in the request
     */
    public AppRequest(String description, Resources resources, String owner,
                      String facultyName, Calendar deadline, int status) {
        this.facultyName = facultyName;
        this.description = description;
        this.mem = resources.getMem();
        this.cpu = resources.getCpu();
        this.gpu = resources.getGpu();
        this.owner = owner;
        this.deadline = deadline;
        this.status = status;
        this.recordThat(new RequestWasCreatedEvent(description));
    }

    /**
     * Equality is only based on the identifier.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AppRequest appRequest = (AppRequest) o;
        return id == (appRequest.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner);
    }
}
