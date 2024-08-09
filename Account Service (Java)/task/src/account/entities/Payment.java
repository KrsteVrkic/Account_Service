package account.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "payment")
@IdClass(PaymentID.class)
public class Payment {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Id
    @Temporal(TemporalType.DATE)
    @Column(name = "period", nullable = false)
    private Date period;

    @Column(name = "salary", nullable = false)
    private Long salary;
}
