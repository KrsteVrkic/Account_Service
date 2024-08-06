package account.entities.responses;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ErrorResponse {
    private LocalDate timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDate.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}