package mail_service;

/**
 * @class Email
 * @brief Class representing an email message.
 */
public class Email {
    private String recipient;
    private String subject;
    private String body;

    /**
     * @brief Constructor for Email class
     * @param recipient
     * @param subject
     * @param body
     */
    public Email(String recipient, String subject, String body) {
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
    }

    /**
     * @brief Gets the recipient of the email
     * @return The recipient email address
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * @brief Gets the subject of the email
     * @return The email subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @brief Gets the body of the email
     * @return The email body
     */
    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Email{" +
                "recipient='" + recipient + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
