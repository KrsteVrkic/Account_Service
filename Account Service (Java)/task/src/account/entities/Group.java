package account.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "principle_groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String code;
    private String name;
    @ManyToMany(mappedBy = "userGroups", fetch = FetchType.EAGER)
    private Set<UserEntity> users = new HashSet<>();
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
    @Override
    public String toString() {
        return "Group{id=" + id + ", code='" + code + "', name='" + name + "'}";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(code, group.code);
    }

    public Group(String role, String name) {
        this.code = role;
        this.name = name;
    }
}