package account.response;

public class SignUpResponse {
    private String name;
    private String lastname;
    private String email;

    public SignUpResponse(String name, String lastname, String email) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
    }

    // Getters
    public String getName() { return name; }
    public String getLastname() { return lastname; }
    public String getEmail() { return email; }
}
