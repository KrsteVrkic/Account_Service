package account.entities.responses;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class StatusResponse {
    @NonNull
    private String status;
}
