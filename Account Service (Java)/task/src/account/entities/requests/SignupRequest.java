package account.entities.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignupRequest {
    @NotEmpty(message = "Name cannot be empty")
    private String name;
    @NotEmpty(message = "Lastname cannot be empty")
    private String lastname;
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    @Pattern(regexp = ".*@acme\\.com$")
    private String email;
    @NotEmpty(message = "Password cannot be empty")
    private String password;
}