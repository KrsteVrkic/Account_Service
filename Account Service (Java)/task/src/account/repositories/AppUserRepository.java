package account.repositories;

import account.entities.AppUser;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface AppUserRepository extends CrudRepository<AppUser, Long> {
    Optional<AppUser> findUserByEmailIgnoreCase(String email);
}