package user_management;

/**
* @class EndUser
* @brief Abstract class representing an end user, a type of authenticated user 
* who can view and manage their own data.
*/
public abstract class EndUser extends AuthenticatedUser {
    /**
     * @brief Empty constructor for EndUser class
     */
    public EndUser() {
    }

    /**
     * @brief Constructor for EndUser class
     * @param username The username of the end user
     * @param email The email of the end user
     * @param password The password of the end user
     * @param userType The type of the user
     */
    public EndUser(String username, String email, String password, UserType userType) {
        super(username, email, password, userType);
    }

    /**
     * @brief Prints the user's data, that is username and email
     */
    public void viewData() {
        System.out.println("Username: " + getUsername());
        System.out.println("Email: " + getEmail());
    }

    /**
     * @brief Changes the user's data
     */
    public void changeData() {
        // TODO: implement change data behavior
    }

    /**
     * @brief Deletes the user's account
     */
    public void deleteAccount() {
        // TODO: implement delete account behavior
    }
}
