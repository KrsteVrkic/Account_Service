package account.entities.responses;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class PaymentsListResponse {
    private List<PaymentResponse> payments;

}
