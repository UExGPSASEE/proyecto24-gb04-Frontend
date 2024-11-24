package model;

public class LoginRequest {

    private String username;
    private String password;

    // Constructor
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters y setters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
