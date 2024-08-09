package account.security;

public enum Role {

    ADMINISTRATOR("ADMINISTRATOR"),
    ACCOUNTANT("ACCOUNTANT"),
    USER("USER");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}