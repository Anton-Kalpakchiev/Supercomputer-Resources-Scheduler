package nl.tudelft.sem.template.resourcepool.domain.dailyschedule;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * The composite key for a DailySchedule.
 */
public class DailyScheduleId implements Serializable {

    static final long serialVersionUID = 512472699;
    private transient Date day;
    private transient long resourcePoolId;

    /**
     * Instantiates a new DailyScheduleId.
     *
     * @param day            the day
     * @param resourcePoolId the resource pool id
     */
    public DailyScheduleId(Date day, long resourcePoolId) {
        this.day = day;
        this.resourcePoolId = resourcePoolId;
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
     * Sets day.
     *
     * @param day the day
     */
    public void setDay(Date day) {
        this.day = day;
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
     * Sets resource pool id.
     *
     * @param resourcePoolId the resource pool id
     */
    public void setResourcePoolId(long resourcePoolId) {
        this.resourcePoolId = resourcePoolId;
    }

    /**
     * Returns whether the daily schedules ids are equal.
     *
     * @return whether the daily schedules ids are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DailyScheduleId that = (DailyScheduleId) o;
        return resourcePoolId == that.resourcePoolId && Objects.equals(day, that.day);
    }

    /**
     * Returns the hash code value for this daily schedule id.
     *
     * @return the hash code value for this daily schedule id
     */
    @Override
    public int hashCode() {
        return Objects.hash(day, resourcePoolId);
    }

    @Override
    public String toString() {
        return "DailyScheduleId{"
                + "day=" + day
                + ", resourcePoolId=" + resourcePoolId
                + '}';
    }
}
