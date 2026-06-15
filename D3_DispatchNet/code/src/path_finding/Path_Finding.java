package path_finding;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.List; //Import this because Luca is a lazy bum

import infrastructure.Junction;
import user_management.Passenger;
import ticketing.Ticket;
import service_management.ServiceSet;
import service_management.ServiceStep;
import departure.Departure;

import IO_operations.IO;

/**
 * @class Path_finding
 * @brief Class representing a path_finding request
 * @details created whith all the relevant information, stores the search result and is able to sort and produce it multiple times
 */
public class Path_Finding{
	final Passenger requester;
	final Junction from;
	final Junction to;
	final LocalTime after; //Optional
	final LocalTime before; //Optional
	srtAlg chosenSrt;
	ArrayList<ArrayList<Ticket>> results;

  //Private constructor helpers
  boolean is_after (Departure dep, Junction to) {
    ServiceSet serv = dep.serviceSet();
    boolean found_first = false;
    boolean out = false;
    for (int i = 0; i<serv.getSteps().size() && !out; i++) {
      if (serv.getSteps().get(i) == dep.serviceStep()) found_first = true;
      if (found_first && serv.getSteps().get(i).getJunction() == to) out = true;
    }

    return out;
  }


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
      
      if (requester == null || from == null || to == null) {
        //TODO error out here
        return null;
      }


      final int MIN_RESULTS = 10;
      final int MAX_ITERATIONS = 10000;

      this.requester = requester;
      this.from = from;
      this.to = to;
      if (after == null && before == null) this.after = LocalTime.now();
      else  this.after = after;
      this.before = before;
      
      this.chosenSrt = srtAlg.Length;

      ArrayList<Integer> previous = new ArrayList<Integer>();
      ArrayList<Departure> queue = new ArrayList<Departure>();
      ArrayList<Integer> offsetMin = new ArrayList<Integer>();
      ArrayList<Integer> candidates = new ArrayList<Integer>();
      
      //Prepare queue and the history holder
      for (int i = 0; i<from.getDepartures().size(); i++) {
        if (this.after == null || !from.getDepartures().get(i).time().isBefore(this.after)) {
          queue.add(from.getDepartures().get(i));
          previous.add(-1);
          offsetMin.add(0);
        }
      }

      //Width-first search
      for (int i = 0; i<queue.size() && candidates.size() < MIN_RESULTS && i<MAX_ITERATIONS; i++) {
        if (is_after(queue.get(i), this.to)) candidates.add(i); //If goes to, candidate solution
        else {//else expand queue
          boolean found_first = false;
          List<ServiceStep> list = queue.get(i).serviceSet().getSteps();
          ServiceStep first = queue.get(i).serviceStep();
          LocalTime now = queue.get(i).time();
          int nowffset = offsetMin.get(i);

          for (int ii = 0; ii<list.size() && (this.before == null || !this.before.isBefore(now.plusMinutes(nowffset))); ii++) {
            if (found_first) {
              nowffset += list.get(ii).getTravelMinutes();
              Junction current = list.get(ii).getJunction();
              for (int iii = 0; iii<current.getDepartures().size(); iii++) {//Iterate over all deparures of the Junction
                Departure dep = current.getDepartures().get(iii);
                if (!dep.time().isBefore(now.plusMinutes(nowffset)) && !dep.time().isAfter(this.before) && !queue.contains(dep)) {//Only add departures that happen after arrival and before the limit, and aren't in queue yet
                  queue.add(dep);
                  previous.add(i);
                  offsetMin.add(nowffset);
                }
              }
            }
            if (list.get(i) == first) found_first = true;//Only start counting after having arrived at the right step
          }
        }
      }
      
      ArrayList<ArrayList<Departure>> candidate_departures = new ArrayList<ArrayList<Departure>>();
      for (int i = 0; i<candidates.size(); i++) {
        ArrayList<Departure> tempList = new ArrayList<Departure>();
        int c = candidates.get(i); //current number
        while (c != -1) {
          tempList.add(queue.get(c));
          c = previous.get(c);
        }
        Collections.reverse(tempList);
        candidate_departures.add(tempList);
      }

      ArrayList<ArrayList<Ticket>> results = new ArrayList<ArrayList<Ticket>>();
      for (int i = 0; i<candidate_departures.size(); i++) {
        ArrayList<Ticket> tempList = new ArrayList<Ticket>();
        for (int ii = 0; ii<candidate_departures.get(i).size()-1; ii++) {//All elements except the last one are trivial
          Ticket newticket = new Ticket(
            this.requester,
            candidate_departures.get(i).get(ii),
            candidate_departures.get(i).get(ii+1).serviceStep()//The first step of the next departure is obviously the last of the current
          );
          tempList.add(newticket);
        }
        //Need to find the correct serviceStep into "to"
        List<ServiceStep> stepList = candidate_departures.get(i).get(candidate_departures.get(i).size()-1).serviceSet().getSteps();
        ServiceStep prevStep = candidate_departures.get(i).get(candidate_departures.get(i).size()-1).serviceStep();
        boolean found_first = false;
        ServiceStep theStep = null;
        for (int ii = 0; ii<stepList.size(); ii++) {
          if (stepList.get(ii) == prevStep) found_first = true;
          if (found_first && stepList.get(ii).getJunction() == this.to) theStep = stepList.get(ii);
        }
        if (theStep == null) {
          //Something went wrong
          //TODO error out here
          return null
        }
        tempList.add(new Ticket(
          this.requester,
          candidate_departures.get(i).get(candidate_departures.get(i).size()-1),
          theStep
        ));
        
        results.add(tempList);
      }

      this.results = results;
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
     * @exception OutOfBounds The argument is not a valid index for the results list
     */
		public void prchTcks() {
		  //TODO manage IO
      
      int index = 0;
      boolean ok_flag = true;
      
      //form query string with Junction names  

      //Get index from user
      do {
        //set index to some value from user
        if ()
      }
      


      //Select ticket here by some way


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
    
    if (L1.get(0).getDeparture().time() == L2.get(0).getDeparture().time()) return 0;
    
    return L1.get(0).getDeparture().time().isBefore(L2.get(0).getDeparture().time())? -1 : 1;
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
    Ticket lastTicketL1 = L1.get(L1.size()-1);
    Ticket lastTicketL2 = L2.get(L2.size()-1);
    
    if (lastTicketL1.getArrival() == lastTicketL2.getArrival()) return 0;
    return lastTicketL1.getArrival().isBefore(lastTicketL2.getArrival())? -1 : 1;
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
    int costL1 = 0;
    int costL2 = 0;
    
    for (int i = 0; i<L1.size(); i++) {
      costL1 += L1.get(i).getCost();
    }

    for (int i = 0; i<L2.size(); i++) {
      costL2 += L2.get(i).getCost();
    }

    return costL1-costL2;
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
