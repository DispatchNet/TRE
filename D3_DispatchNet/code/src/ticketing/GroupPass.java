package ticketing;

import java.util.ArrayList;

import user_management.Passenger;
/**
 * @class GroupPass
 * @brief A collection of tickets, bought by one passanger for mutiple people
 * @see Ticket
 * @see Passenger
 */
public class GroupPass {
  
  final Passenger owner;
  ArrayList<Ticket> list;
  
  //Public
  
  /**
   * @brief Basic constructor of GroupPass class
   * @details Does not have a List argument, creates an empty one
   * @param owner The Passenger performing the purchase
   */
  public GroupPass (Passenger owner) {
    this.owner = owner;
    this.list = new ArrayList<Ticket>();
  }
  
  /**
   * @brief Constructor of GroupPass class
   * @details Uses the List argument to initialize with pre-existing tickets
   * @param owner The Passenger performing the purchase
   * @param list A list of tickets
   */
  public GroupPass (Passenger owner, ArrayList<Ticket> list) {
    this.owner = owner;
    this.list = list;
  }
  
  /**
   * @brief Returns the owner
   * @return Passenger pointer
   */
  public Passenger getOwner() {
    return owner;
  }
  
  /**
   * @brief Returns the stored list
   * @return ArrayList of Tickets
   */
  public ArrayList<Ticket> getSubTickets () {
    return list;
  }

  /**
   * @brief Stored list setter
   * @param newList A list of Tickets
   */
  public void setSubTickets(ArrayList<Ticket> newList) {
    this.list = newList;
  }

  public static void main () {//Unit test
  
  }
}
