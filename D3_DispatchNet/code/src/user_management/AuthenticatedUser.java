package user_management;

import java.util.Objects;
import java.util.UUID;

/**
 * @class AuthenticatedUser
 * @brief Abstract class representing an authenticated user in the system,
 * containing their credentials and user type.
 */
public abstract class AuthenticatedUser {
    private final String id;
    private String username;
    private String email;
    private String password;
    private UserType userType;
    protected final UserManagement userManagement; // must be visible to subclasses

    /**
    * @brief Empty Constructor for AuthenticatedUser class
    */
    public AuthenticatedUser(UserManagement userManagement) {
        this.id = UUID.randomUUID().toString();
        this.userManagement = userManagement;
    }

    /**
     * @brief Constructor for AuthenticatedUser class
     * @param username The username of the authenticated user
     * @param email The email of the authenticated user
     * @param password The password of the authenticated user
     * @param userType The type of the authenticated user
     */
    public AuthenticatedUser(
        String username, String email, String password, UserType userType,
        UserManagement userManagement
    ) {
        this(UUID.randomUUID().toString(), username, email, password, userType, userManagement);
    }

    /**
     * @brief Constructor for AuthenticatedUser class with explicit id
     * @param id The unique identifier of the authenticated user
     * @param username The username of the authenticated user
     * @param email The email of the authenticated user
     * @param password The password of the authenticated user
     * @param userType The type of the authenticated user
     */
    public AuthenticatedUser(
        String id, String username, String email, String password, UserType userType,
        UserManagement userManagement
    ) {
        this.id = id == null ? UUID.randomUUID().toString() : id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.userManagement = userManagement;
    }

    /**
     * @brief Gets the unique id of the authenticated user
     * @return The unique identifier
     */
    public String getId() {
        return id;
    }

    /**
     * @brief Gets the username of the authenticated user
     * @return The username of the authenticated user
     */
    public String getUsername() {
        return username;
    }

    /**
     * @brief Gets the email of the authenticated user
     * @return The email of the authenticated user
     */
    public String getEmail() {
        return email;
    }

    /**
     * @brief Gets the password of the authenticated user
     * @return The password of the authenticated user
     */
    public String getPassword() {
        return password;
    }

    /**
     * @brief Gets the type of the authenticated user
     * @return The type of the authenticated user
     */
    public UserType getUserType() {
        return userType;
    }

    /**
     * @brief Sets the username of the authenticated user
     * @param username The username of the authenticated user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @brief Sets the email of the authenticated user
     * @param email The email of the authenticated user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @brief Sets the password of the authenticated user
     * @param password The password of the authenticated user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @brief Sets the type of the authenticated user
     * @param userType The type of the authenticated user
     */
    /* 
    public void setUserType(UserType type) {
        this.userType = type;
    }*/

    /**
    * @brief Log out the authenticated user, moving to the anonymous user state.
    */
    public void logout() {
        userManagement.logoutUser();
    }

    /**
     * @brief Checks if this authenticated user is equal to another object
     * @param o The object to compare with
     * @return True if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;

        if(!(o instanceof AuthenticatedUser))
            return false;

        AuthenticatedUser that = (AuthenticatedUser) o;
        
        return Objects.equals(id, that.id)
                && Objects.equals(username, that.username)
                && Objects.equals(email, that.email)
                && userType == that.userType;
    }

    /**
     * @brief Generates a hash code for the authenticated user
     * @return The hash code of the authenticated user
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, userType);
    }
}
