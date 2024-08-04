package account.responses;

import java.util.Date;

public class EmailErrorResponse {

    private Date timestamp;
    private int status;
    private String error;
    private String path;
    private String message;

    public EmailErrorResponse(Date date, int status, String error, String path, String message) {
        this.message = message;
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.path = path;
    }
    public Date getTimestamp() {
        return timestamp;
    }
    public int getStatus() {
        return status;
    }
    public String getError() {
        return error;
    }
    public String getPath() {
        return path;
    }
    public String getMessage() {
        return message;
    }
}
