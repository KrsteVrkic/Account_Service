package account.repositories;

import account.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findUserByEmailIgnoreCase(String email);
    UserEntity findByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);
}