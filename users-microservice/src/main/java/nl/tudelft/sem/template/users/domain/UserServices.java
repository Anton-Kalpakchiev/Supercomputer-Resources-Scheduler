package nl.tudelft.sem.template.users.domain;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public final class UserServices {

    private final transient FacultyAccountService facultyAccountService;
    private final transient FacultyVerificationService facultyVerificationService;
    private final transient RegistrationService registrationService;

    public FacultyAccountService getFacultyAccountService() {
        return facultyAccountService;
    }

    public FacultyVerificationService getFacultyVerificationService() {
        return facultyVerificationService;
    }

    public RegistrationService getRegistrationService() {
        return registrationService;
    }
}
