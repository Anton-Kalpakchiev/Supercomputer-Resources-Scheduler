package nl.tudelft.sem.template.schedule.domain;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.NoArgsConstructor;

/**
 * A DDD entity representing an application user in our domain.
 */
@Entity
@Table(name = "schedules")
@NoArgsConstructor
public class DailySchedule {
    
    /**
     * Identifier for the daily schedule.
     */
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    @Column(name = "day", nullable = false)
    private Date day;

    @Column(name = "resource_pool_id", nullable = false)
    private long resourcePoolId;

    @Column(name = "list_request_id", nullable = false)
    private List<Long> list;

    /**
     * Create a new DailySchedule per Resource Pool.
     *
     * @param day
     * @param resourcePoolId
     * @param list
     */
    public DailySchedule(Date day, long resourcePoolId, List<Long> list) {
        this.id = day.getTime() + resourcePoolId;
        this.day = day;
        this.resourcePoolId = resourcePoolId;
        this.list = list;
    }

    public Date getDay() {
        return day;
    }

    public long getResourcePoolId() {
        return resourcePoolId;
    }

    public List<Long> getList() {
        return list;
    }

    public void addRequest(long requestId) {
        this.list.add(requestId);
    }

    /**
     * Equality is only based on the identifier.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailySchedule that = (DailySchedule) o;
        return id == that.id;
    }

}
