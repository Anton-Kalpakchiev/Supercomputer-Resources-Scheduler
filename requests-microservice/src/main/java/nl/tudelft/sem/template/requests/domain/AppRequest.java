package nl.tudelft.sem.template.requests.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    /**
     * Create new application request.
     *
     * @param description The description of the request
     * @param resources The resources in the request
     */
    public AppRequest(String description, Resources resources) {
        this.description = description;
        this.mem = resources.getMem();
        this.cpu = resources.getCpu();
        this.gpu = resources.getGpu();
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
}
