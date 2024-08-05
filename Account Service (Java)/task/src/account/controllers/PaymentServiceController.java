package account.controllers;

import account.responses.PaymentResponse;
import account.services.PaymentService;
import account.user.AppUser;
import account.user.AppUserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@Validated
@RestController
@RequestMapping("/api")
public class PaymentServiceController {

    private final PaymentService paymentService;

    public PaymentServiceController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/empl/payment")
    public PaymentResponse getPayment() {
        return paymentService.getPayment();
    }




























}
