package account.entities.requests;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @JsonProperty("new_password")
    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    private String newPassword;
}
