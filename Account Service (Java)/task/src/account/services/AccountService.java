package account.services;

import com.sun.jdi.InternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import account.entities.Group;
import account.entities.responses.SignupResponse;
import account.exceptions.AdminDeletionException;
import account.exceptions.PasswordBreachedException;
import account.exceptions.PasswordEqualsException;
import account.entities.requests.ChangePasswordRequest;
import account.entities.responses.ChangePasswordResponse;
import account.repositories.GroupRepository;
import account.security.BreachedPasswords;
import account.entities.UserEntity;
import account.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import account.entities.requests.RoleChangeRequest;
import static account.security.Role.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BreachedPasswords breachedPasswords;
    private final GroupRepository groupRepository;

    private final String adminRole = ADMINISTRATOR.getRole();
    private final String accountRole = ACCOUNTANT.getRole();
    private final String userRole = USER.getRole();
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    public AccountService(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          BreachedPasswords breachedPasswords, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.breachedPasswords = breachedPasswords;
        this.groupRepository = groupRepository;
    }

    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (breachedPasswords.isBreached(request.getNewPassword())) throw new PasswordBreachedException();

        UserEntity userEntity = userRepository.findUserByEmailIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));

        if (passwordEncoder.matches(request.getNewPassword(), userEntity.getPassword())) throw new PasswordEqualsException();

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
                        user.getUserGroups().stream()
                                .map(group -> "ROLE_" + group.getCode())
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> deleteUserByEmail(String email) {

        Optional<UserEntity> userOptional = userRepository.findUserByEmailIgnoreCase(email);
        if (userOptional.isEmpty()) throw new EntityNotFoundException("User not found!");

        UserEntity currentUser = userOptional.get();
        Set<Group> userGroups = currentUser.getUserGroups();

        boolean isAdmin = userGroups.stream().anyMatch(group -> adminRole.equals(group.getCode()));
        if (isAdmin) throw new AdminDeletionException();

        userRepository.delete(currentUser);

        return ResponseEntity.ok(Map.of("user", email, "status", "Deleted successfully!"));
    }

    public ResponseEntity<?> changeUserRole(RoleChangeRequest request) {

        String email = request.getUser();
        String roleCode = request.getRole().toUpperCase();
        String operation = request.getOperation().toUpperCase();

        Logger logger = LoggerFactory.getLogger(AccountService.class);
        logger.info("Received role change request: User={}, Role={}, Operation={}", email, roleCode, operation);

        Group group = groupRepository.findByCode(roleCode)
                .orElseThrow(() -> new EntityNotFoundException("Role not found!"));

        UserEntity userEntity = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));

        Set<Group> roles = userEntity.getUserGroups();
        boolean isAdmin = roles.stream().anyMatch(g -> adminRole.equals(g.getCode()));

        switch (operation) {
            case "GRANT":
                handleRoleGranting(roles, group, roleCode, isAdmin);
                break;
            case "REMOVE":
                handleRoleRemoval(roles, group, roleCode, isAdmin);
                break;
            default:
                throw new DataIntegrityViolationException("Invalid operation!");
        }

        userEntity.setUserGroups(roles);
        userRepository.save(userEntity);

        return ResponseEntity.ok(new SignupResponse(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getLastname(),
                userEntity.getEmail(),
                userEntity.getUserGroups().stream()
                        .map(r -> "ROLE_" + r.getCode())
                        .sorted()
                        .collect(Collectors.toList())
        ));
    }


    private void handleRoleGranting(Set<Group> roles, Group group, String roleCode, boolean isAdmin) {

        if (roles.contains(group)) throw new EntityNotFoundException("Role already granted!");

        if (adminRole.equals(roleCode)) {
            // Check if the user already has an ADMINISTRATOR role
            if (isAdmin) throw new IllegalArgumentException("Cannot add multiple ADMINISTRATOR roles!");
            // Ensure user doesn't have business roles
            if (roles.stream().anyMatch(r -> accountRole.equals(r.getCode()) || userRole.equals(r.getCode()))) {
                throw new IllegalArgumentException("The user cannot combine administrative and business roles!");
            }
        } else if (accountRole.equals(roleCode)) {
            // Check if the user is an ADMINISTRATOR
            if (isAdmin) throw new IllegalArgumentException("The user cannot combine administrative and business " +
                    "roles!");
        } else if (userRole.equals(roleCode)) {
            // Check if the user is an ADMINISTRATOR
            if (isAdmin) throw new IllegalArgumentException("The user cannot combine administrative and business " +
                    "roles!");
            // No restriction on assigning USER role to a user with ACCOUNTANT role
        } else {
            throw new InternalException("Internal Server Error");
        }

        roles.add(group);
    }

    private void handleRoleRemoval(Set<Group> roles, Group group, String roleCode, boolean isAdmin) {

        Logger logger = LoggerFactory.getLogger(AccountService.class);
        logger.info("hello from remove roles");

        if (!roles.contains(group)) throw new IllegalArgumentException("The user does not have a role!");

        if (adminRole.equals(roleCode)) {
            if (isAdmin) {
                throw new IllegalArgumentException("Can't remove ADMINISTRATOR role!");
            }
        }

        if (roles.size() == 1) throw new IllegalArgumentException("The user must have at least one role!");

        roles.remove(group);
    }
}
