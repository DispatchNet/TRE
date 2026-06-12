package payment_gateway;

/**
 * @class MockPaymentGateway
 * @brief Mock implementation of PaymentGateway used for testing.
 * Simulates a random transaction success or failure.
 */
public class MockPaymentGateway implements PaymentGateway {
    @Override
    public boolean processTransaction(String transactionDescription) {
        if (transactionDescription == null || transactionDescription.isBlank()) {
            System.err.println("[MockPaymentGateway] Invalid transaction description.");
            return false;
        }

        boolean success = Math.random() >= 0.5;
        System.out.println("[MockPaymentGateway] Transaction requested: " + transactionDescription);
        System.out.println("[MockPaymentGateway] Transaction " + (success ? "succeeded" : "failed") + ".");
        return success;
    }
}
