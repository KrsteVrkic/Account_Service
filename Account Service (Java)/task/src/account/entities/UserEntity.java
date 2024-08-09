package account.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "users")
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

    @Setter
    @Getter
    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "user_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"
            ))
    private Set<Group> userGroups = new HashSet<>();

    @Override
    public String toString() {
        return "UserEntity{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }

}