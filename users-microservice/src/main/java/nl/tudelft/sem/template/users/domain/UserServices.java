package nl.tudelft.sem.template.users.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class UserServices {

    private final transient FacultyAccountService facultyAccountService;
    private final transient FacultyVerificationService facultyVerificationService;
    private final transient RegistrationService registrationService;

    public FacultyAccountService facultyAccountService() {
        return facultyAccountService;
    }

    public FacultyVerificationService facultyVerificationService() {
        return facultyVerificationService;
    }

    public RegistrationService registrationService() {
        return registrationService;
    }
}
