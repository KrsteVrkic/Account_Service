package account.repositories;

import account.entities.UserGroup;
import org.springframework.data.repository.CrudRepository;

public interface UserGroupRepository extends CrudRepository<UserGroup, String> {
}
