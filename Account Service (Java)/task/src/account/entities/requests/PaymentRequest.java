package account.entities.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
public class PaymentRequest {

    @NonNull
    @Positive(message = "Salary must be positive")
    private Long salary;

    @Email
    @NotBlank(message = "email field is empty")
    @Pattern(regexp = ".*@acme\\.com$", message = "invalid email")
    private String employee;

    @NotBlank(message = "period field is empty")
    @JsonFormat(pattern = "MM-yyyy")
    @Pattern(regexp = "^(0[1-9]|1[0-2])-(19|20)\\d{2}$", message = "date must be of valid MM-yyyy format")
    private String period;

    @JsonCreator
    public PaymentRequest(@JsonProperty("salary") Long salary,
                          @JsonProperty("employee") String employee,
                          @JsonProperty("period") String period) {
        this.salary = salary;
        this.employee = employee;
        this.period = period;
    }

    public Date getDateFromString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
        try {
            return dateFormat.parse(this.period);
        } catch (ParseException e) {
            throw new ValidationException("invalid date format");
        }
    }
}
