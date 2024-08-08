package account.services;

import account.entities.UserEntity;
import account.entities.Group;
import account.exceptions.UserExistException;
import account.repositories.GroupRepository;
import account.repositories.UserRepository;
import account.entities.requests.SignupRequest;
import account.entities.responses.SignupResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class SignupService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupService(UserRepository userRepository, GroupRepository groupRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserExistException();
        }

        UserEntity userEntity = new UserEntity(
                request.getName(),
                request.getLastname(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );

        Set<Group> roles = new HashSet<>();
        if (userRepository.count() == 0) {
            Group adminGroup = groupRepository.findByCode("ROLE_ADMINISTRATOR");
            roles.add(adminGroup);
        } else {
            Group userGroup = groupRepository.findByCode("ROLE_USER");
            roles.add(userGroup);
        }
        userEntity.setUserGroups(roles);

        userRepository.save(userEntity);

        // Create response
        return new SignupResponse(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getLastname(),
                userEntity.getEmail(),
                userEntity.getUserGroups().stream()
                        .map(Group::getCode)
                        .sorted()
                        .toArray(String[]::new)
        );
    }
}
