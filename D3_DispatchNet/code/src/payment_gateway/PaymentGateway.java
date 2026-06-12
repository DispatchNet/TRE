package payment_gateway;

/**
 * @interface PaymentGateway
 * @brief Interface representing a payment gateway used by the system.
 */
public interface PaymentGateway {
    /**
     * @brief Processes a transaction request.
     *
     * @param transactionDescription Description of the transaction.
     * @return true when transaction succeeds, false when it fails.
     */
    boolean processTransaction(String transactionDescription);
}
