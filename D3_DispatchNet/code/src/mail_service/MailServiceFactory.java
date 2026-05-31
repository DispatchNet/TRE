package mail_service;

/**
 * @class MailServiceFactory
 * @brief Factory class for creating mail service instances.
 */
public class MailServiceFactory {
    private static MailService instance;

    /**
     * @brief Gets the default mail service instance.
     * Currently returns a MockMailService.
     * 
     * @return The mail service instance
     */
    public static MailService getMailService() {
        if (instance == null) {
            instance = new MockMailService();
        }
        return instance;
    }

    /**
     * @brief Sets a custom mail service instance.
     * Useful for testing or swapping implementations.
     * 
     * @param mailService The mail service to use
     */
    public static void setMailService(MailService mailService) {
        instance = mailService;
    }

    /**
     * @brief Resets the mail service instance to the default.
     */
    public static void reset() {
        instance = null;
    }
}
