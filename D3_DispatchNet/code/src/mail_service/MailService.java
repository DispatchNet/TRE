package mail_service;

/**
 * Interface for mail service operations.
 * Defines the contract for sending emails.
 */
public interface MailService {
    /**
     * @brief Sends an email.
     * 
     * @param email The email to send
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendEmail(Email email);

    /**
     * @brief Sends an email to a recipient with subject and body.
     * 
     * @param recipient The email recipient
     * @param subject The email subject
     * @param body The email body
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendEmail(String recipient, String subject, String body);
}
