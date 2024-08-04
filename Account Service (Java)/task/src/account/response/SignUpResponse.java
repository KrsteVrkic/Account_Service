package account.response;

public class SignUpResponse {
    private String name;
    private String lastname;
    private String email;
    private long id;

    public SignUpResponse(String name, String lastname, String email, long id) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public long getId() {
        return id;
    }
}
