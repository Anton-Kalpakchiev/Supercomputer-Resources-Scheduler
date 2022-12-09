package nl.tudelft.sem.template.resourcepool.domain.dailyschedule;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class DailyScheduleId implements Serializable {
    static final long serialVersionUID = 512472699;
    private transient Date day;
    private transient long resourcePoolId;

    public DailyScheduleId(Date day, long resourcePoolId) {
        this.day = day;
        this.resourcePoolId = resourcePoolId;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(day, resourcePoolId);
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public long getResourcePoolId() {
        return resourcePoolId;
    }

    public void setResourcePoolId(long resourcePoolId) {
        this.resourcePoolId = resourcePoolId;
    }
}
