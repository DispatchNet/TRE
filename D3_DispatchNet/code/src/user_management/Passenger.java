package user_management;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mail_service.Email;
import payment_gateway.MockPaymentGateway;
import payment_gateway.PaymentGateway;
import ticketing.Ticket;

/**
 * @class Passenger
 * @brief Class representing a passenger, a type of end user who can 
 * additionally purchase and manage tickets.
 */
public class Passenger extends EndUser {
    // Active and cancelled tickets are both tracked in the same list,
    // so ticket lifecycle history can be obtained from each Ticket object.
    private final List<Ticket> tickets = new ArrayList<>();
    private final PaymentGateway paymentGateway = new MockPaymentGateway();

    /**
     * @brief Constructor for Passenger class
     * @param username The username of the passenger
     * @param email The email of the passenger
     * @param password The password of the passenger
     * @param userManagement The user management instance
     */
    public Passenger(
        String username, String email, String password, UserManagement userManagement
    ) {
        super(username, email, password, UserType.Passenger, userManagement);
    }

    /**
     * @brief Constructor for Passenger class with explicit id
     */
    public Passenger(
        String id, String username, String email, String password, UserManagement userManagement
    ) {
        super(id, username, email, password, UserType.Passenger, userManagement);
    }

    /**
     * @brief Gets the list of active tickets purchased by the passenger.
     * @return A list of active Ticket objects.
     */
    public List<Ticket> getTickets() {
        return tickets.stream()
            .filter(Ticket::isActive)
            .collect(Collectors.toList());
    }

    /**
     * @brief Gets all tickets, including cancelled ones, for persistence and history.
     * @return A list of all Ticket objects.
     */
    public List<Ticket> getAllTickets() {
        return new ArrayList<>(tickets);
    }

    /**
     * @brief Adds a ticket loaded from the persistent storage.
     * @param ticket The ticket loaded from the database.
     */
    public void addLoadedTicket(Ticket ticket) {
        if (ticket != null && ticket.getOwner() == this) {
            tickets.add(ticket);
        }
    }

    /**
     * @brief Purchases a new ticket
     * @param ticket The ticket to purchase
     * @return true if the ticket was purchased successfully, false otherwise
     */
    public boolean purchaseTicket(Ticket ticket) {
        if (ticket == null) {
            userManagement.displayError("Invalid ticket information.");
            return false;
        }

        if (ticket.getOwner() != this) {
            userManagement.displayError("Ticket owner must match the authenticated passenger.");
            return false;
        }

        if (findTicketById(ticket.getId()) != null && ticket.isActive()) {
            userManagement.displayError("This ticket has already been purchased.");
            return false;
        }

        String transactionDescription = "Purchase ticket: " + ticket.getDescription();
        boolean success = paymentGateway.processTransaction(transactionDescription);

        if (!success) {
            userManagement.displayError("Ticket purchase failed. Please try again.");
            return false;
        }

        ticket.setStatus(Ticket.TicketStatus.ACTIVE);
        ticket.addHistoryEntry("PURCHASED");

        if (findTicketById(ticket.getId()) == null) {
            tickets.add(ticket);
        }

        String subject = "Ticket purchase completed";
        String body = "Hello " + getUsername() + ",\n\n" +
            "Your ticket purchase was successful.\n" +
            "Ticket: " + ticket.getDescription() + "\n\n" +
            "Thank you for using DispatchNet.";

        userManagement.sendEmail(new Email(getEmail(), subject, body));
        userManagement.saveTickets(); // persist tickets after successful purchase
        System.out.println("Ticket purchase completed successfully.");
        return true;
    }

    /**
     * @brief Cancels a purchased ticket
     * @param ticket The ticket to cancel
     * @return true if the ticket was canceled successfully, false otherwise
     */
    public boolean cancelTicket(Ticket ticket) {
        if (ticket == null) {
            userManagement.displayError("Invalid ticket information.");
            return false;
        }

        Ticket managedTicket = findTicketById(ticket.getId());
        if (managedTicket == null || !managedTicket.isActive()) {
            userManagement.displayError("Ticket not found in your active purchases.");
            return false;
        }

        String transactionDescription = "Cancel ticket: " + managedTicket.getDescription();
        boolean success = paymentGateway.processTransaction(transactionDescription);

        if (!success) {
            userManagement.displayError("Ticket cancellation failed. Please try again.");
            return false;
        }

        managedTicket.setStatus(Ticket.TicketStatus.CANCELLED);
        managedTicket.addHistoryEntry("CANCELLED");

        String subject = "Ticket cancellation completed";
        String body = "Hello " + getUsername() + ",\n\n" +
            "Your ticket cancellation was successful.\n" +
            "Ticket: " + managedTicket.getDescription() + "\n\n" +
            "Thank you for using DispatchNet.";

        userManagement.sendEmail(new Email(getEmail(), subject, body));
        userManagement.saveTickets(); // persist ticket cancellation status
        System.out.println("Ticket cancellation completed successfully.");
        return true;
    }

    /**
     * @brief Retrieves the history of purchased and cancelled tickets
     */
    public void getTicketsHistory() {
        System.out.println("Ticket history for " + getUsername() + ":");

        if (tickets.isEmpty()) {
            System.out.println("  No ticket purchases or cancellations found.");
            return;
        }

        for (Ticket ticket : tickets) {
            System.out.println("  Ticket: " + ticket.getDescription());
            System.out.println("    Status: " + ticket.getStatus());
            for (String event : ticket.getHistory()) {
                System.out.println("    " + event);
            }
            System.out.println();
        }
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

    /**
     * @brief Finds a ticket in the passenger tickets list by id.
     * @param ticketId The ticket id to search for.
     * @return The matching Ticket or null if not found.
     */
    private Ticket findTicketById(String ticketId) {
        return tickets.stream()
            .filter(ticket -> ticket.getId().equals(ticketId))
            .findFirst()
            .orElse(null);
    }
} 
