package account.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private String lastname;

    @Email
    @NonNull
    @Column(unique = true)
    private String email;

    @NonNull
    @Size(min = 12)
    private String password;

    @Column
    private boolean accountVerified;

    @Column
    private int failedLoginAttempts;

    @Column
    private boolean loginDisabled;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "user_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"
            ))
    private Set<Group> userGroups = new HashSet<>();

    public void addUserGroups(Group group){
        userGroups.add(group);
        group.getUsers().add(this);
    }

    public void removeUserGroups(Group group){
        userGroups.remove(group);
        group.getUsers().remove(this);
    }
}