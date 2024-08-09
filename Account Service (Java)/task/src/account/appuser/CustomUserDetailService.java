package account.appuser;

import account.entities.UserEntity;
import account.entities.Group;
import account.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Service("userDetailsService")
@Transactional
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailService.class);

    @Autowired
    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        email = email.toLowerCase();
        logger.info("Attempting to load user by email: {}", email);

        final UserEntity customer = userRepository.findByEmail(email);
        if (customer == null) {
            logger.error("User not found with email: {}", email);
            throw new UsernameNotFoundException(email);
        }

        logger.info("User found: {}", email);

        return User.withUsername(customer.getEmail().toLowerCase())
                .password(customer.getPassword())
                .disabled(customer.isLoginDisabled())
                .authorities(getAuthorities(customer)).build();
    }

    private Collection<GrantedAuthority> getAuthorities(UserEntity user) {
        Set<Group> userGroups = user.getUserGroups();
        Collection<GrantedAuthority> authorities = new ArrayList<>(userGroups.size());
        for (Group userGroup : userGroups) {
            authorities.add(new SimpleGrantedAuthority(userGroup.getCode()));
        }
        return authorities;
    }
}

