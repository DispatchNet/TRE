/**
 * @brief Enum representing the different types of users in the system.
 */
public enum UserType {
    Passenger,
    TrainCompany,
    NetworkManager;

    @Override
    public String toString() {
        switch (this) {
            case Passenger:
                return "Passenger";
            case TrainCompany:
                return "TrainCompany";
            case NetworkManager:
                return "NetworkManager";
            default:
                return "Unknown";
        }
    }
}
