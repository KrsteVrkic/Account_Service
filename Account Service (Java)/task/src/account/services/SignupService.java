package account.services;

import account.exceptions.PasswordBreachedException;
import account.security.BreachedPasswords;
import account.exceptions.UserExistException;
import account.requests.SignupRequest;
import account.responses.SignupResponse;
import account.entity.AppUser;
import account.appuser.AppUserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SignupService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BreachedPasswords breachedPasswords;

    public SignupService(AppUserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         BreachedPasswords breachedPasswords) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.breachedPasswords = breachedPasswords;
    }

    public SignupResponse signup(SignupRequest request) {

        List<String> breachedPasswords = this.breachedPasswords.getBreachedPasswords();
        if (breachedPasswords.contains(request.getPassword())) {
            throw new PasswordBreachedException();
        }

        if (userRepository.findUserByEmailIgnoreCase(request.getEmail()).isPresent()) {
            throw new UserExistException();
        }

        AppUser savedUser = createAndSaveUser(request);
        return toSignupResponse(savedUser);
    }

    private AppUser createAndSaveUser(SignupRequest request) {
        AppUser newUser = new AppUser();
        newUser.setName(request.getName());
        newUser.setLastname(request.getLastname());
        newUser.setEmail(request.getEmail());
        newUser.setRole("USER");
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        return userRepository.save(newUser);
    }

    private SignupResponse toSignupResponse(AppUser savedUser) {
        return new SignupResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getLastname(),
                savedUser.getEmail()
        );
    }
}
