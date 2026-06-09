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

class Path_Finding{
	Passenger requester;
	Junction from;
	Junction to;
	LocalTime after; //Optional
	LocalTime before; //Optional
	srtAlg chosenSrt;
	ArrayList<ArrayList<Ticket>> results;

//Public	
		public Path_Finding(Passenger req_, Junction from_, Junction to_, LocalTime aft_, LocalTime bef_) {
			//TODO
		}

		public Passenger getRequester() {
			return requester;
		}

		public Junction getFrom() {
			return from;
		}

		public Junction getTo() {
			return to;
		}

		public LocalTime getAfter() {
			return after;
		}

		public LocalTime getBefore() {
			return before;
		}

		public srtAlg getChosenSrt() {
			return chosenSrt;
		}

		public void setChosenSrt(srtAlg newAlg) {
			chosenSrt = newAlg;
		}

		public ArrayList<ArrayList<Ticket>> getResults() {
			return srtPth();
		}

		public void prchTcks(int index) {
		  //TODO
		}

//Private
		ArrayList<ArrayList<Ticket>> srtPth() {
      Collections.sort(results, new PathComparator(chosenSrt));
			return results;
		}

//Unit test
		public static void main() {
		}
};

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

class sortByDeparture implements Comparator<ArrayList<Ticket>> {
  public int compare (ArrayList<Ticket> L1, ArrayList<Ticket> L2) {
    //TODO compare departure times
    return 0;
  }
};

class sortByArrival implements Comparator<ArrayList<Ticket>> {
  public int compare (ArrayList<Ticket> L1, ArrayList<Ticket> L2) {
    //TODO Compare arrival times
    return 0;
  }
};

class sortByCost implements Comparator<ArrayList<Ticket>> {
  public int compare (ArrayList<Ticket> L1, ArrayList<Ticket> L2) {
    //TODO Compare Cost
    return 0;
  }
};

class PathComparator implements Comparator<ArrayList<Ticket>> {
  private ArrayList<Comparator<ArrayList<Ticket>>> Comps;
  private srtAlg chosenSrt;

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
