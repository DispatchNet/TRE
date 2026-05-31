package user_management;

import java.util.ArrayList;
import java.util.List;

import mail_service.Email;
import mail_service.MailService;
import mail_service.MailServiceFactory;
import IO_operations.IO;

/**
 * @class UserManagement
 * @brief Class managing the user accounts in the system. Other modules will
 * interact with this class to manage user sessions and data.
 */
public class UserManagement {
    // List of registered authenticated users in the system
    private final static List<AuthenticatedUser> registeredUsers = new ArrayList<>();
    // List of logged in authenticated users in the system
    private final static List<AuthenticatedUser> loggedUsers = new ArrayList<>();
    // List of current anonymous users in the system
    private final static List<AnonymousUser> anonymousUsers = new ArrayList<>();
    // Mail service instance for sending emails
    private final static MailService mailService = MailServiceFactory.getMailService();
    // IO instance for user input and output
    private final static IO io = new IO();

    /**
     * @brief Constructor for UserManagement class
     */
    public UserManagement() {
    }

    /**
     * @brief Gets the list of registered users
     * @return A list of registered authenticated users
     */
    public List<AuthenticatedUser> getRegisteredUsers() {
        return new ArrayList<>(registeredUsers);
    }

    /**
     * @brief Gets the list of logged-in users
     * @return A list of logged-in authenticated users
     */
    public List<AuthenticatedUser> getLoggedUsers() {
        return new ArrayList<>(loggedUsers);
    }

    /**
     * @brief Gets the list of anonymous users
     * @return A list of anonymous users
     */
    public List<AnonymousUser> getAnonymousUsers() {
        return new ArrayList<>(anonymousUsers);
    }

    /**
     * @brief Adds a registered authenticated user to the system
     * @param user The authenticated user to add
     */
    public void addRegisteredUser(AuthenticatedUser user) {
        registeredUsers.add(user);
    }

    /**
     * @brief Adds a logged-in authenticated user to the system
     * @param user The authenticated user to add
     */
    public void addLoggedUser(AuthenticatedUser user) {
        loggedUsers.add(user);
    }

    /**
     * @brief Adds an anonymous user to the system
     * @param user The anonymous user to add
     */
    public void addAnonymousUser(AnonymousUser user) {
        anonymousUsers.add(user);
    }

    /**
     * @brief Removes an authenticated user from the system
     * @param user The authenticated user to remove
     */
    public void removeRegisteredUser(AuthenticatedUser user) {
        registeredUsers.remove(user);
    }

    /**
     * @brief Removes a logged-in authenticated user from the system
     * @param user The authenticated user to remove
     */
    public void removeLoggedUser(AuthenticatedUser user) {
        loggedUsers.remove(user);
    }

    /**
     * @brief Removes an anonymous user from the system
     * @param user The anonymous user to remove
     */
    public void removeAnonymousUser(AnonymousUser user) {
        anonymousUsers.remove(user);
    }

    /**
     * @brief Sends an email to a user
     * @param Email The email to send
     * @return true if the email was sent successfully, false otherwise
     */
    public boolean sendEmail(Email email) {
        return mailService.sendEmail(email);
    }

    /**
     * @brief Displays an error message
     * @param message The error message to display
     */
    public void displayError(String message) {
        io.displayError(message);
    }

    /**
     * @brief Prompts the user for input
     * @param message The message to display
     * @return The user's input
     */
    public String prompt(String message) {
        return io.prompt(message);
    }

    /**
     * @brief Prompts the user for a password
     * @param message The message to display
     * @return The user's input
     */
    public String promptPassword(String message) {
        return io.promptPassword(message);
    }

    /**
     * @brief Checks if a username is unique
     * @param username The username to check
     * @return true if the username is unique (i.e., not already taken),
     * false otherwise
     */
    protected boolean checkUsernameUniqueness(String username) {
        for (AuthenticatedUser user : registeredUsers) {
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
        for (AuthenticatedUser user : registeredUsers) {
            if (user.getEmail().equals(email)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * @brief Checks if an email exists in the system
     * @param email The email to check
     * @return true if the email exists, false otherwise
     */
    protected boolean checkEmailExistence(String email) {
        for (AuthenticatedUser user : registeredUsers) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @brief Checks if a password meets security requirements
     * @param password The password to check
     * @return true if the password meets security requirements
     * (i.e. at least 8 characters long and contains a lower case letter, 
     * an uppercase letter and a number), false otherwise
     */
    protected boolean checkPasswordSecurity(String password) {
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
     * @return true if an authenticated user with the given credentials exists, 
     * false otherwise
     * @details If true the logged-in user will be added to the list of 
     * logged users
     */
    protected boolean authenticateUser(String username, String password) {
        for (AuthenticatedUser user : registeredUsers) {
            if (user.getUsername().equals(username) && 
                user.getPassword().equals(password)) {
                    addLoggedUser(user);
                    return true;
            }
        }

        return false;
    }

    /**
     * @brief Gets an authenticated user by their username
     * @param username The username of the user to retrieve
     * @return The authenticated user with the specified username, or null if not found
     */
    protected AuthenticatedUser getAuthenticatedUserByUsername(String username) {
        for (AuthenticatedUser user : registeredUsers) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }

        return null;
    }

    /**
     * @brief Gets an authenticated user by their email
     * @param email The email of the user to retrieve
     * @return The authenticated user with the specified email, or null if not found
     */
    protected AuthenticatedUser getAuthenticatedUserByEmail(String email) {
        for (AuthenticatedUser user : registeredUsers) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }

        return null;
    }

    /**
     * @brief Main method for testing UserManagement
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        UserManagement userManagement = new UserManagement();

        // add some anonymous users
        AnonymousUser anon1 = new AnonymousUser(anonymousUsers.size() + 1, userManagement);
        userManagement.addAnonymousUser(anon1);
        AnonymousUser anon2 = new AnonymousUser(anonymousUsers.size() + 1, userManagement);
        userManagement.addAnonymousUser(anon2);
        AnonymousUser anon3 = new AnonymousUser(anonymousUsers.size() + 1, userManagement);
        userManagement.addAnonymousUser(anon3);
        AnonymousUser anon4 = new AnonymousUser(anonymousUsers.size() + 1, userManagement);
        userManagement.addAnonymousUser(anon4);

        // print users before registration
        userManagement.printUsers();

        // register a new user from the first anonymous user
        anon1.register();

        // register a new user from the second anonymous user
        anon2.register();

        // login the first user
        anon3.login();

        // print users after registration and login
        userManagement.printUsers();
    }

    /**
     * @brief Prints all users in the system, used for testing purposes
     */
    private void printUsers() {
        System.out.println("Authenticated Users:");
        for (AuthenticatedUser user : registeredUsers) {
            System.out.println(" - " + user.getUsername());
        }
        System.out.println("Logged-in Users:");
        for (AuthenticatedUser user : loggedUsers) {
            System.out.println(" - " + user.getUsername());
        }
        System.out.println("Anonymous Users:");
        for (AnonymousUser user : anonymousUsers) {
            System.out.println(" - " + user.getIdentifier());
        }
    }
}
