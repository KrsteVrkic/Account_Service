package account.exceptions;

public class PasswordBreachedException extends RuntimeException {
    public PasswordBreachedException() {
        super("The password is in the hacker's database!");
    }
}