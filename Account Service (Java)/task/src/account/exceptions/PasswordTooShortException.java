package account.exceptions;

public class PasswordTooShortException extends RuntimeException {
    public PasswordTooShortException() {
        super("The password is too short!");
    }
}