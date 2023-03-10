package nl.tudelft.sem.template.resourcepool.domain.dailyschedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.resourcepool.domain.resources.Resources;
import nl.tudelft.sem.template.resourcepool.domain.resources.ResourcesAttributeConverter;

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
    private Calendar day;

    @Id
    @Column(name = "resource_pool_id", nullable = false)
    private long resourcePoolId;

    @ElementCollection
    @Column(name = "list_request_id", nullable = false)
    private List<Long> list;

    @Convert(converter = ResourcesAttributeConverter.class)
    @Column(name = "available_resources", nullable = true)
    private Resources availableResources;

    @Convert(converter = ResourcesAttributeConverter.class)
    @Column(name = "total_resources", nullable = true)
    private Resources totalResources;

    /**
     * Create a new DailySchedule per Resource Pool.
     *
     * @param day            the day that the requests are scheduled on
     * @param resourcePoolId the id of the resource pool the requests are scheduled in
     */
    public DailySchedule(Calendar day, long resourcePoolId) {
        Calendar thisDay = Calendar.getInstance();
        thisDay.setTimeInMillis(0);
        thisDay.set(Calendar.YEAR, day.get(Calendar.YEAR));
        thisDay.set(Calendar.MONTH, day.get(Calendar.MONTH));
        thisDay.set(Calendar.DAY_OF_MONTH, day.get(Calendar.DAY_OF_MONTH));

        this.day = thisDay;
        this.resourcePoolId = resourcePoolId;
        this.availableResources = new Resources(0, 0, 0);
        this.totalResources = new Resources(0, 0, 0);
        this.list = new ArrayList<>();
    }

    /**
     * Gets day.
     *
     * @return the day
     */
    public Calendar getDay() {
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
     * Gets the available resources.
     *
     * @return the available resources
     */
    public Resources getAvailableResources() {
        return availableResources;
    }

    /**
     * Sets the available resources.
     *
     * @param availableResources the available resources for this day and faculty
     */
    public void setAvailableResources(Resources availableResources) {
        this.availableResources = availableResources;
    }

    /**
     * Gets the total amount of resources that were available for this day.
     *
     * @return the total amount of resources
     */
    public Resources getTotalResources() {
        return totalResources;
    }

    /**
     * Sets the total amount of resources.
     *
     * @param totalResources the total amount of resources that need to be set
     */
    public void setTotalResources(Resources totalResources) {
        this.totalResources = totalResources;
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

    /**
     * Returns a human-readable string representation for this daily schedule.
     *
     * @return a human-readble string representation for this daily schedule
     */
    public String toPrettyString() {
        StringBuilder stringBuilder = new StringBuilder("DailySchedule:");
        stringBuilder.append("\n\t Resource Pool:").append(this.resourcePoolId);
        stringBuilder.append("\n\t Date: ").append(this.day.get(Calendar.DAY_OF_MONTH))
                .append("/").append(this.day.get(Calendar.MONTH))
                .append("/").append(this.day.get(Calendar.YEAR));
        stringBuilder.append("\n\t Total Resources: ").append(this.totalResources.toString());
        stringBuilder.append("\n\t Available Resources: ").append(this.availableResources.toString());
        stringBuilder.append("\n\t List of requests: ").append(this.list.toString());
        stringBuilder.append("\n\t");
        return stringBuilder.toString();
    }
}
