package account.services;

import account.exceptions.PasswordBreachedException;
import account.exceptions.PasswordSameAsCurrentException;
import account.exceptions.UserNotFoundException;
import account.requests.ChangePasswordRequest;
import account.responses.ChangePasswordResponse;
import account.security.BreachedPasswords;
import account.user.AppUser;
import account.user.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BreachedPasswords breachedPasswords;

    public AccountService(AppUserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          BreachedPasswords breachedPasswords) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.breachedPasswords = breachedPasswords;
    }

    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Optional<AppUser> userOptional = userRepository.findUserByEmailIgnoreCase(userDetails.getUsername());
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException();
        }

        AppUser appUser = userOptional.get();
        String newPassword = request.getNewPassword();
        String encodedPassword = appUser.getPassword();

        if (passwordEncoder.matches(newPassword, encodedPassword)) {
            throw new PasswordSameAsCurrentException();
        }

        if (breachedPasswords.isBreached(newPassword)) {
            throw new PasswordBreachedException();
        }

        appUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(appUser);

        return new ChangePasswordResponse(appUser.getEmail().toLowerCase(),
                "The password has been updated successfully");
    }
}
