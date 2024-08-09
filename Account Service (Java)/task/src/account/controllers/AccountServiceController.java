package account.controllers;

import account.entities.requests.ChangePasswordRequest;
import account.entities.requests.SignupRequest;
import account.entities.responses.ChangePasswordResponse;
import account.entities.responses.SignupResponse;
import account.entities.requests.RoleChangeRequest;
import account.services.AccountService;
import account.services.SignupService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api")
public class AccountServiceController {

    private final SignupService signupService;
    private final AccountService accountService;

    public AccountServiceController(SignupService signupService, AccountService accountService) {
        this.signupService = signupService;
        this.accountService = accountService;
    }

    @PostMapping("/auth/signup")
    public SignupResponse postSignup(@Valid @RequestBody SignupRequest request) {
        return signupService.signup(request);
    }

    @PostMapping("/auth/changepass")
    public ChangePasswordResponse postChangePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return accountService.changePassword(request);
    }

    @GetMapping("/admin/user/")
    public List<SignupResponse> getAllUsers() {
        return accountService.getAllUsers();
    }

    @DeleteMapping("/admin/user/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        return accountService.deleteUserByEmail(email);
    }

    @PutMapping("admin/user/role")
    public ResponseEntity<?> changeUserRole(@Valid @RequestBody RoleChangeRequest request) {
        return accountService.changeUserRole(request);
    }
}