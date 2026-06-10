package ticketing;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import user_management.Passenger;
import service_management.ServiceStep;
import departure.Departure;
import infrastructure.Junction;

/**
 * @class Ticket
 * @brief Class representing a ticket, containing information about the owner, fist departure and last valid arrival
 */
public class Ticket{
  Passenger owner;
  Departure departure;
  ServiceStep lastStep;

  //Public
  
    /**
     * @brief Constructor of Ticket class
     * @description Constructs a ticket enforcing the mandatory owner, departure and step information
     * @param owner The owner of the ticket
     * @param departure The first station and train the ticket is valid from
     * @param lastStep The last station and train the ticket is valid for 
     * @see Departure
     * @see ServiceStep
     */
    public Ticket (Passenger owner, Departure departure, ServiceStep lastStep) {
      this.owner = owner;
      this.departure = departure;
      this.lastStep = lastStep;
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
     * @brief lastStep getter
     * @return a ServiceStep pointer
     * @see ServiceStep
     */
    public ServiceStep getLastStep() {
      return lastStep;
    }

    /**
     * @brief evalutes the total cost of the ticket
     * @description uses the depature and lastStep information to measure the distance covered and which train is used, then computes the resulting price
     * @return an integer representing the monetary value 
     */
    public int getCost () {
      //TODO evaluate cost 
      return 0;
    }
    
    /*
     * @brief Get the last junction the train is valid for
     * @description Looks into the lastStep value and extracts the junction it points total
     * @return a Junction pointer
     * @see Junction
     */
    public Junction getJunction () {
      //TODO get junction
      return lastStep.getJunction();
    }
  //Private

  //Unit test
		public static void main() {
      
		}
}
