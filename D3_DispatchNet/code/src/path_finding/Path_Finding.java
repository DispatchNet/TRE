import java.time.LocalTime;
import java.util.List;

class Passenger {}; //Temporary passenger class, when properly implemented will be imported

class Junction {}; //Temporary Junction class

class Ticket {}; 

enum srtAlg {Lenght, Departure, Arrival, Cost};

class Path_Finding{
	Passenger requester;
	Junction from;
	Junction to;
	LocalTime after; //Optional
	LocalTime before; //Optional
	srtAlg chosenSrt;
	List<List<Ticket>> results;

	
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

		public List<List<Ticket>> getResults() {
			return srtPth();
		}

		public void prchTcks(int index) {
			//TODO
		}



		List<List<Ticket>> srtPth() {
			//TODO
			return results;
		}


		public static void main() {
		}
};
