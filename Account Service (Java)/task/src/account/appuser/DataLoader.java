package account.appuser;

import account.entities.Group;
import account.repositories.GroupRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    @Resource
    private final GroupRepository groupRepository;

    @Autowired
    public DataLoader(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
        createRoles();
    }

    private void createRoles() {
        try {
            groupRepository.save(new Group("ROLE_ADMINISTRATOR", "Admin Group"));
            groupRepository.save(new Group("ROLE_USER", "User Group"));
            groupRepository.save(new Group("ROLE_ACCOUNTANT", "Hustler Group"));
        } catch (Exception Ignored) {}
    }
}