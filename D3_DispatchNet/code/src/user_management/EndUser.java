package user_management;

import mail_service.Email;

/**
* @class EndUser
* @brief Abstract class representing an end user, a type of authenticated user 
* who can view and manage their own data.
*/
public abstract class EndUser extends AuthenticatedUser {
    /**
     * @brief Empty constructor for EndUser class
     */
    public EndUser(UserManagement userManagement) {
        super(userManagement);
    }

    /**
     * @brief Constructor for EndUser class
     * @param username The username of the end user
     * @param email The email of the end user
     * @param password The password of the end user
     * @param userType The type of the user
     */
    public EndUser(
        String username, String email, String password, UserType userType, 
        UserManagement userManagement
    ) {
        super(username, email, password, userType, userManagement);
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
        // ask user which data they want to change, amongst username, email and password
        String choice;
        boolean validChoice;
        do {
            choice = userManagement.prompt(
                "What data do you want to change? (username/email/password)"
            );

            validChoice = choice.equals("username") || 
                choice.equals("email") || choice.equals("password");
            
            if (!validChoice) {
                userManagement.displayError(
                    "Invalid choice. Please enter 'username', 'email', or 'password'."
                );
            }
        } while (!validChoice);

        switch (choice) {
            case "username":
                String username;

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

                // set the new username
                this.setUsername(username);

                break;

            case "email":
                String email;

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

                // set the new email
                this.setEmail(email);

                break;

            case "password":
                String password1, password2;

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

                // set the new password
                this.setPassword(password1);

                break;
        
            default:
                userManagement.displayError("Invalid choice.");
                break;
        }

        // send email to the user
        userManagement.sendEmail(
            new Email(
                this.getEmail(),
                "Your account information has been updated",
                "Hello " + this.getUsername() + ",\n\n" +
                "Your account information has been successfully updated."
            )
        );
    }

    /**
     * @brief Deletes the user's account
     */
    public void deleteAccount() {
        userManagement.logoutUser();
    }
}
