package account.entities.responses;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupResponse {

    private long id;
    private String name;
    private String lastname;
    private String email;
    private List<String> roles;
}