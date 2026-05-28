/**
 * @brief Class representing an anonymous user, who can only view the map and
 * search for stations and trains.
 */
public class AnonymousUser {
    private final int identifier;

    /**
     * @brief Constructor for AnonymousUser class
     * @param identifier The identifier for the anonymous user
     */
    public AnonymousUser(int identifier) {
        this.identifier = identifier;
    }

    /**
     * @brief Gets the identifier for the anonymous user
     * @return The identifier for the anonymous user
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * @brief Register a new user account from the anonymous user state.
     */
    public void registerUser() {
        // TODO: implement registration behavior
    }

    /**
     * @brief Reset the password of a user from the anonymous user state.
     */
    public void resetPassword() {
        // TODO: implement password reset behavior
    }

    /**
     * @brief Log the anonymous user into the authenticated state.
     */
    public void login() {
        // TODO: implement login behavior
    }
}
