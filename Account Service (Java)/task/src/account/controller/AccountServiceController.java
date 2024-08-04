package account.controller;

import account.exception.UserExistException;
import account.request.SignUpRequest;
import account.response.ErrorResponse;
import account.response.SignUpResponse;
import account.user.AppUser;
import account.user.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Date;


@RestController
@RequestMapping("/api")
@Validated
public class AccountServiceController {

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AccountServiceController(AppUserRepository appUserRepository, BCryptPasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (!signUpRequest.getEmail().endsWith("@acme.com")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(
                            new Date(),
                            400,
                            "Bad Request",
                            "/api/auth/signup")
                    );
        }

        var doesUserExist = appUserRepository.findAppUserByName(signUpRequest.getName());
        if (doesUserExist.isPresent()) throw new UserExistException();

        AppUser newUser = new AppUser();
        newUser.setName(signUpRequest.getName());
        newUser.setLastname(signUpRequest.getLastname());
        newUser.setEmail(signUpRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        AppUser savedUser = appUserRepository.save(newUser);

        SignUpResponse signUpResponse = new SignUpResponse(
                savedUser.getName(),
                savedUser.getLastname(),
                savedUser.getEmail(),
                savedUser.getId()
        );
        return ResponseEntity.status(HttpStatus.OK).body(signUpResponse);
    }
}