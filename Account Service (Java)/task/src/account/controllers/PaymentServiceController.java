package account.controllers;

import account.entities.requests.PaymentRequest;
import account.entities.requests.PaymentUpdateRequest;
import account.services.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.text.ParseException;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api")
public class PaymentServiceController {

    private final PaymentService paymentService;
    public PaymentServiceController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/empl/payment")
    public ResponseEntity<?> getPayment(@RequestParam(required = false) @Valid @Pattern(regexp = "(^(0[1-9]|1[0-2])-" +
            "(19|20)\\d{2}$)") String period) throws ParseException {
        return paymentService.getPayment(period);
    }

    @PostMapping("/acct/payments")
    public ResponseEntity<?> postPayments(@Valid @RequestBody List<@Valid PaymentRequest> request) {
        return paymentService.postPayments(request);
    }

    @PutMapping("/acct/payments")
    public ResponseEntity<?> putPayment(@Valid @RequestBody PaymentUpdateRequest request) throws ParseException {
        return paymentService.updatePayment(request);
    }
}
