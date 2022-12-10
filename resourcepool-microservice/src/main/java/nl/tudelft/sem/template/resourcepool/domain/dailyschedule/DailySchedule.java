package nl.tudelft.sem.template.resourcepool.domain.dailyschedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;

import lombok.NoArgsConstructor;

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

    @ElementCollection
    @Column(name = "list_request_id", nullable = false)
    private List<Long> list;

    /**
     * Create a new DailySchedule per Resource Pool.
     *
     * @param day            the day that the requests are scheduled on
     * @param resourcePoolId the id of the resource pool the requests are scheduled in
     */
    public DailySchedule(Date day, long resourcePoolId) {
        this.day = day;
        this.resourcePoolId = resourcePoolId;
        this.list = new ArrayList<>();
    }

    /**
     * Gets day.
     *
     * @return the day
     */
    public Date getDay() {
        return day;
    }

    /**
     * Gets resource pool id.
     *
     * @return the resource pool id
     */
    public long getResourcePoolId() {
        return resourcePoolId;
    }

    /**
     * Gets list with all the request ids.
     *
     * @return the list
     */
    public List<Long> getList() {
        return list;
    }

    /**
     * Adds the id of a request to the list.
     *
     * @param requestId the request id
     */
    public void addRequest(long requestId) {
        this.list.add(requestId);
    }

    /**
     * Equality is only based on the identifier.
     *
     * @return whether the daily schedules are equal
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

    /**
     * Returns the hash code value for this daily schedule.
     *
     * @return the hash code value for this daily schedule
     */
    @Override
    public int hashCode() {
        return Objects.hash(day, resourcePoolId, list);
    }

    /**
     * Returns a string representation for this daily schedule.
     *
     * @return a string representation for this daily schedule
     */
    @Override
    public String toString() {
        return "DailySchedule{"
                + "day=" + day
                + ", resourcePoolId=" + resourcePoolId
                + ", list=" + list
                + '}';
    }
}
