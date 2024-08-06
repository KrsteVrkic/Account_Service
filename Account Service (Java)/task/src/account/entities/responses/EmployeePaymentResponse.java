package account.entities.responses;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class EmployeePaymentResponse {

    private String name;
    private String lastname;
    private String period;
    private String salary;
}
