package user_management;

/**
* @class TrainCompany
* @brief Class representing a train company, a type of end user who can
* additionally create service requests and manage their account.
*/
public class TrainCompany extends EndUser {
    /**
     * @brief Constructor for TrainCompany class
     * @param username The username of the train company
     * @param email The email of the train company
     * @param password The password of the train company
     * @param userManagement The user management instance
     */
    public TrainCompany(
        String username, String email, String password, UserManagement userManagement
    ) {
        super(username, email, password, UserType.TrainCompany, userManagement);
    }

    /**
     * @brief Constructor for TrainCompany class with explicit id
     */
    public TrainCompany(
        String id, String username, String email, String password, UserManagement userManagement
    ) {
        super(id, username, email, password, UserType.TrainCompany, userManagement);
    }

    /**
     * @brief Creates a new service request
     */
    public void createServiceRequest() {
        // TODO: implement creating a service request
    }

    /**
     * @brief Creates a new service type
     */
    public void createServiceType() {
        // TODO: implement creating a service type
    }

    /**
     * @brief Creates a new train type
     */
    public void createTrainType() {
        // TODO: implement creating a train type
    }
}
