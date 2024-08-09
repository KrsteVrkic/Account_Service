package account.entities.requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RoleChangeRequest {
    @NotEmpty(message = "User must not be empty")
    private String user;
    @NotEmpty(message = "Role must not be empty")
    private String role;
    @NotEmpty(message = "Operation must not be empty")
    private String operation;
}
