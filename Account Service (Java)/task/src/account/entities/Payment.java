package account.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Date;

@Entity
@Data
@IdClass(PaymentID.class)
public class Payment {
    @Id
    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST)
    @PrimaryKeyJoinColumn(name = "id")
    private AppUser user;
    @Id
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date period;
    @NotNull
    private Long salary;
}