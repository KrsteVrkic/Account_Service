package account.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import account.entities.Group;
import account.entities.responses.SignupResponse;
import account.exceptions.AdminDeletionException;
import account.exceptions.PasswordBreachedException;
import account.exceptions.PasswordEqualsException;
import account.exceptions.UserNotFoundException;
import account.entities.requests.ChangePasswordRequest;
import account.entities.responses.ChangePasswordResponse;
import account.repositories.GroupRepository;
import account.security.BreachedPasswords;
import account.entities.UserEntity;
import account.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import account.entities.requests.RoleChangeRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AccountService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BreachedPasswords breachedPasswords;
    private final GroupRepository groupRepository;

    public AccountService(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          BreachedPasswords breachedPasswords, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.breachedPasswords = breachedPasswords;
        this.groupRepository = groupRepository;
    }

    public ChangePasswordResponse changePassword(ChangePasswordRequest request, UserDetails userDetails) {

        if (breachedPasswords.isBreached(request.getNewPassword())) {
            throw new PasswordBreachedException();
        }

        UserEntity userEntity = userRepository.findUserByEmailIgnoreCase(userDetails.getUsername())
                .orElseThrow(EntityNotFoundException::new);

        if (passwordEncoder.matches(request.getNewPassword(), userEntity.getPassword())) {
            throw new PasswordEqualsException();
        }

        userEntity.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(userEntity);

        return new ChangePasswordResponse(
                userDetails.getUsername().toLowerCase(),
                "The password has been updated successfully"
        );
    }

    public List<SignupResponse> getAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(user -> new SignupResponse(
                        user.getId(),
                        user.getName(),
                        user.getLastname(),
                        user.getEmail(),
                        user.getUserGroups().stream().map(Group::getCode).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> deleteUserByEmail(String email) {
        Optional<UserEntity> userOptional = userRepository.findUserByEmailIgnoreCase(email);
        if (userOptional.isEmpty()) throw new EntityNotFoundException("User not found!");
        UserEntity currentUser = userOptional.get();
        Set<Group> userGroups = currentUser.getUserGroups();

        boolean isAdmin = userGroups.stream().anyMatch(group -> "ROLE_ADMINISTRATOR".equals(group.getCode()));
        if (isAdmin) {
            throw new AdminDeletionException();
        }

        userRepository.delete(currentUser);

        return ResponseEntity.ok(Map.of("user", email, "status", "Deleted successfully!"));
    }

    public ResponseEntity<?> changeUserRole(RoleChangeRequest request) {

        String email = request.getUser();
        String role = "ROLE_" + request.getRole().toUpperCase();
        String operation = request.getOperation();
        Logger logger = LoggerFactory.getLogger(AccountService.class);
        logger.info("Received role change request: User={}, Role={}, Operation={}", email, role, operation);

        Group group = groupRepository.findByCode(role)
                .orElseThrow(() -> new EntityNotFoundException("Role not found!"));

        UserEntity userEntity = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));

        Set<Group> roles = userEntity.getUserGroups();
        boolean isAdmin = roles.stream().anyMatch(g -> "ROLE_ADMINISTRATOR".equals(g.getCode()));

        if ("GRANT".equalsIgnoreCase(operation)) {
            if (roles.contains(group)) {
                throw new DataIntegrityViolationException("Role already granted!");
            }

            if (isAdmin && role.equals("ROLE_ADMINISTRATOR")) {
                throw new IllegalArgumentException("Cannot add multiple ADMINISTRATOR roles!");
            }

            if (role.equals("ROLE_ADMINISTRATOR") && roles.stream().anyMatch(r -> r.getCode().equals("ROLE_ACCOUNTANT"))) {
                throw new IllegalArgumentException("The user cannot combine administrative and business roles!");
            }

            if (role.equals("ROLE_ACCOUNTANT") && roles.stream().anyMatch(r -> r.getCode().equals("ROLE_ADMINISTRATOR"))) {
                throw new IllegalArgumentException("The user cannot combine administrative and business roles!");
            }

            roles.add(group);
        } else if ("REMOVE".equalsIgnoreCase(operation)) {
            if (roles.stream().noneMatch(g -> group.getCode().equals(g.getCode()))) {
                throw new IllegalArgumentException("The user does not have the specified role!");
            }

            if ("ROLE_ADMINISTRATOR".equals(role) && roles.size() == 1) {
                throw new IllegalArgumentException("The user must have at least one role!");
            }

            if ("ROLE_ADMINISTRATOR".equals(role) && isAdmin) {
                throw new IllegalArgumentException("Can't remove ADMINISTRATOR role!");
            }
            roles.remove(group);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid operation!");
        }

        userEntity.setUserGroups(roles);
        userRepository.save(userEntity);

        return ResponseEntity.ok(new SignupResponse(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getLastname(),
                userEntity.getEmail(),
                userEntity.getUserGroups().stream()
                        .map(Group::getCode)
                        .sorted()
                        .collect(Collectors.toList())
        ));
    }

}