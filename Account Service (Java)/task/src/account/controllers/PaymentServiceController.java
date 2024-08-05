package account.controllers;

import account.responses.PaymentResponse;
import account.services.PaymentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
