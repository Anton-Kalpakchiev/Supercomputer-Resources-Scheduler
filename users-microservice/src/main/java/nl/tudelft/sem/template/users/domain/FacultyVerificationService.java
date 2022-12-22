package nl.tudelft.sem.template.users.domain;

import java.util.Objects;
import lombok.AllArgsConstructor;
import nl.tudelft.sem.template.users.models.VerifyFacultyRequestModel;
import nl.tudelft.sem.template.users.models.VerifyFacultyResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class FacultyVerificationService {

    private RestTemplate restTemplate;

    /**
     * Verifies whether a faculty actually exists.
     *
     * @param facultyId the faculty id to be verified
     * @param token the authentication token
     * @return whether a faculty exists
     * @throws FacultyException thrown when faculty does not exist
     */
    public boolean verifyFaculty(long facultyId, String token) throws FacultyException {
        String url = "http://localhost:8085/verifyFaculty";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<VerifyFacultyRequestModel> entity = new HttpEntity<>(new VerifyFacultyRequestModel(facultyId), headers);

        ResponseEntity<VerifyFacultyResponseModel> result = restTemplate.postForEntity(url, entity,
                VerifyFacultyResponseModel.class);

        if (result.getStatusCode().is2xxSuccessful() && Objects.requireNonNull(result.getBody()).isVerified()) {
            return true;
        } else {
            throw new FacultyException("The faculty the user wants to be employed at does not exist");
        }
    }
}
