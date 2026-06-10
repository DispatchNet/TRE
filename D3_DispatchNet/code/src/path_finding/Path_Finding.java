package path_finding;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import infrastructure.Junction;
import user_management.Passenger;
import ticketing.Ticket;

//TODO Temporary class, when properly implemented will be imported 

public enum srtAlg {Length, Departure, Arrival, Cost};
/**
 * @class Path_finding
 * @brief Class representing a path_finding request
 * @details created whith all the relevant information, stores the search result and is able to sort and produce it multiple times
 */
class Path_Finding{
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
     * @description Enforces mandatory information like requester and parameters of the search, initializes the sorting algorithm enum to its default value and computes the result lists
     * @param requester which Passenger initiated the request, used for ticket generation
     * @param from Where the search starts 
     * @param to Where the search ends 
     * @param after The earliest accepted time for the first train in a path to depart, is optional (all values pass) but if no before argument is specified defaults to current time
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
     * @reutrn a srtAlg enum
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
     * @description Calls the private srtPth function when necessary, and formats the results
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
     * @description calls the complex comparator PathComparator to sort in the correct order
     * @return A list of lists, of tickets
     * @see PathComparator
     */
		ArrayList<ArrayList<Ticket>> srtPth() {
      Collections.sort(results, new PathComparator(chosenSrt));
			return results;
		}

//Unit test
		public static void main() {
		}
};

/**
 * @class sortByLenght
 * @brief The comparator that allows the paths to be sorted by sortByLenght
 * @description Extracts time difference in a path and uses it as a primary sorting criterion, and uses junction count as a secondary one.
 * @return A negative integer if the first argument is shorter, a positive integer otherwise. If equal, returns 0
 */
class sortByLenght implements Comparator<ArrayList<Ticket>> {
  public int compare (ArrayList<Ticket> L1, ArrayList<Ticket> L2) {
    //TODO Get difference of depature time of first and arrival of last for each list 
    //Shorter one is better 
    //If times are the same, continue with size comparison
    int size1 = L1.size();
    int size2 = L2.size();

    return size1-size2;
  }
};

/**
 * @class sortByDeparture
 * @brief The comparator that allows the paths to be sorted by departure time
 * @description Extracts and compares the Departure information of the first train in each ticket list
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
 * @description Extracts and compares the arrival time from the lastStep information of the last ticket of each list
 * @returns A negative integer if the first argument is earlier, a positive integer otherwise. If equal, returns 0
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
 * @description Extracts and compares the sum total of costs of each ticket list 
 * @returns A negative integer if the first argument is cheaper, a postive integer otherwise. If equal, returns 0
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
 * @description Compares by the selected system first, and if the result is indecisive attempts to use all others in order
 * @returns A negative integer if the first argument is "better", a positive integer otherwise. If equal, returns 0
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
   * @description Initializes the sub-comparators, and sets the preferred one via the enum
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
