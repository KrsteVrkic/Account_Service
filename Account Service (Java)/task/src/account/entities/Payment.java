package account.entities;

import jakarta.persistence.*;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public void setPeriodFromString(String input)  {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
        try {
            this.period = dateFormat.parse(input);
        } catch (ParseException e) {
            throw new ValidationException("invalid date format");
        }
    }
}