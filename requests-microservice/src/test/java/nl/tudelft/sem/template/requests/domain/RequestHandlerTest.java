package nl.tudelft.sem.template.requests.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RequestHandlerTest {
    @Autowired
    private transient RegistrationService registrationService;

    @Mock
    private transient RequestRepository requestRepository;

    @MockBean
    private transient ResourcePoolService mockResourcePoolService;

    @Autowired
    private transient RequestHandler requestHandler;

    String description;
    Resources resources;
    String owner;
    String facultyName;
    Resources availableResources;
    Calendar deadline;
    Resources freePoolResources;
    String token;

    private transient AppRequest r1;
    private transient AppRequest r2;
    private transient AppRequest r3;

    @Captor
    private ArgumentCaptor<AppRequest> appRequestArgumentCaptor;

    @BeforeEach
    void setupRegister() throws InvalidResourcesException {
        description = "give me resources";
        resources = new Resources(50, 50, 50);
        owner = "The Boss";
        facultyName = "CSE";
        availableResources = new Resources(100, 100, 100);
        deadline = Calendar.getInstance();
        freePoolResources = new Resources(75, 75, 75);
        token = "token";
        requestHandler = new RequestHandler(requestRepository, mockResourcePoolService);

        appRequestArgumentCaptor = ArgumentCaptor.forClass(AppRequest.class);
        r1 = registrationService.registerRequest(description, resources, owner,
                facultyName, availableResources, deadline, freePoolResources, token);
        r2 = registrationService.registerRequest("don't return because of status",
                resources, owner, facultyName, availableResources, deadline, freePoolResources, token);
        r3 = registrationService.registerRequest("don't return because of facultyName",
                resources, "owner2", "other", availableResources, deadline, freePoolResources, token);
    }

    @Test
    void getPendingResources() throws InvalidResourcesException {
        // Act
        r1.setStatus(0);
        r2.setStatus(1);
        r3.setStatus(0);
        ArrayList<AppRequest> expected = new ArrayList<>();
        expected.add(r1);

        when(requestRepository.findAll()).thenReturn(List.of(r1, r2, r3));

        List<AppRequest> result =  requestHandler.getPendingRequestsForFacultyName(facultyName);
        // Assert
        assertEquals(expected, result);
        assertFalse(result.contains(r2));
        assertFalse(result.contains(r3));
    }

    @Test
    public void getResourcesForIdException() {
        long requestId = 0L;
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> requestHandler.getResourcesForId(requestId));
    }

    @Test
    void getRequestIdsByNetIdTest() {
        when(requestRepository.findAll()).thenReturn(List.of(r1, r2, r3));
        assertEquals((r1.getId() + "/" + r2.getId()),
                requestHandler.getRequestIdsByNetId(owner));
    }

    @Test
    void registerRequest0Test() {
        requestHandler.registerRequestOnceStatusDecided(0, r1, token);

        verify(requestRepository).save(appRequestArgumentCaptor.capture());

        assertEquals(0, appRequestArgumentCaptor.getValue().getStatus());
    }


    @Test
    void registerRequest1Test() {

        requestHandler.registerRequestOnceStatusDecided(1, r1, token);

        verify(mockResourcePoolService, times(2)).approval(any(),
                eq(r1.getId()), eq(true), eq(token));
        verify(requestRepository).save(appRequestArgumentCaptor.capture());

        assertEquals(1, appRequestArgumentCaptor.getValue().getStatus());
    }

    @Test
    void registerRequest2Test() {
        requestHandler.registerRequestOnceStatusDecided(2, r1, token);

        verify(requestRepository).save(appRequestArgumentCaptor.capture());

        assertEquals(2, appRequestArgumentCaptor.getValue().getStatus());
    }

    @Test
    void registerRequest3Test() {

        requestHandler.registerRequestOnceStatusDecided(3, r1, token);

        verify(requestRepository).save(appRequestArgumentCaptor.capture());

        assertEquals(3, appRequestArgumentCaptor.getValue().getStatus());
    }


}
