package account.repositories;
import account.entities.Payment;
import account.entities.PaymentID;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, PaymentID> {
    Iterable<Payment> findAllByUserIdOrderByPeriodDesc(@NotNull Long userId);
}
