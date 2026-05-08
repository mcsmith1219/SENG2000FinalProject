package auth;
import database.UserRepository;
import model.User;

/**
 * Class:
 * AuthService() - service for handling user authentication and registration.
 *
 * Methods:
 * AuthService(UserRepository) - constructor with user repository dependency.
 * login(String, String) - attempts to log in a user with username and password.
 * register(String, String, String) - attempts to register a new user.
 * findUser(String) - finds a user by username.
 **/

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password) {
        User user = userRepository.findUser(username);
        if (user != null && user.passwordMatches(password)) {
            return user;
        }
        return null;
    }

    public boolean register(String username, String password, String displayName) {
        return userRepository.createUser(username, password, displayName);
    }

    public User findUser(String username) {
        return userRepository.findUser(username);
    }
}
