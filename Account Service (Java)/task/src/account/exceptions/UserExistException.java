package account.exceptions;

public class UserExistException extends RuntimeException {
    public UserExistException() {
        super("User exist!");
    }
}