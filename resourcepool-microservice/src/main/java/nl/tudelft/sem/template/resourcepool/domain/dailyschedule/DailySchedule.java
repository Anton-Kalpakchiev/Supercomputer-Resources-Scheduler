package nl.tudelft.sem.template.resourcepool.domain.dailyschedule;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * A DDD entity representing an application user in our domain.
 */
@Entity
@Table(name = "schedules")
@IdClass(DailyScheduleId.class)
@NoArgsConstructor
public class DailySchedule {
    
    /**
     * Identifier for the daily schedule.
     */
    @Id
    @Column(name = "day", nullable = false)
    private Date day;

    @Id
    @Column(name = "resource_pool_id", nullable = false)
    private long resourcePoolId;

    @Column(name = "list_request_id", nullable = false)
    private List<Long> list;

    /**
     * Create a new DailySchedule per Resource Pool.
     *
     * @param day the day that the requests are scheduled on
     * @param resourcePoolId the id of the resource pool the requests are scheduled in
     */
    public DailySchedule(Date day, long resourcePoolId) {
        this.day = day;
        this.resourcePoolId = resourcePoolId;
        this.list = new ArrayList<>();
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DailySchedule that = (DailySchedule) o;
        DailyScheduleId thatId = new DailyScheduleId(that.getDay(), that.getResourcePoolId());
        DailyScheduleId current = new DailyScheduleId(this.day, this.resourcePoolId);
        return current.equals(thatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, resourcePoolId, list);
    }
}
