package account.controllers;

import account.exceptions.UserExistException;
import account.requests.SignupRequest;
import account.responses.SignupResponse;
import account.user.AppUser;
import account.user.AppUserRepository;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Validated
public class AccountServiceController {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountServiceController(AppUserRepository userRepository,
                                    PasswordEncoder passwordEncoder) {
        this.appUserRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth/signup")
    public SignupResponse postSignup(@Valid @RequestBody SignupRequest r) {

        AppUser newUser = new AppUser();
        newUser.setName(r.getName());
        newUser.setLastname(r.getLastname());
        newUser.setEmail(r.getEmail());
        newUser.setRole("USER");
        newUser.setPassword(passwordEncoder.encode(r.getPassword()));

        if (appUserRepository.findUserByEmailIgnoreCase(r.getEmail()).isPresent()) {
            throw new UserExistException();
        }

        AppUser savedUser = appUserRepository.save(newUser);
        return new SignupResponse(savedUser.getId(),
                savedUser.getName(),
                savedUser.getLastname(),
                savedUser.getEmail()
        );
    }
}
