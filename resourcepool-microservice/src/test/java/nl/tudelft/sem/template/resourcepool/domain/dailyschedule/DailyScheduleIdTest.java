package nl.tudelft.sem.template.resourcepool.domain.dailyschedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import org.junit.jupiter.api.Test;

class DailyScheduleIdTest {

    @Test
    public void constructorTest() {
        DailyScheduleId dsi = new DailyScheduleId(new Date(2022, 1, 1), 1);
        assertNotNull(dsi);
    }

    @Test
    void getDay() {
        DailyScheduleId dsi = new DailyScheduleId(new Date(2022, 1, 1), 1);
        assertEquals(dsi.getDay(), new Date(2022, 1, 1));
    }

    @Test
    void setDay() {
        DailyScheduleId dsi = new DailyScheduleId(new Date(2022, 1, 1), 1);
        dsi.setDay(new Date(2021, 2, 21));
        assertEquals(dsi.getDay(), new Date(2021, 2, 21));
    }

    @Test
    void getResourcePoolId() {
        DailyScheduleId dsi = new DailyScheduleId(new Date(2022, 1, 1), 1);
        assertEquals(dsi.getResourcePoolId(), 1);
    }

    @Test
    void setResourcePoolId() {
        DailyScheduleId dsi = new DailyScheduleId(new Date(2022, 1, 1), 1);
        dsi.setResourcePoolId(81);
        assertEquals(dsi.getResourcePoolId(), 81);
    }

    @Test
    void testEquals() {
        DailyScheduleId dsi1 = new DailyScheduleId(new Date(2022, 1, 1), 1);
        DailyScheduleId dsi2 = new DailyScheduleId(new Date(2022, 1, 1), 1);
        assertEquals(dsi1, dsi2);
    }

    @Test
    void testNotEquals() {
        DailyScheduleId dsi1 = new DailyScheduleId(new Date(2022, 1, 1), 1);
        DailyScheduleId dsi2 = new DailyScheduleId(new Date(2022, 1, 1), 26);
        assertNotEquals(dsi1, dsi2);
    }

    @Test
    void testToString() {
        DailyScheduleId dsi = new DailyScheduleId(new Date(2022, 12, 31), 1);
        assertEquals(dsi.toString(), "DailyScheduleId{day=Wed Jan 31 00:00:00 CET 3923, resourcePoolId=1}");
    }
}