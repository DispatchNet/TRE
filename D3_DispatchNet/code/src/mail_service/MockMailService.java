package mail_service;

/**
 * Mock implementation of MailService.
 * Simulates sending emails without actually sending them.
 * Used for testing and development purposes.
 */
public class MockMailService implements MailService {

    @Override
    public boolean sendEmail(Email email) {
        if (email == null || email.getRecipient() == null || email.getRecipient().isEmpty()) {
            System.err.println("[MockMailService] Failed to send email: Invalid recipient");
            return false;
        }

        System.out.println("[MockMailService] Email sent successfully:");
        System.out.println("  To: " + email.getRecipient());
        System.out.println("  Subject: " + email.getSubject());
        System.out.println("  Body: " + email.getBody());
        System.out.println();

        return true;
    }

    @Override
    public boolean sendEmail(String recipient, String subject, String body) {
        Email email = new Email(recipient, subject, body);
        return sendEmail(email);
    }
}
