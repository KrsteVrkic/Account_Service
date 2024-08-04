package account.controllers;

import account.responses.PaymentResponse;
import account.user.AppUser;
import account.user.AppUserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@Validated
@RestController
@RequestMapping("/api/empl")
public class PaymentController {

    private final AppUserRepository userRepository;

    public PaymentController(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/payment")
    public ResponseEntity<?> getPayment(@AuthenticationPrincipal UserDetails d) {

        String email = d.getUsername();
        System.out.println("Authenticated email: " + email); // Log for debugging
        AppUser user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PaymentResponse paymentResponse = new PaymentResponse(
                user.getId(),
                user.getName(),
                user.getLastname(),
                user.getEmail()
        );
        return ResponseEntity.ok(paymentResponse);
    }

}
