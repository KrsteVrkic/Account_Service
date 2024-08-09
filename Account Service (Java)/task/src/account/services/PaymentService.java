package account.services;

import account.entities.Payment;
import account.entities.PaymentID;
import account.entities.requests.PaymentRequest;
import account.entities.requests.PaymentUpdateRequest;
import account.entities.responses.PaymentResponse;
import account.entities.UserEntity;
import account.entities.responses.StatusResponse;
import account.repositories.UserRepository;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(UserRepository userRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    /**
     * @return ResponseEntity containing payment details or a list of payments if period is null.
     */
    public ResponseEntity<?> getPayment(String period) throws ParseException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
        Optional<UserEntity> user = userRepository.findUserByEmailIgnoreCase(userDetails.getUsername());
        if (user.isEmpty()) throw new EntityNotFoundException("User not found");
        if (period != null) {
            Date date = dateFormat.parse(period);
            Payment payment = paymentRepository.findById(new PaymentID(user.get().getId(), date))
                    .orElseThrow(() -> new EntityNotFoundException("Payment record not found"));
            PaymentResponse response = createPaymentResponse(user.get(), payment);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else { // Get all payments for the user if period is not specified
            List<PaymentResponse> response =
                    paymentRepository.findAllByUserIdOrderByPeriodDesc(user.get().getId()).stream()
                    .map(payment -> createPaymentResponse(user.get(), payment))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

    }

    private PaymentResponse createPaymentResponse(UserEntity user, Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setName(user.getName());
        response.setLastname(user.getLastname());
        response.setPeriod(payment.getPeriod());
        response.setSalary(payment.getSalary());
        return response;
    }

    /**
     * @param request List of PaymentRequest objects containing payment details to be posted.
     */
    @Transactional
    public ResponseEntity<?> postPayments(List<PaymentRequest> request) {
        List<Payment> payments = new ArrayList<>();
        List<UserEntity> users = request.stream()
                .map(req -> userRepository.findUserByEmailIgnoreCase(req.getEmployee().toLowerCase())
                        .orElseThrow(EntityNotFoundException::new))
                .toList();
        request.forEach(req -> {
            UserEntity user = users.stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(req.getEmployee()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("User does not exist: " + req.getEmployee()));
            Payment payment = new Payment();
            payment.setUser(user);
            payment.setPeriod(req.getDateFromString());
            payment.setSalary(req.getSalary());
            PaymentID paymentID = new PaymentID(user.getId(), payment.getPeriod());
            if (paymentRepository.existsById(paymentID)) throw new EntityExistsException();
            payments.add(payment);
        });

        paymentRepository.saveAll(payments);
        return new ResponseEntity<>(new StatusResponse("Added successfully!"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> updatePayment(PaymentUpdateRequest request) throws ParseException,
            EntityNotFoundException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
        Date date = dateFormat.parse(request.getPeriod());
        Optional<UserEntity> userOpt = userRepository.findUserByEmailIgnoreCase(request.getEmployee());
        if (userOpt.isEmpty()) throw new EntityNotFoundException();
        UserEntity user = userOpt.get();

        PaymentID paymentID = new PaymentID(user.getId(), date);
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentID);
        if (paymentOpt.isEmpty()) throw new EntityNotFoundException();
        Payment payment = paymentOpt.get();

        payment.setSalary(request.getSalary());
        paymentRepository.save(payment);
        return new ResponseEntity<>(new StatusResponse("Updated successfully!"), HttpStatus.OK);
    }
}
