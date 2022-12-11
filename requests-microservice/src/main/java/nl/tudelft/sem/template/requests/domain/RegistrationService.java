package nl.tudelft.sem.template.requests.domain;

import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private final transient RequestRepository requestRepository;

    /**
     * Instantiates a new RegistrationService.
     *
     * @param requestRepository the request repository
     */
    public RegistrationService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    /**
     * Register a new request.
     *
     * @param description The description of the request
     * @param resources   The resorces requested
     */
    public AppRequest registerRequest(String description, Resources resources, String owner) {
        // Create new request
        AppRequest request = new AppRequest(description, resources, owner);
        requestRepository.save(request);

        return request;
    }
}

