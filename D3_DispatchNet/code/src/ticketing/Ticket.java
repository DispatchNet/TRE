package ticketing;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.time.LocalTime;

import user_management.Passenger;
import service_management.ServiceStep;
import departure.Departure;
import infrastructure.Junction;
import infrastructure.Line;

/**
 * @class Ticket
 * @brief Class representing a ticket, containing information about the owner, first departure and last valid arrival
 */
public class Ticket {
    private final String id;
    private final Passenger owner;
    private final Departure departure;
    private final ServiceStep lastStep;
    private final String description;
    private TicketStatus status;
    private final List<String> history = new ArrayList<>();

    /**
     * @brief Constructor of Ticket class
     * @details Constructs a ticket enforcing the mandatory owner, departure and step information
     * @param owner The owner of the ticket
     * @param departure The first station and train the ticket is valid from
     * @param lastStep The last station and train the ticket is valid for 
     * @see Departure
     * @see ServiceStep
     */
    public Ticket(Passenger owner, Departure departure, ServiceStep lastStep) {
        this(UUID.randomUUID().toString(), owner, departure, lastStep);
    }

    /**
     * @brief Constructor of Ticket class with explicit id
     * @details Enforces mandatory id by checking if provided one is NULL and assigning one if it is 
     * @param id Possible string to treat as identifier
     * @param owner The owner of the ticket
     * @param departure The first station and train the ticket is valid from
     * @param lastStep The last station and train the ticket is valid for 
     * @see Departure
     * @see ServiceStep
     */
    public Ticket(String id, Passenger owner, Departure departure, ServiceStep lastStep) {
        this.id = id == null ? UUID.randomUUID().toString() : id;
        this.owner = owner;
        this.departure = departure;
        this.lastStep = lastStep;
        this.status = TicketStatus.INACTIVE;
        this.description = buildDescription();
    }

    /**
     * @brief Constructor used when loading tickets from storage.
     * @details Preserves status and history, but not departure or lastStep
     * @param id Possible string to treat as identifier
     * @param owner The owner of the ticket
     * @param description Description of the ticket
     * @param status Status of the ticket
     * @param history List of strings containing the history of the ticket
     */
    public Ticket(String id, Passenger owner, String description, TicketStatus status, List<String> history) {
        this.id = id == null ? UUID.randomUUID().toString() : id;
        this.owner = owner;
        this.departure = null;
        this.lastStep = null;
        this.description = description == null ? "" : description;
        this.status = status == null ? TicketStatus.INACTIVE : status;
        if (history != null) {
            this.history.addAll(history);
        }
    }

    /**
     * @brief Returns the unique ticket identifier.
     * @return The ticket id.
     */
    public String getId() {
        return id;
    }

    /**
     * @brief Owner getter 
     * @return a Passenger pointer
     * @see Passenger
     */
    public Passenger getOwner() {
        return owner;
    }
    
    /**
     * @brief Departure getter
     * @return a Departure pointer
     * @see Departure
     */
    public Departure getDeparture() {
        return departure;
    }

    /**
     * @brief Returns the ticket description.
     * @return A human-readable summary of the ticket.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @brief Gets the current ticket status.
     * @return The current TicketStatus.
     */
    public TicketStatus getStatus() {
        return status;
    }

    /**
     * @brief Sets the ticket status.
     * @param status The new status.
     */
    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    /**
     * @brief Returns true when the ticket is currently active.
     * @return true if status is ACTIVE.
     */
    public boolean isActive() {
        return status == TicketStatus.ACTIVE;
    }

    /**
     * @brief Gets the list of history events for this ticket.
     * @return A copy of history entries.
     */
    public List<String> getHistory() {
        return new ArrayList<>(history);
    }

    /**
     * @brief Adds an entry to the ticket lifecycle history.
     * @param entry The history event to add.
     */
    public void addHistoryEntry(String entry) {
        if (entry != null && !entry.isBlank()) {
            history.add(entry);
        }
    }

    /**
     * @brief lastStep getter
     * @return a ServiceStep pointer
     * @see ServiceStep
     */
    public ServiceStep getLastStep() {
        return lastStep;
    }

    /**
     * @brief evalutes the total cost of the ticket
     * @details uses the departure and lastStep information to measure the distance covered and which train is used, then computes the resulting price
     * @return an integer representing the monetary value
     */
    public int getCost() {
      int centsPerKm = this.departure.serviceSet().getType().getCentsPerKm();
      int meters = this.getLength();
      
      return centsPerKm*meters/1000;
    }
    
    /*
     * @brief Get the last junction the train is valid for
     * @details Looks into the lastStep value and extracts the junction it points total
     * @return a Junction pointer
     * @see Junction
     */
    public Junction getJunction() {
        return lastStep == null ? null : lastStep.getJunction();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket ticket)) return false;
        return Objects.equals(id, ticket.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * @brief Constructs a human-readable description from departure and lastStep.
     * @return A string describing the ticket route.
     */
    private String buildDescription() {
        if (departure == null || lastStep == null) {
            return "";
        }
        // TODO: uncomment when ready
        Junction startJunction = null; //departure.serviceStep().getStationData().getJunction();
        Junction endJunction = lastStep.getJunction();
        if (startJunction == null || endJunction == null) {
            return "";
        }
        return startJunction.getName() + " to " + endJunction.getName();
    }

    public static enum TicketStatus {
        INACTIVE,
        ACTIVE,
        CANCELLED
    }

    /**
     * @brief returns the lenght in meters of the ticket's course
     * @return an integer representing meters of lenght
     */
    public int getLength() {
      
      ServiceStep first = this.departure.serviceStep();
      ServiceStep last = lastStep;

      List<ServiceStep> list = this.departure.serviceSet().getSteps();
      
      ArrayList<Junction> passed_junctions = new ArrayList<Junction>();

      boolean in_bounds = false;
      for (int i = 0; i<list.size() && list.get(i) != last; i++) {
        if (!in_bounds && list.get(i) == first) in_bounds = true;
        if (in_bounds) passed_junctions.add(list.get(i).getJunction());
      }
      passed_junctions.add(last.getJunction());

      int meters = 0;

      for (int i = 1; i<passed_junctions.size(); i++) {
        meters += passed_junctions.get(i-1).getToOther(passed_junctions.get(i)).getLengthMeters();
      }

      return meters;
    }

    public LocalTime getArrival() {
        
      ServiceStep first = this.departure.serviceStep();
      ServiceStep last = lastStep;

      List<ServiceStep> list = this.departure.serviceSet().getSteps();

      long offset = 0;

      boolean in_bounds = false;
      for (int i = 0; i<list.size() && list.get(i) != last; i++) {
        if (in_bounds) offset += list.get(i).getTravelMinutes();//Line earlier than flag check because first is not counted
        if (!in_bounds && list.get(i) == first) in_bounds = true;
      }
      offset += last.getTravelMinutes();

      return this.departure.time().plusMinutes(offset);

    }

    //Unit test
    public static void main(String[] args) {
        System.out.println("Ticket class loaded successfully.");
    }
}
