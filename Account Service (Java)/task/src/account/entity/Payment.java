package account.entity;

import jakarta.persistence.*;

import java.time.YearMonth;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employee;
    private YearMonth period;
    private Long salary;

    // Getters and setters
}
