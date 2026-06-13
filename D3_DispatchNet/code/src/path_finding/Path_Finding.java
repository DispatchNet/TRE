package path_finding;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import infrastructure.Junction;
import user_management.Passenger;
import ticketing.Ticket;
import path_finding.srtAlg;

/**
 * @class Path_finding
 * @brief Class representing a path_finding request
 * @details created whith all the relevant information, stores the search result and is able to sort and produce it multiple times
 */
public class Path_Finding{
	Passenger requester;
	Junction from;
	Junction to;
	LocalTime after; //Optional
	LocalTime before; //Optional
	srtAlg chosenSrt;
	ArrayList<ArrayList<Ticket>> results;

  //Public	
		/**
     * @brief creates the Pathfinding_class
     * 
     * @details Enforces mandatory information like requester and parameters of the search, initializes the sorting algorithm enum to its default value and computes the result lists
     * @param requester which Passenger initiated the request, used for ticket generation
     * @param from Where the search starts 
     * @param to Where the search ends 
     * @param after The earliest accepted time for the first train in a path to depart, is optional (all values pass) but if no "before" argument is specified defaults to current time
     * @param before The latest accepted time for the last train in a path to arrive, is optional (all values pass)
     * @exception NoPath The "from" Junction cannot reach the "to" Junction
     * @exception BadSearch No path respects both "after" and "before" argument
     * @see Passenger
     * @see Junction
     * @see Ticket
     */
    public Path_Finding(Passenger requester, Junction from, Junction to, LocalTime after, LocalTime before) {
			//TODO
		}
    
    /**
     * @brief requester getter 
     * @return a Passenger pointer
     */
		public Passenger getRequester() {
			return requester;
		}
    
    /**
     * @brief from getter
     * @return a Junction pointer
     */
		public Junction getFrom() {
			return from;
		}

    /**
     * @brief to getter
     * @return a Junction pointer
     */
		public Junction getTo() {
			return to;
		}
  
    /**
     * @brief after getter
     * @return a LocalTime
     */
		public LocalTime getAfter() {
			return after;
		}

    /**
     * @brief before getter
     * @return a LocalTime
     */
		public LocalTime getBefore() {
			return before;
		}
    
    /**
     * @brief chosenSrt getter
     * @return a srtAlg enum
     */
		public srtAlg getChosenSrt() {
			return chosenSrt;
		}
    
    /**
     * @brief chosenSrt setter
     * @param newAlg new enum value to use 
     */
		public void setChosenSrt(srtAlg newAlg) {
			chosenSrt = newAlg;
		}
    
    /**
     * @brief public way to access the results of the pathfinding 
     * 
     * @details Calls the private srtPth function when necessary, and formats the results
     * @return An ArrayList of paths (ArrayList of Tickets)
     */
		public ArrayList<ArrayList<Ticket>> getResults() {
			return srtPth();
		}
    
    /**
     * @brief Handles the pruchase of tickets from the POV of the pathfinder
     * @param index which one of the lists of tickets is the one chosen by the user 
     * @exception OutOfBounds The argument is not a valid index for the results list
     */
		public void prchTcks(int index) {
		  //TODO
		}

//Private
    /**
     * @brief sorts the results list according to the stored srtAlg
     * 
     * @details calls the complex comparator PathComparator to sort in the correct order
     * @return A list of lists, of tickets (List of Paths)
     * @see PathComparator
     */
		ArrayList<ArrayList<Ticket>> srtPth() {
      Collections.sort(results, new PathComparator(chosenSrt));
			return results;
		}

//Unit test
		public static void main() {
      System.out.println("Works");
		}
};

/**
 * @class sortByLenght
 * @brief The comparator that allows the paths to be sorted by sortByLenght
 * 
 * @details Extracts time difference in a path and uses it as a primary sorting criterion, and uses junction count as a secondary one.
 * @return A negative integer if the first argument is shorter, a positive integer otherwise. If equal, returns 0
 */
