package account.entities.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class PaymentUpdateRequest {
    @Email
    @NotBlank(message = "email field is empty")
    @Pattern(regexp = ".*@acme\\.com$", message = "invalid email")
    private String employee;
    @NotBlank(message = "period field is empty")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-yyyy")
    @Pattern(regexp = "^(0[1-9]|1[0-2])-(19|20)\\d{2}$", message = "date must be of valid MM-yyyy format")
    private String period;
    @NonNull
    @Positive(message = "Salary must be positive")
    private Long salary;
}
