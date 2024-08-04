package account.exception;

public class UserExistException extends RuntimeException {
    public UserExistException() {
        super("User exist!");
    }
}
