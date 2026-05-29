package user_management;

/**
 * @brief Class representing an anonymous user, who can only view the map and
 * search for stations and trains.
 */
public class AnonymousUser {
    private final int identifier;
    private final UserManagement userManagement;

    /**
     * @brief Constructor for AnonymousUser class
     * @param identifier The identifier for the anonymous user
     * @param userManagement The user management instance
     */
    public AnonymousUser(int identifier, UserManagement userManagement) {
        this.identifier = identifier;
        this.userManagement = userManagement;
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
        String username, email, password1, password2;

        // ask username until username is unique
        do {
            println("Enter username:");
            username = readLine();
        } while (!userManagement.checkUsernameUniqueness(username));

        // ask email until email is unique
        do {
            println("Enter email:");
            email = readLine();
        } while (!userManagement.checkEmailUniqueness(email));

        // ask password until password is confirmed and meets security requirements
        do {
            println("Enter password:");
            password1 = readLine();
            println("Confirm password:");
            password2 = readLine();
        } while (
            !password1.equals(password2) && 
            !userManagement.checkPasswordSecurity(password1)
        );

        // create new user account and add it to the system
        Passenger newUser = new Passenger(username, email, password1);
        userManagement.addAuthenticatedUser(newUser);
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
        String username, password;

        // ask username and password until they match an existing user account
        do {
            println("Enter username:");
            username = readLine();
            
            println("Enter password:");
            password = readLine();
        } while (!userManagement.authenticateUser(username, password));
    }

    
}