class sortByLenght implements Comparator<ArrayList<Ticket>> {
  public int compare (ArrayList<Ticket> L1, ArrayList<Ticket> L2) {
    //TODO Get difference of depature time of first and arrival of last for each list 
    
    LocalTime startL1 = L1.get(0).getDeparture().time();
    LocalTime endL1 = L1.get(L1.size()-1).getDeparture().time();
    int offsetL1 = L1.get(L1.size()-1).getLastStep().getTravelMinutes();

    LocalTime startL2 = L2.get(0).getDeparture().time();
    LocalTime endL2 = L2.get(L2.size()-1).getDeparture().time();
    int offsetL2 = L2.get(L2.size()-1).getLastStep().getTravelMinutes();
    
    long diffL1 = MINUTES.between(startL1, endL1.plusMinutes(offsetL1));
    long diffL2 = MINUTES.between(startL2, endL2.plusMinutes(offsetL2));
    
    //Shorter one is better
    if (diffL1 < diffL2) return -1;
    if (diffL2 > diffL2) return 1;
    
    //Compare Km lenght 
    int metersL1 = 0;
    for (int i = 0; i<L1.size(); i++) {
      metersL1 += L1.get(i).getLength();
    } 

    int metersL2 = 0;
    for (int i = 0; i<L2.size(); i++) {
      metersL2 += L2.get(i).getLength();
    }

    if (metersL1 != metersL2) return metersL1-metersL2;

    //If times and lenght are the same, continue with size comparison
    int size1 = L1.size();
    int size2 = L2.size();

    return size1-size2;
  }
};

/**
 * @class sortByDeparture
 * @brief The comparator that allows the paths to be sorted by departure time
 * 
 * @details Extracts and compares the Departure information of the first train in each ticket list
 * @return A negative integer if the first argument is after, a positive integer otherwise. If equal, returns 0
 */
class sortByDeparture implements Comparator<ArrayList<Ticket>> {
  public int compare (ArrayList<Ticket> L1, ArrayList<Ticket> L2) {
    //TODO compare departure times
    return 0;
  }
};
/**
 * @class sortByArrival
 * @brief The comparator that allows the paths to be sorted by arrival time
 * 
 * @details Extracts and compares the arrival time from the lastStep information of the last ticket of each list
 * @return A negative integer if the first argument is earlier, a positive integer otherwise. If equal, returns 0
 */
class sortByArrival implements Comparator<ArrayList<Ticket>> {
  public int compare (ArrayList<Ticket> L1, ArrayList<Ticket> L2) {
    //TODO Compare arrival times
    return 0;
  }
};

/**
 * @class sortByCost
 * @brief The comparator that allows the paths to be sorted by Cost
 * 
 * @details Extracts and compares the sum total of costs of each ticket list 
 * @return A negative integer if the first argument is cheaper, a postive integer otherwise. If equal, returns 0
 */
class sortByCost implements Comparator<ArrayList<Ticket>> {
  public int compare (ArrayList<Ticket> L1, ArrayList<Ticket> L2) {
    //TODO Compare Cost
    return 0;
  }
};

/**
 * @class PathComparator
 * @brief Complex comparator that allows paths sorting according to a provided srtAlg parameter
 *
 * @details Compares by the selected system first, and if the result is indecisive attempts to use all others in order
 * @return A negative integer if the first argument is "better", a positive integer otherwise. If equal, returns 0
 * @see sortByLenght
 * @see sortByDeparture
 * @see sortByArrival
 * @see sortByCost
 */
class PathComparator implements Comparator<ArrayList<Ticket>> {
  private ArrayList<Comparator<ArrayList<Ticket>>> Comps;
  private srtAlg chosenSrt;
  
  /**
   * @brief Comparator constructor 
   * 
   * @details Initializes the sub-comparators, and sets the preferred one via the enum
   * @param alg The preferred sorting enum
   */
  public PathComparator (srtAlg alg) {
    this.Comps = new ArrayList<Comparator<ArrayList<Ticket>>>();
    this.Comps.add(new sortByLenght());
    this.Comps.add(new sortByDeparture());
    this.Comps.add(new sortByArrival());
    this.Comps.add(new sortByCost());
    this.chosenSrt = alg;
  }

  @Override
  public int compare (ArrayList<Ticket> L1, ArrayList<Ticket> L2) {
   
    int sortVal = 0; //Stores the return values
    switch (chosenSrt) { //Start with the chosen sorting mehtod
      case Length:
        sortVal = Comps.get(0).compare(L1, L2);
      break;

      case Departure:
        sortVal = Comps.get(1).compare(L1, L2);
      break;

      case Arrival:
        sortVal = Comps.get(2).compare(L1, L2);
      break;

      case Cost:
        sortVal = Comps.get(3).compare(L1, L2);
      break;

      default:
        //How did we get here?
    }

    for (int i = 0; i<4 && sortVal == 0; i++) { //If the sorth is inconclusive, try all the others
      sortVal = Comps.get(i).compare(L1, L2);
    }

    return sortVal;
  }
}
