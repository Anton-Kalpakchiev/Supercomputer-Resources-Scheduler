package nl.tudelft.sem.template.schedule.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class DailyScheduleId implements Serializable {
    private Date day;
    private long resourcePoolId;

    public DailyScheduleId(Date day, long resourcePoolId) {
        this.day = day;
        this.resourcePoolId = resourcePoolId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyScheduleId that = (DailyScheduleId) o;
        return resourcePoolId == that.resourcePoolId && Objects.equals(day, that.day);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, resourcePoolId);
    }
}
