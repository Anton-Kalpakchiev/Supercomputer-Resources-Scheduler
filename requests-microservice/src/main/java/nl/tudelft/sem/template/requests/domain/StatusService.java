package nl.tudelft.sem.template.requests.domain;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StatusService {
    private final transient RequestRepository requestRepository;

    /**
     * Instantiates a new RegistrationService.
     *
     * @param requestRepository the request repository
     */
    public StatusService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    /**
     * Get the status of a request by id.
     *
     * @param id The id of the request.
     * @return The status of the current request
     */
    public int getStatus(long id) {
        return requestRepository.findById(id).get().getStatus();
    }

    /**
     * Set the status of a request by id.
     *
     * @param id The id of the request.
     * @param status The new status to which the request should be changed.
     */
    public void setStatus(long id, int status) {
        AppRequest request = requestRepository.findById(id).get();
        request.setStatus(status);
        requestRepository.save(request);

    }
}

