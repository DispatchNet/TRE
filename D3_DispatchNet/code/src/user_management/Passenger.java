package user_management;

import java.util.ArrayList;
import java.util.List;

//------------------------
//to substitute Ticket with a real class
// group pass can be omitted
//---------------------------


/**
 * @brief Class representing a passenger, a type of end user who can 
 * additionally purchase and manage tickets.
 */
public class Passenger extends EndUser {
    private final List<String> tickets = new ArrayList<>();

    /**
     * @brief Constructor for Passenger class
     * @param username The username of the passenger
     * @param email The email of the passenger
     * @param password The password of the passenger
     */
    public Passenger(String username, String email, String password) {
        super(username, email, password, UserType.Passenger);
    }

    /**
     * @brief Gets the list of tickets purchased by the passenger
     * @return A list of Ticket 
     */
    public List<String> getTickets() {
        return new ArrayList<>(tickets);
    }

    /**
     * @brief Purchases a new ticket
     * @param ticket The ticket to purchase
     * @return true if the ticket was purchased successfully, false otherwise
     */
    public boolean purchaseTicket(String ticket) {
        // TODO: implement ticket purchasing
        return tickets.add(ticket);
    }

    /**
     * @brief Purchases a new group pass
     * @param groupPass The group pass to purchase
     * @return true if the group pass was purchased successfully, false otherwise
     */
    public boolean purchaseTicketGroup(String groupPass) {
        // TODO: implement group pass purchasing
        return tickets.add(groupPass);
    }

    /**
     * @brief Cancels a purchased ticket
     * @param ticket The ticket to cancel
     * @return true if the ticket was canceled successfully, false otherwise
     */
    public boolean cancelTicket(String ticket) {
        return tickets.remove(ticket);
    }

    /**
     * @brief Cancels a purchased group pass
     * @param groupPass The group pass to cancel
     * @return true if the group pass was canceled successfully, false otherwise
     */
    public boolean cancelTicketGroup(String groupPass) {
        // TODO: implement group pass cancellation
        return tickets.remove(groupPass);
    }

    /**
     * @brief Retrieves the history of purchased tickets
     */
    public void getTicketsHistory() {
        // TODO: implement ticket history retrieval
    }

    /**
     * @brief Finds a path between two stations
     */
    public void findPath() {
        // TODO: implement path finding
    }

    /**
     * @brief Searches for stations or trains
     */
    public void searchStationTrain() {
        // TODO: implement station/train search
    }

    /**
     * @brief Views the map
     */
    public void viewMap() {
        // TODO: implement map viewing
    }
}
