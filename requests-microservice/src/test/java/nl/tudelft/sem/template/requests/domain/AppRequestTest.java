package nl.tudelft.sem.template.requests.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Objects;
import org.junit.jupiter.api.Test;

class AppRequestTest {
    @Test
    public void constructorTest() throws InvalidResourcesException {
        String description = "give me resources";
        Resources resources = new Resources(100, 100, 100);
        String owner = "user";
        String facultyName = "cse";
        Calendar deadline = Calendar.getInstance();

        AppRequest appRequest = new AppRequest(description, resources, owner,
                facultyName, deadline, -1);

        assertEquals(appRequest.getDescription(), description);
        assertEquals(appRequest.getMem(), resources.getMem());
        assertEquals(appRequest.getCpu(), resources.getCpu());
        assertEquals(appRequest.getGpu(), resources.getGpu());
        assertEquals(appRequest.getOwner(), owner);
        assertEquals(appRequest.getFacultyName(), facultyName);
        assertEquals(appRequest.getDeadline(), deadline);
    }

    @Test
    public void equalsTest() throws InvalidResourcesException {
        String description = "give me resources";
        Resources resources = new Resources(100, 100, 100);
        String owner = "user";
        String facultyName = "cse";
        Calendar deadline = Calendar.getInstance();

        AppRequest appRequest = new AppRequest(description, resources, owner,
                facultyName, deadline, -1);

        AppRequest theSame = new AppRequest(description, resources, owner,
                facultyName, deadline, -1);

        assertTrue(appRequest.equals(theSame));
    }

    @Test
    public void hashTest() throws InvalidResourcesException {
        String description = "give me resources";
        Resources resources = new Resources(100, 100, 100);
        String owner = "user";
        String facultyName = "cse";
        Calendar deadline = Calendar.getInstance();

        AppRequest appRequest = new AppRequest(description, resources, owner,
                facultyName, deadline, -1);

        int expectedHash = Objects.hash(owner);

        assertEquals(appRequest.hashCode(), expectedHash);

    }


}