package account.repositories;

import account.entities.Group;
import org.springframework.data.repository.CrudRepository;

public interface UserGroupRepository extends CrudRepository<Group, Long> {
}
