package user_management;

import java.util.ArrayList;
import java.util.List;

import csv_database.CsvDatabase;
import mail_service.Email;
import mail_service.MailService;
import mail_service.MockMailService;
import payment_gateway.PaymentGateway;
import payment_gateway.MockPaymentGateway;
import IO_operations.IO;

/**
 * @class UserManagement
 * @brief Class managing the user accounts in the system. Other modules will
 * interact with this class to manage user sessions and data.
 */
public class UserManagement {
    // List of authenticated authenticated users in the system
    private final static List<AuthenticatedUser> authenticatedUsers = new ArrayList<>();
    // Mail service instance for sending emails
    private final static MailService mailService = new MockMailService();
    // Payment gateway instance for processing payments (used by passengers)
    private final static PaymentGateway paymentGateway = new MockPaymentGateway();
    // IO instance for user input and output
    private final IO io;
    // User session to track the current active user (only one at a time)
    private final static UserSession session = new UserSession();
    // CSV database instance for loading and saving user data and tickets
    private final CsvDatabase csvDatabase;

    /**
     * @brief Constructor for UserManagement class
     */
    public UserManagement(IO io, CsvDatabase csvDatabase) {
        this.io = io;
        this.csvDatabase = csvDatabase;
        session.setAnonymousUser(new AnonymousUser(this));
        authenticatedUsers.addAll(csvDatabase.loadAuthenticatedUsers(this));
        try {
            csvDatabase.loadTickets(this);
        } catch (Exception e) {
            System.err.println("Failed to load tickets: " + e.getMessage());
        }

        // create default admin account if it doesn't exist
        if (getAuthenticatedUserByUsername("Admin") == null) {
            NetworkManager admin = new NetworkManager(
                "Admin", "admin@dispatchnet.it", "VerySecurePassword1234", this
            );

            addAuthenticatedUser(admin);
        }
    }

    /**
     * @brief Gets the list of authenticated users
     * @return A list of authenticated authenticated users
     */
    public List<AuthenticatedUser> getAuthenticatedUsers() {
        return new ArrayList<>(authenticatedUsers);
    }

    /**
     * @brief Adds a authenticated authenticated user to the system
     * @param user The authenticated user to add
     */
    public void addAuthenticatedUser(AuthenticatedUser user) {
        authenticatedUsers.add(user);
        csvDatabase.saveAuthenticatedUsers(authenticatedUsers);
    }

    /**
     * @brief Removes an authenticated user from the system
     * @param user The authenticated user to remove
     */
    public void removeAuthenticatedUser(AuthenticatedUser user) {
        authenticatedUsers.remove(user);
        csvDatabase.saveAuthenticatedUsers(authenticatedUsers);
    }

    /**
     * @brief Sends an email to a user
     * @param Email The email to send
     * @return true if the email was sent successfully, false otherwise
     */
    public boolean sendEmail(Email email) {
        return mailService.sendEmail(email);
    }

    public boolean processTransaction(String transactionDescription) {
        return paymentGateway.processTransaction(transactionDescription);
    }

    /**
     * @biref print a generic message
     * @param message message to be printed
     */
    public void print(String message) {
      io.print(message);
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
     * @brief Gets the current user session
     * @return The current user session
     */
    public UserSession getSession() {
        return session;
    }

    /**
     * @brief Checks if the current user is authenticated
     * @return true if the current user is authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        return session.isAuthenticated();
    }

    /**
     * @brief Gets the authenticated user for the current session
     * @return The authenticated user for the current session, or null if the session is anonymous
     */
    public AuthenticatedUser getAuthenticatedUser() {
        return session.getAuthenticatedUser();
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
     * @brief Checks if an email exists in the system
     * @param email The email to check
     * @return true if the email exists, false otherwise
     */
    protected boolean checkEmailExistence(String email) {
        for (AuthenticatedUser user : authenticatedUsers) {
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
     */
    protected boolean authenticateUser(String username, String password) {
        for (AuthenticatedUser user : authenticatedUsers) {
            if (user.getUsername().equals(username) && 
                user.getPassword().equals(password)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @brief Transitions the current session from anonymous to authenticated.
     * Sets the given authenticated user as the current session user.
     * @param user The authenticated user to log in
     */
    public void loginUser(AuthenticatedUser user) {
        session.setAuthenticatedUser(user);
    }

    /**
     * @brief Transitions the current session from authenticated to anonymous.
     * Clears the authenticated user and creates a new anonymous session.
     */
    public void logoutUser() {
        session.setAnonymousUser(new AnonymousUser(this));
    }

    /**
     * @brief Gets an authenticated user by their username
     * @param username The username of the user to retrieve
     * @return The authenticated user with the specified username, or null if not found
     */
    protected AuthenticatedUser getAuthenticatedUserByUsername(String username) {
        for (AuthenticatedUser user : authenticatedUsers) {
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
        for (AuthenticatedUser user : authenticatedUsers) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }

        return null;
    }

    /**
     * @brief Gets an authenticated user by their id
     * @param id The id of the user to retrieve
     * @return The authenticated user with the specified id, or null if not found
     */
    public AuthenticatedUser getAuthenticatedUserById(String id) {
        for (AuthenticatedUser user : authenticatedUsers) {
            if (user.getId().equals(id)) {
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
        UserManagement userManagement = new UserManagement(new IO(), new CsvDatabase());

        // set initial anonymous user
        AnonymousUser currentAnon = new AnonymousUser(userManagement);
        session.setAnonymousUser(currentAnon);

        // print initial state (anonymous user)
        userManagement.printUsers();

        // register a new user from the anonymous user state
        currentAnon.register();

        // print state after registration (authenticated user)
        userManagement.printUsers();

        // logout the authenticated user
        userManagement.getSession().getAuthenticatedUser().logout();

        // print state after logout (anonymous user)
        userManagement.printUsers();
    }

    /**
     * @brief Prints the current session state and authenticated users
     */
    private void printUsers() {
        System.out.println("Authenticated Users:");
        for (AuthenticatedUser user : authenticatedUsers) {
            System.out.println(" - " + user.getUsername());
        }
        System.out.println("Current Session:");
        if (session.isAuthenticated()) {
            System.out.println(" - Authenticated: " + session.getAuthenticatedUser().getUsername());
        } else {
            System.out.println(" - Anonymous: " + session.getAnonymousUser().getIdentifier());
        }
        System.out.println();
    }
}
