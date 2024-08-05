package account.services;

import account.entities.Payment;
import account.entities.PaymentID;
import account.entities.requests.PaymentRequest;
import account.entities.responses.PaymentResponse;
import account.entities.AppUser;
import account.entities.responses.StatusResponse;
import account.repositories.AppUserRepository;
import account.repositories.PaymentRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import account.entities.responses.ErrorResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final AppUserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(AppUserRepository userRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    public ResponseEntity<?> getPayment(String period) throws ParseException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (period != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
            Date date = dateFormat.parse(period);
            PaymentResponse response = new PaymentResponse();
            Optional<AppUser> user = userRepository.findUserByEmailIgnoreCase(userDetails.getUsername().toLowerCase());
            user.ifPresent(u -> paymentRepository.findById(new PaymentID(u.getId(), date)).ifPresent(r -> {
                response.setPeriod(r.getPeriod());
                response.setSalary(r.getSalary());
                response.setLastname(u.getLastname());
                response.setName(u.getName());
            }));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            List<PaymentResponse> response = new ArrayList<>();
            Optional<AppUser> user = userRepository.findUserByEmailIgnoreCase(userDetails.getUsername().toLowerCase());
            user.ifPresent(v -> paymentRepository.findAllByUserIdOrderByPeriodDesc(v.getId()).forEach(r -> {
                PaymentResponse result = new PaymentResponse();
                result.setName(v.getName());
                result.setLastname(v.getLastname());
                result.setPeriod(r.getPeriod());
                result.setSalary(r.getSalary());
                response.add(result);
            }));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @Transactional
    public ResponseEntity<?> postPayments(List<PaymentRequest> request) {
        List<Payment> payments = new ArrayList<>();

        try {
            // First, find all the users associated with the requests
            List<AppUser> users = new ArrayList<>();
            for (PaymentRequest req : request) {
                Optional<AppUser> userOpt = userRepository.findUserByEmailIgnoreCase(req.getEmployee().toLowerCase());
                if (userOpt.isEmpty()) {
                    throw new EntityNotFoundException("User does not exist: " + req.getEmployee());
                }
                users.add(userOpt.get());
            }

            // Now, create and validate Payment objects
            for (int i = 0; i < request.size(); i++) {
                PaymentRequest req = request.get(i);
                AppUser user = users.get(i);
                Payment payment = new Payment();
                payment.setUser(user);
                payment.setPeriodFromString(req.getPeriod());
                payment.setSalary(req.getSalary());

                PaymentID paymentID = new PaymentID(user.getId(), payment.getPeriod());
                if (paymentRepository.existsById(paymentID)) {
                    throw new EntityExistsException("Payment already exists for this user and period");
                }
                payments.add(payment);
            }

            paymentRepository.saveAll(payments);
            return new ResponseEntity<>(new StatusResponse("Added successfully!"), HttpStatus.OK);

        } catch (EntityNotFoundException | EntityExistsException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse(LocalDateTime.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            "Bad Request",
                            e.getMessage(),
                            "/api/acct/payments")
            );
        }
    }
}






