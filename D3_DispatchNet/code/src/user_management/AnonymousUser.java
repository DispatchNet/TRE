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
    public void register() { // TODO: add errors display
        String username, email, password1, password2;

        // ask username until username is unique
        do {
            System.out.println("Enter username:");
            username = System.console().readLine();
        } while (!userManagement.checkUsernameUniqueness(username));

        // ask email until email is unique
        do {
            System.out.println("Enter email:");
            email = System.console().readLine();
        } while (!userManagement.checkEmailUniqueness(email));

        // ask password until password is confirmed and meets security requirements
        do {
            System.out.println("Enter password:");
            password1 = System.console().readLine();
            System.out.println("Confirm password:");
            password2 = System.console().readLine();
        } while (
            !password1.equals(password2) && 
            !userManagement.checkPasswordSecurity(password1)
        );

        // create new user account and add it to the system
        Passenger newUser = new Passenger(username, email, password1);
        userManagement.addAuthenticatedUser(newUser);

        // send email to the new user
        userManagement.sendEmail(
            new Email(
                email, 
                "Welcome to DispatchNet!", 
                "Your account has been successfully created."
            )
        );

        // remove the anonymous user from the system
        userManagement.removeAnonymousUser(this);
    }

    /**
     * @brief Reset the password of a user from the anonymous user state.
     */
    public void resetPassword() { // TODO: add errors display
        String email;

        // ask email until email is associated with an existing user account
        do {
            System.out.println("Enter email:");
            email = System.console().readLine();
        } while (!userManagement.checkEmailExistence(email));

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
            System.out.println("Enter OTP sent to your email:");
            enteredOtp = System.console().readLine();
        } while (!enteredOtp.equals(otp));

        // ask new password until password is confirmed and meets security requirements
        String password1, password2;

        do {
            System.out.println("Enter new password:");
            password1 = System.console().readLine();
            System.out.println("Confirm new password:");
            password2 = System.console().readLine();
        } while (
            !password1.equals(password2) && 
            !userManagement.checkPasswordSecurity(password1)
        );

        // update the user's password in the system
        AuthenticatedUser user = userManagement.getAuthenticatedUserByEmail(email);
        // redundant null check since we already verified email existence,
        // but added for concurrency safety
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

        // remove the anonymous user from the system
        userManagement.removeAnonymousUser(this);
    }

    /**
     * @brief Log the anonymous user into the authenticated state.
     */
    public void login() { // TODO: add errors display
        String username, password;

        // ask username and password until they match an existing user account
        do {
            System.out.println("Enter username:");
            username = System.console().readLine();
            
            System.out.println("Enter password:");
            password = System.console().readLine();
        } while (!userManagement.authenticateUser(username, password));

        // remove the anonymous user from the system
        userManagement.removeAnonymousUser(this);
    }

    
}
