    package account.repositories;

    import account.entities.Group;
    import org.springframework.data.repository.CrudRepository;
    import java.util.Optional;

    public interface GroupRepository extends CrudRepository<Group, Long> {
        Optional<Group> findByCode(String role);
    }