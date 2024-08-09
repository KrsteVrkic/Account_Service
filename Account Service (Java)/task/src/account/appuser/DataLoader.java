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
            groupRepository.save(new Group("ADMINISTRATOR", "Admin Group"));
            groupRepository.save(new Group("ACCOUNTANT", "Accountant Group"));
            groupRepository.save(new Group("USER", "User Group"));
        } catch (Exception Ignored) {}
    }
}