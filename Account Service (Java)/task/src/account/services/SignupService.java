package account.services;

import account.entities.*;
import account.security.BreachedPasswords;
import account.exceptions.PasswordBreachedException;
import account.exceptions.UserExistException;
import account.repositories.GroupRepository;
import account.repositories.UserRepository;
import account.entities.requests.SignupRequest;
import account.entities.responses.SignupResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static account.security.Role.*;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SignupService {

    private final BreachedPasswords breachedPasswords;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupService(BreachedPasswords breachedPasswords, UserRepository userRepository, GroupRepository groupRepository, PasswordEncoder passwordEncoder) {

        this.breachedPasswords = breachedPasswords;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        if (breachedPasswords.isBreached(request.getPassword())) throw new PasswordBreachedException();
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) throw new UserExistException();

        UserEntity userEntity = new UserEntity(
                request.getName(),
                request.getLastname(),
                request.getEmail().toLowerCase(),
                passwordEncoder.encode(request.getPassword())
        );

        Group adminGroup = groupRepository.findByCode(ADMINISTRATOR.getRole()).get();
        Group userGroup = groupRepository.findByCode(USER.getRole()).get();
        Group groupToAdd = userRepository.count() == 0 ? adminGroup : userGroup;

        userEntity.setUserGroups(Set.of(groupToAdd));
        userEntity.setAccountVerified(true);
        userRepository.save(userEntity);

        return new SignupResponse(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getLastname(),
                userEntity.getEmail(),
                userEntity.getUserGroups()
                        .stream()
                        .map(r -> "ROLE_" + r.getCode())
                        .sorted()
                        .collect(Collectors.toList())
        );
    }
}
