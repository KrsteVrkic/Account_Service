package account.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @JsonProperty("new_password")
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    private String newPassword;
}
