package account.services;
import account.entities.Payment;
import account.entities.PaymentID;
import account.entities.requests.PaymentRequest;
import account.entities.requests.PaymentUpdateRequest;
import account.entities.responses.ErrorResponse;
import account.entities.responses.PaymentResponse;
import account.entities.AppUser;
import account.entities.responses.StatusResponse;
import account.repositories.AppUserRepository;
import account.repositories.PaymentRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            try {
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
                if (response.getPeriod() == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
                            LocalDateTime.now(),
                            HttpStatus.NOT_FOUND.value(),
                            "Not Found",
                            "Payment record not found",
                            "/api/empl/payment"
                    ));
                }
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (ParseException e) {
                throw e;
            }
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
            // Validate and collect users
            List<AppUser> users = request.stream()
                    .map(req -> userRepository.findUserByEmailIgnoreCase(req.getEmployee().toLowerCase())
                            .orElseThrow(() -> new EntityNotFoundException("User does not exist: " + req.getEmployee())))
                    .collect(Collectors.toList());

            // Create and validate Payment objects
            for (int i = 0; i < request.size(); i++) {
                PaymentRequest req = request.get(i);
                AppUser user = users.get(i);
                Payment payment = new Payment();
                payment.setUser(user);
                payment.setPeriod(req.getDateFromString());  // Use getDateFromString to set period
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/acct/payments"
            ));
        } catch (ConstraintViolationException e) {
            // Handle validation exceptions
            String messages = e.getConstraintViolations().stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    messages,
                    "/api/acct/payments"
            ));
        }
    }


    @Transactional
    public ResponseEntity<?> updatePayment(PaymentUpdateRequest request) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
            Date date = dateFormat.parse(request.getPeriod());

            // Find user
            Optional<AppUser> userOpt = userRepository.findUserByEmailIgnoreCase(request.getEmployee().toLowerCase());
            if (userOpt.isEmpty()) {
                throw new EntityNotFoundException("User does not exist: " + request.getEmployee());
            }
            AppUser user = userOpt.get();

            // Find payment
            PaymentID paymentID = new PaymentID(user.getId(), date);
            Optional<Payment> paymentOpt = paymentRepository.findById(paymentID);
            if (paymentOpt.isEmpty()) {
                throw new EntityNotFoundException("Payment does not exist for this user and period");
            }
            Payment payment = paymentOpt.get();

            // Update salary
            payment.setSalary(request.getSalary());
            paymentRepository.save(payment);

            return new ResponseEntity<>(new StatusResponse("Updated successfully!"), HttpStatus.OK);

        } catch (EntityNotFoundException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/acct/payments"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "An unexpected error occurred",
                    "/api/acct/payments"
            ));
        }
    }
}
