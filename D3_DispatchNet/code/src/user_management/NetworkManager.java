/**
* @brief Class representing a network manager, a type of authenticated user who 
* can manage the network and review service requests.
*/
public class NetworkManager extends AuthenticatedUser {
    /**
     * @brief Constructor for NetworkManager class
     * @param username The username of the network manager
     * @param email The email of the network manager
     * @param password The password of the network manager
     */
    public NetworkManager(String username, String email, String password) {
        super(username, email, password, UserType.NetworkManager);
    }

    /**
     * @brief Views the transportation network
     */
    public void viewNetwork() {
        // TODO: implement network viewing
    }

    /**
     * @brief Creates a new station in the transportation network
     */
    public void createStation() {
        // TODO: implement station creation
    }

    /**
     * @brief Creates a new junction in the transportation network
     */
    public void createJunction() {
        // TODO: implement junction creation
    }

    /**
     * @brief Creates a new line in the transportation network
     */
    public void createLine() {
        // TODO: implement line creation
    }

    /**
     * @brief Edits an existing station in the transportation network
     * @param station The station to edit
     */
    public void editStation(String station) { // all the parameters are special classes to be integrated
        // TODO: implement station editing
    }

    /**
     * @brief Edits an existing junction in the transportation network
     * @param junction The junction to edit
     */
    public void editJunction(String junction) {
        // TODO: implement junction editing
    }

    /**
     * @brief Edits an existing line in the transportation network
     * @param line The line to edit
     */
    public void editLine(String line) {
        // TODO: implement line editing
    }

    /**
     * @brief Deletes an existing station in the transportation network
     * @param station The station to delete
     */
    public void deleteStation(String station) {
        // TODO: implement station deletion
    }

    /**
     * @brief Deletes an existing junction in the transportation network
     * @param junction The junction to delete
     */
    public void deleteJunction(String junction) {
        // TODO: implement junction deletion
    }

    /**
     * @brief Deletes an existing line in the transportation network
     * @param line The line to delete
     */
    public void deleteLine(String line) {
        // TODO: implement line deletion
    }

    /**
     * @brief Reviews a service request
     * @param serviceRequest The service request to review
     */
    public void reviewServiceRequest(String serviceRequest) {
        // TODO: implement service request review
    }
}
