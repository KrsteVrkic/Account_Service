package account.services;
import account.exceptions.PasswordBreachedException;
import account.exceptions.PasswordEqualsException;
import account.exceptions.UserNotFoundException;
import account.entities.requests.ChangePasswordRequest;
import account.entities.responses.ChangePasswordResponse;
import account.security.BreachedPasswords;
import account.entities.UserEntity;
import account.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AccountService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BreachedPasswords breachedPasswords;
    public AccountService(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          BreachedPasswords breachedPasswords) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.breachedPasswords = breachedPasswords;
    }

    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<UserEntity> userOptional = userRepository.findUserByEmailIgnoreCase(userDetails.getUsername());
        if (userOptional.isEmpty()) throw new UserNotFoundException();
        UserEntity userEntity = userOptional.get();
        String newPassword = request.getNewPassword();
        String encodedPassword = userEntity.getPassword();
        if (passwordEncoder.matches(newPassword, encodedPassword)) throw new PasswordEqualsException();
        if (breachedPasswords.isBreached(newPassword)) throw new PasswordBreachedException();
        userEntity.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(userEntity);
        return new ChangePasswordResponse(userEntity
                .getEmail()
                .toLowerCase(),
                "The password has been updated successfully");
    }
}
