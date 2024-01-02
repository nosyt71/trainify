package nus.iss.trainify.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class User {

    @NotNull(message = "Username is required")
    @Size(min=3, max=30, message = "Name must be between 3 to 30 characters")
    private String username;

    @NotNull(message = "Password is required")
    @Size(min=3, max=30, message = "Password must be between 3 to 30 characters")
    private String password;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
