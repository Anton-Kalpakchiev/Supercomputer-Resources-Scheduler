package nl.tudelft.sem.template.requests.domain;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
//We can remove this line later on, but I can't figure out how to fix this and the code works perfect with the error in it
@SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.AvoidLiteralsInIfCondition"})
public class RequestHandler {
    private final transient RequestRepository requestRepository;
    private final transient ResourcePoolService resourcePoolService;

    public RequestHandler(RequestRepository requestRepository, ResourcePoolService resourcePoolService) {
        this.requestRepository = requestRepository;
        this.resourcePoolService = resourcePoolService;
    }

    /**
     * Gets the requested resources.
     *
     * @param requestId the id of the requested
     * @return the requested resources
     */
    public Resources getResourcesForId(long requestId) {
        Optional<AppRequest> optional = requestRepository.findById(requestId);
        if (optional.isPresent()) {
            AppRequest request = optional.get();
            Resources resources = new Resources(request.getCpu(), request.getGpu(), request.getMem());
            return resources;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Gets the set of all IDs of resource requests made by a given user.
     *
     * @param netId the netid of the user
     * @return a String that has the encoded IDs
     */
    public String getRequestIdsByNetId(String netId) {
        List<Long> ids = requestRepository.findAll().stream().filter(x -> x.getOwner().equals(netId))
                .map(x -> x.getId()).collect(Collectors.toList());
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            long id = ids.get(i);
            answer.append(id);
            if (i != ids.size() - 1) {
                answer.append("/");
            }
        }
        return answer.toString();
    }

    /**
     * Gets the pending requests for the facultyName.
     *
     * @param facultyName the facultyName for which the pending requests need to be retrieved
     * @return the list of pending requests
     */
    public List<AppRequest> getPendingRequestsForFacultyName(String facultyName) {
        return requestRepository.findAll().stream().filter(x -> x.getFacultyName().equals(facultyName) && x.getStatus() == 0)
                .collect(Collectors.toList());
    }

    /**
     * Once the status is decided in the registerRequest method, the actual registering of the request
     * and all communication happens here.
     *
     * @param status  the status of the request
     * @param request the request
     * @param token   the JWT token
     */
    public void registerRequestOnceStatusDecided(int status, AppRequest request, String token) {
        if (status == 0) {
            //pending for manual review
            request.setStatus(0);
            requestRepository.save(request);
        } else if (status == 1) {
            //auto approve
            request.setStatus(1);
            requestRepository.save(request);
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_MONTH, 1);
            resourcePoolService.approval(tomorrow, request.getId(), true, token);
        } else if (status == 2) {
            //auto reject
            request.setStatus(2);
            requestRepository.save(request);
        } else {
            //pending for the FRP to get more resources at 6h before end of day
            request.setStatus(3);
            requestRepository.save(request);
        }
    }
}
