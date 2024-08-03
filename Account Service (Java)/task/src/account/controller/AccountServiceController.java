package account.controller;

import account.request.SignUpRequest;
import account.response.ErrorResponse;
import account.response.SignUpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Date;

@RestController
@RequestMapping("/api")
@Validated
public class AccountServiceController {

    @PostMapping("/auth/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (!signUpRequest.getEmail().endsWith("@acme.com")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(new Date(), 400, "Bad Request", "/api/auth/signup"));
        }

        SignUpResponse response = new SignUpResponse(
                signUpRequest.getName(),
                signUpRequest.getLastname(),
                signUpRequest.getEmail()
        );
        return ResponseEntity.ok(response);
    }
}
