package user_management;

/**
 * @enum UserType
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

    /**
     * @brief Main method for testing UserType values.
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        for (UserType type : UserType.values()) {
            System.out.println(type + " => " + type.toString());
        }
    }
}
