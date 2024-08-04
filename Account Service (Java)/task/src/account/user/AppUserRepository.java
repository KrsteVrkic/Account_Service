package account.user;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AppUserRepository extends CrudRepository<AppUser, Integer> {
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findAppUserByName(String name);
    Optional<AppUser> findByEmailIgnoreCase(String email);
}
