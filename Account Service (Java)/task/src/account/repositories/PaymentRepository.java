package account.repositories;

import account.entities.Payment;
import account.entities.PaymentID;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends CrudRepository<Payment, PaymentID> {
    Iterable<Payment> findAllByUserIdOrderByPeriodDesc(@NotNull Long userId);
}
