package user_management;

/**
 * @class UserSession
 * @brief Tracks the current user session and whether the current user is authenticated.
 * This project deals with one active user at a time, so the session knows
 * whether the active user is authenticated or anonymous.
 */
public class UserSession {
    private AuthenticatedUser authenticatedUser;
    private AnonymousUser anonymousUser;

    /**
     * @brief Creates an empty session with no active user.
     */
    public UserSession() {
        this.authenticatedUser = null;
        this.anonymousUser = null;
    }

    /**
     * @brief Returns true if the current session belongs to an authenticated user.
     * @return true when an authenticated user is active, false otherwise
     */
    public boolean isAuthenticated() {
        return authenticatedUser != null;
    }

    /**
     * @brief Gets the authenticated user for the current session.
     * @return The active authenticated user, or null if the session is anonymous
     */
    public AuthenticatedUser getAuthenticatedUser() {
        return authenticatedUser;
    }

    /**
     * @brief Gets the anonymous user for the current session.
     * @return The active anonymous user, or null if the session is authenticated
     */
    public AnonymousUser getAnonymousUser() {
        return anonymousUser;
    }

    /**
     * @brief Starts an authenticated session for the given user.
     * @param authenticatedUser The authenticated user to set as current
     */
    public void setAuthenticatedUser(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        this.anonymousUser = null;
    }

    /**
     * @brief Starts an anonymous session for the given anonymous user.
     * @param anonymousUser The anonymous user to set as current
     */
    public void setAnonymousUser(AnonymousUser anonymousUser) {
        this.anonymousUser = anonymousUser;
        this.authenticatedUser = null;
    }

    /**
     * @brief Clears the current session state.
     */
    public void clearSession() {
        this.authenticatedUser = null;
        this.anonymousUser = null;
    }

    /**
     * @brief Gets a readable identifier for the current session user.
     * @return The username for authenticated sessions, an anonymous ID for 
     * anonymous sessions, or "no-user" when the session is empty
     */
    public String getCurrentUserIdentifier() {
        if (isAuthenticated()) {
            return authenticatedUser.getUsername();
        }
        if (anonymousUser != null) {
            return "anonymous-" + anonymousUser.getIdentifier();
        }
        return "no-user";
    }
}
