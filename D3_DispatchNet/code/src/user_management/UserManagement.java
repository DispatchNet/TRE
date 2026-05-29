package user_management;

import java.util.ArrayList;
import java.util.List;

/**
 * @brief Class managing the user accounts in the system. Other modules will
 * interact with this class to manage user sessions and data.
 */
public class UserManagement {
    private final List<AuthenticatedUser> authenticatedUsers = new ArrayList<>();
    private final List<AnonymousUser> anonymousUsers = new ArrayList<>();

    /**
     * @brief Constructor for UserManagement class
     */
    public UserManagement() {
    }

    /**
     * @brief Gets the list of authenticated users
     * @return A list of authenticated users
     */
    public List<AuthenticatedUser> getAuthenticatedUsers() {
        return new ArrayList<>(authenticatedUsers);
    }

    /**
     * @brief Gets the list of anonymous users
     * @return A list of anonymous users
     */
    public List<AnonymousUser> getAnonymousUsers() {
        return new ArrayList<>(anonymousUsers);
    }

    /**
     * @brief Adds an authenticated user to the system
     * @param user The authenticated user to add
     */
    public void addAuthenticatedUser(AuthenticatedUser user) {
        authenticatedUsers.add(user);
    }

    /**
     * @brief Adds an anonymous user to the system
     */
    public void addAnonymousUser() {
        anonymousUsers.add(new AnonymousUser(anonymousUsers.size() + 1));
    }

    /**
     * @brief Removes an authenticated user from the system
     * @param user The authenticated user to remove
     */
    public void removeAuthenticatedUser(AuthenticatedUser user) {
        authenticatedUsers.remove(user);
    }

    /**
     * @brief Removes an anonymous user from the system
     * @param user The anonymous user to remove
     */
    public void removeAnonymousUser(AnonymousUser user) {
        anonymousUsers.remove(user);
    }

    /**
     * @brief Checks if a username is unique
     * @param username The username to check
     * @return true if the username is unique (i.e., not already taken),
     * false otherwise
     */
    protected boolean checkUsernameUniqueness(String username) {
        for (AuthenticatedUser user : authenticatedUsers) {
            if (user.getUsername().equals(username)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @brief Checks if an email is unique
     * @param email The email to check
     * @return true if the email is unique (i.e., not already taken),
     * false otherwise
     */
    protected boolean checkEmailUniqueness(String email) {
        for (AuthenticatedUser user : authenticatedUsers) {
            if (user.getEmail().equals(email)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * @brief Checks if a password meets security requirements
     * @param password The password to check
     * @return true if the password meets security requirements
     * (i.e. at least 8 characters long and contains a lower case letter, 
     * an uppercase letter and a number), false otherwise
     */
    protectedboolean checkPasswordSecurity(String password) {
        // Check if password is at least 8 characters long
        if (password.length() < 8) {
            return false;
        }

        // Check if password contains a lower case letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }

        // Check if password contains an uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        // Check if password contains a number
        if (!password.matches(".*[0-9].*")) {
            return false;
        }

        return true;
    }

    /**
     * @brief Authenticates a user based on their username and password
     * @param username The username of the user to authenticate
     * @param password The password of the user to authenticate
     * @return true if the user is authenticated, false otherwise
     */
    protected boolean authenticateUser(String username, String password) {
        for (AuthenticatedUser user : authenticatedUsers) {
            if (user.getUsername().equals(username) && 
                user.getPassword().equals(password)) {
                return true;
            }
        }

        //-----------------------------
        // maybe to remove the user from the anonymous users list and add it to the authenticated users list
        //---------------------------

        return false;
    }

    /**
     * @brief Main method for testing UserManagement
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
    }
}
