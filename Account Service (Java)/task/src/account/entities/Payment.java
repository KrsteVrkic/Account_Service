package account.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.YearMonth;

@Data
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employee;
    private YearMonth period;
    private Long salary;

}
