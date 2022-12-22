package nl.tudelft.sem.template.resourcepool.domain.dailyschedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import org.junit.jupiter.api.Test;

class DailyScheduleIdTest {

    @Test
    public void constructorTest() {
        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, 2022);
        day.set(Calendar.MONTH, 1);
        day.set(Calendar.DAY_OF_MONTH, 1);
        DailyScheduleId dsi = new DailyScheduleId(day, 1);
        assertNotNull(dsi);
    }

    @Test
    void getDay() {
        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, 2022);
        day.set(Calendar.MONTH, 1);
        day.set(Calendar.DAY_OF_MONTH, 1);
        DailyScheduleId dsi = new DailyScheduleId(day, 1);
        assertEquals(dsi.getDay(), day);
    }

    @Test
    void setDay() {
        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, 2022);
        day.set(Calendar.MONTH, 1);
        day.set(Calendar.DAY_OF_MONTH, 1);
        Calendar day2 = Calendar.getInstance();
        day2.set(Calendar.YEAR, 2021);
        day2.set(Calendar.MONTH, 2);
        day2.set(Calendar.DAY_OF_MONTH, 21);
        DailyScheduleId dsi = new DailyScheduleId(day, 1);
        dsi.setDay(day2);
        assertEquals(dsi.getDay(), day2);
    }

    @Test
    void getResourcePoolId() {
        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, 2022);
        day.set(Calendar.MONTH, 1);
        day.set(Calendar.DAY_OF_MONTH, 1);
        DailyScheduleId dsi = new DailyScheduleId(day, 1);
        assertEquals(dsi.getResourcePoolId(), 1);
    }

    @Test
    void setResourcePoolId() {
        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, 2022);
        day.set(Calendar.MONTH, 1);
        day.set(Calendar.DAY_OF_MONTH, 1);
        DailyScheduleId dsi = new DailyScheduleId(day, 1);
        dsi.setResourcePoolId(81);
        assertEquals(dsi.getResourcePoolId(), 81);
    }

    @Test
    void testEquals() {
        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, 2022);
        day.set(Calendar.MONTH, 1);
        day.set(Calendar.DAY_OF_MONTH, 1);
        Calendar day2 = Calendar.getInstance();
        day2.set(Calendar.YEAR, 2022);
        day2.set(Calendar.MONTH, 1);
        day2.set(Calendar.DAY_OF_MONTH, 1);
        DailyScheduleId dsi1 = new DailyScheduleId(day, 1);
        DailyScheduleId dsi2 = new DailyScheduleId(day2, 1);
        assertTrue(dsi1.equals(dsi2));
    }

    @Test
    void testNotEquals() {
        Calendar day = Calendar.getInstance();
        day.set(Calendar.YEAR, 2022);
        day.set(Calendar.MONTH, 1);
        day.set(Calendar.DAY_OF_MONTH, 1);
        DailyScheduleId dsi1 = new DailyScheduleId(day, 1);
        DailyScheduleId dsi2 = new DailyScheduleId(day, 26);
        assertNotEquals(dsi1, dsi2);
    }

    @Test
    void testToString() {
        Calendar day = Calendar.getInstance();
        long facId = 5L;
        DailyScheduleId dsId = new DailyScheduleId(day, facId);
        assertEquals(dsId.toString(), "DailyScheduleId{day=" + day.toString()
                + ", resourcePoolId=5}");
    }
}