package model;
/**
 * Class:
 * User() - model representing a user account with authentication.
 *
 * Methods:
 * User(int, String, String, String) - constructor.
 * getId() - returns the user ID.
 * getUsername() - returns the username.
 * getPassword() - returns the password hash.
 * passwordMatches(String) - checks if input password matches stored password.
 * getDisplayName() - returns the display name.
 **/

public class User {
    private final int id;
    private final String username;
    private final String password;
    private final String displayName;

    public User(int id, String username, String password, String displayName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean passwordMatches(String input) {
        return password.equals(input);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPassword() {
        return password;
    }
}
