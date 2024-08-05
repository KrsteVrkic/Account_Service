package account.controllers;

import account.entities.requests.ChangePasswordRequest;
import account.entities.requests.SignupRequest;
import account.entities.responses.ChangePasswordResponse;
import account.entities.responses.SignupResponse;
import account.services.AccountService;
import account.services.SignupService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AccountServiceController {

    private final SignupService signupService;
    private final AccountService accountService;

    public AccountServiceController(SignupService signupService, AccountService accountService) {
        this.signupService = signupService;
        this.accountService = accountService;
    }

    @PostMapping("/signup")
    public SignupResponse postSignup(@Valid @RequestBody SignupRequest request) {
        return signupService.signup(request);
    }

    @PostMapping("/changepass")
    public ChangePasswordResponse postChangePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return accountService.changePassword(request);
    }
}