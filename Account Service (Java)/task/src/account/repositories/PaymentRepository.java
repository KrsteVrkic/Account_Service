package account.repositories;
import account.entities.Payment;
import account.entities.PaymentID;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PaymentRepository extends CrudRepository<Payment, PaymentID> {
    List<Payment> findAllByUserIdOrderByPeriodDesc(@NotNull Long userId);
}
