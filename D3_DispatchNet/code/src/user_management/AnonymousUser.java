package user_management;

import mail_service.Email;

/**
 * @class AnonymousUser
 * @brief Class representing an anonymous user, who can only view the map and
 * search for stations and trains.
 */
public class AnonymousUser {
    private final int identifier;
    private final UserManagement userManagement;
    private static int idCounter = 0;

    /**
     * @brief Constructor for AnonymousUser class
     * @param identifier The identifier for the anonymous user
     * @param userManagement The user management instance
     */
    public AnonymousUser(UserManagement userManagement) {
        this.identifier = idCounter++;
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
    public void register() {
        String username, email, password1, password2;

        // ask username until username is unique
        boolean usernameUnique;
        do {
            username = userManagement.prompt("Enter username:");
            usernameUnique = userManagement.checkUsernameUniqueness(username);
            if (!usernameUnique) {
                userManagement.displayError(
                    "That username is already taken. " + 
                    "Please choose a different username."
                );
            }
        } while (!usernameUnique);

        // ask email until email is unique
        boolean emailUnique;
        do {
            email = userManagement.prompt("Enter email:");
            emailUnique = userManagement.checkEmailUniqueness(email);
            if (!emailUnique) {
                userManagement.displayError(
                    "That email is already registered. " + 
                    "Please use a different email address."
                );
            }
        } while (!emailUnique);

        // ask password until password is confirmed and meets security requirements
        while (true) {
            password1 = userManagement.promptPassword("Enter password:");
            password2 = userManagement.promptPassword("Confirm password:");

            if (!password1.equals(password2)) {
                userManagement.displayError(
                    "Passwords do not match. Please try again."
                );
                continue;
            }

            if (!userManagement.checkPasswordSecurity(password1)) {
                userManagement.displayError(
                    "Password does not meet security requirements." + 
                    " Please choose a stronger password."
                );
                continue;
            }

            break;
        }

        // create new user account and add it to the system
        Passenger newUser = new Passenger(username, email, password1, userManagement);
        userManagement.addRegisteredUser(newUser);

        // send email to the new user
        userManagement.sendEmail(
            new Email(
                email, 
                "Welcome to DispatchNet!", 
                "Your account has been successfully created."
            )
        );

        // log the new user in the newly created account
        userManagement.addLoggedUser(newUser);

        // remove the anonymous user from the system
        userManagement.removeAnonymousUser(this);
    }

    /**
     * @brief Reset the password of a user from the anonymous user state.
     */
    public void resetPassword() {
        String email;

        // ask email until email is associated with an existing user account
        boolean emailExists;
        do {
            email = userManagement.prompt("Enter email:");
            emailExists = userManagement.checkEmailExistence(email);
            if (!emailExists) {
                userManagement.displayError(
                    "No account exists for that email. " + 
                    "Please enter a valid registered email."
                );
            }
        } while (!emailExists);

        // create random OTP
        String otp = String.valueOf((int)(Math.random() * 1000000));

        // send password reset email to the user
        userManagement.sendEmail(
            new Email(
                email, 
                "Password Reset Request", 
                "A request has been received to reset your password. " +
                "If you did not make this request, please ignore this email." +
                "Your OTP for password reset is: " + otp
            )
        );

        // ask OTP until it matches the one sent in the email
        String enteredOtp;

        do {
            enteredOtp = userManagement.prompt("Enter OTP sent to your email:");
            if (!enteredOtp.equals(otp)) {
                userManagement.displayError(
                    "Invalid OTP. Please check your email and try again."
                );
            }
        } while (!enteredOtp.equals(otp));

        // ask new password until password is confirmed and meets security requirements
        String password1, password2;

        while (true) {
            password1 = userManagement.promptPassword("Enter new password:");
            password2 = userManagement.promptPassword("Confirm new password:");

            if (!password1.equals(password2)) {
                userManagement.displayError(
                    "Passwords do not match. Please try again."
                );
                continue;
            }

            if (!userManagement.checkPasswordSecurity(password1)) {
                userManagement.displayError(
                    "Password does not meet security requirements." + 
                    " Please choose a stronger password."
                );
                continue;
            }

            break;
        }

        // update the user's password in the system
        AuthenticatedUser user = userManagement.getAuthenticatedUserByEmail(email);
        // redundant null check since we already verified email existence,
        // but added for concurrency safety (currently the system is single-threaded)
        if (user != null) {
            user.setPassword(password1);
        }

        // send email to the user confirming password reset
        userManagement.sendEmail(
            new Email(
                email, 
                "Password Reset Successful", 
                "Your password has been successfully reset."
            )
        );

        // log the user in the account
        userManagement.addLoggedUser(user);

        // remove the anonymous user from the system
        userManagement.removeAnonymousUser(this);
    }

    /**
     * @brief Log the anonymous user into the authenticated state.
     */
    public void login() {
        String username, password;
        boolean authenticated;

        // ask username and password until they match an existing user account
        do {
            username = userManagement.prompt("Enter username:");
            password = userManagement.promptPassword("Enter password:");

            authenticated = userManagement.authenticateUser(username, password);
            if (!authenticated) {
                userManagement.displayError(
                    "Invalid username or password. Please try again."
                );
            }
        } while (!authenticated);

        // log the user in the account
        AuthenticatedUser user = userManagement.getAuthenticatedUserByUsername(username);
        userManagement.addLoggedUser(user);

        // remove the anonymous user from the system
        userManagement.removeAnonymousUser(this);
    }

    
}
