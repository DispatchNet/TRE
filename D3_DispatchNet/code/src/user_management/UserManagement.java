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
     * @brief Main method for testing UserManagement
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
    }
}
