package account.services;

import account.exceptions.UserNotFoundException;
import account.entities.responses.PaymentResponse;
import account.entities.AppUser;
import account.repositories.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class PaymentService {

    private final AppUserRepository userRepository;

    public PaymentService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public PaymentResponse getPayment() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<AppUser> userOptional = userRepository.findUserByEmailIgnoreCase(userDetails.getUsername());

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException();
        }

        AppUser user = userOptional.get();
        return new PaymentResponse(
                user.getId(),
                user.getName(),
                user.getLastname(),
                user.getEmail()
        );
    }
}
