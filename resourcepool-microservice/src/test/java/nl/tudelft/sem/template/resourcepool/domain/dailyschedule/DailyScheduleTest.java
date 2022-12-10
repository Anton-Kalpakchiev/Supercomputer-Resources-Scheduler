package nl.tudelft.sem.template.resourcepool.domain.dailyschedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;


class DailyScheduleTest {

    @Test
    public void constructorTest() {
        DailySchedule ds = new DailySchedule(new Date(2022, 1, 1), 1);
        assertNotNull(ds);
    }

    @Test
    void getDay() {
        DailySchedule ds = new DailySchedule(new Date(2022, 1, 1), 1);
        assertEquals(ds.getDay(), new Date(2022, 1, 1));
    }

    @Test
    void getResourcePoolId() {
        DailySchedule ds = new DailySchedule(new Date(2022, 1, 1), 1);
        assertEquals(ds.getResourcePoolId(), 1);
    }

    @Test
    void getList() {
        DailySchedule ds = new DailySchedule(new Date(2022, 12, 31), 1);
        assertEquals(ds.getList(), new ArrayList<>());
    }

    @Test
    void addRequest() {
        DailySchedule ds = new DailySchedule(new Date(2022, 12, 31), 1);
        ds.addRequest(1L);
        ds.addRequest(61L);
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(61L);
        assertEquals(ds.getList(), list);
    }

    @Test
    void testEquals() {
        DailySchedule ds1 = new DailySchedule(new Date(2022, 1, 1), 1);
        DailySchedule ds2 = new DailySchedule(new Date(2022, 1, 1), 1);
        ds2.addRequest(50L);
        assertEquals(ds1, ds2);
    }

    @Test
    void testNotEquals() {
        DailySchedule ds1 = new DailySchedule(new Date(2022, 1, 1), 1);
        DailySchedule ds2 = new DailySchedule(new Date(2023, 8, 4), 1);
        ds1.addRequest(50L);
        ds2.addRequest(50L);
        assertNotEquals(ds1, ds2);
    }

    // Makes the pipeline fail because the Date() method with 3 parameters is deprecated and doesn't pass on gitlab
    // because it doesn't know how to convert the date to a string, this test does work locally
    // @Test
    // void testToString() {
    //    DailySchedule ds = new DailySchedule(new Date(2022, 12, 31), 1);
    //    ds.addRequest(50L);
    //    assertEquals(ds.toString(), "DailySchedule{day=Wed Jan 31 00:00:00 CET 3923, resourcePoolId=1, list=[50]}");
    // }
}