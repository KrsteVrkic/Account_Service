package account.exceptions;

public class PasswordSameAsCurrentException extends RuntimeException {
    public PasswordSameAsCurrentException() {
        super("The passwords must be different!");
    }
}