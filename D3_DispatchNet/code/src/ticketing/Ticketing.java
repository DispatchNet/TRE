package ticketing;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import user_management.Passenger;
import service_management.ServiceStep;
import departure.Departure;
import infrastructure.Junction;

public class Ticket{
  Passenger owner;
  Departure departure;
  ServiceStep lastStep;

  //Public	
    public Passenger getOwner() {
      return owner
    }

    public Departure getDeparture() {
      return departure
    } 

    public ServiceStep getLastStep() {
      return lastStep
    }

    public int getCost () {
      //TODO evaluate cost 
      return 0
    }

    public Junction getJunction () {
      //TODO get junction
      return lastStep.getJunction();
    }
  //Private

  //Unit test
		public static void main() {
		}
};

