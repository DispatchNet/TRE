package main;

import java.time.LocalTime;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.lang.StringBuilder;

import infrastructure.Network;
import service_management.ServiceManagement;
import user_management.AnonymousUser;
import user_management.NetworkManager;
import user_management.Passenger;
import user_management.TrainCompany;
import user_management.EndUser;
import user_management.AuthenticatedUser;
import user_management.UserManagement;
import path_finding.Path_Finding;
import infrastructure.Junction;
import IO_operations.IO;
import csv_database.CsvDatabase;

import service_management.ServiceSet;

/**
 * @class holds all globally relevant information
 */
public class Main {
  // IO instance for user input and output
  private final static IO io = new IO();
  // CSV database instance for loading and saving user data, network elements and tickets
  private final static CsvDatabase csvDatabase = new CsvDatabase();
  // The rail network graph, including all junctions and services
  static final Network network = new Network();
  // The service management system for handling service sets and requests
  static final ServiceManagement serviceManagement = new ServiceManagement();
  // The user management system for handling users, authentication, and tickets
  static final UserManagement userManagement = new UserManagement(io, csvDatabase);

  public static Network getNetwork() {
    return network;
  }

  public static ServiceManagement getServiceManagement() {
    return serviceManagement;
  }

  public static UserManagement getUserManagement() {
    return userManagement;
  }

  public static void main(String[] args) {
    // Load objects from CSV files
    csvDatabase.loadNetwork(network);
    csvDatabase.loadAuthenticatedUsers(userManagement); // also loads tickets

    // Start the interactive interface flow
    runInterface();

    // Save objects to CSV files before exiting
    csvDatabase.saveNetwork(network);
    csvDatabase.saveAuthenticatedUsers(userManagement.getAuthenticatedUsers());
    try {
        csvDatabase.saveTickets(userManagement);
    } catch (Exception e) {
        System.err.println("Failed to save tickets: " + e.getMessage());
    }

    io.printLn("Thank you for using DispatchNet. Goodbye!");
  }

  private static void runInterface() {
    io.printLn("--- Welcome to DispatchNet! ---");

    boolean exitRequested = false;

    while(!exitRequested) {
      io.printLn("\n--- DispatchNet Homepage ---");

      // get current user
      Object currentUser = userManagement.isAuthenticated() ? 
        userManagement.getAuthenticatedUser() : 
        userManagement.getSession().getAnonymousUser();

      // common options
      io.printLn("a) Account");
      // common options (not for Network Manager)
      if(! (currentUser instanceof NetworkManager)) {
        // Passenger and Anonymous only
        if(!(currentUser instanceof TrainCompany)) {
          io.printLn("p) Path finding");
          io.printLn("s) Search for station or train");
        }
        // Passenger only
        if(currentUser instanceof Passenger)
          io.printLn("t) Ticket history");
        // TrainCompany only
        else if(currentUser instanceof TrainCompany)
          io.printLn("s) Service");
      }
      // NetworkManager only
      else {
        io.printLn("n) Network");
        io.printLn("r) Register Train Company");
      }
      // common options
      io.printLn("m) Show Map");
      io.printLn("q) Quit");

      // get input
      String choice = io.prompt("Enter your choice:").trim();

      // handle input
      if(choice.equals("a"))
          accountInterface(currentUser);
      if(! (currentUser instanceof NetworkManager)) {
        switch(choice) {
          case "q": exitRequested = true; break;
          default: break;
        }
        if(currentUser instanceof TrainCompany) {
          if(choice.equals("s"))
            ((TrainCompany) currentUser).createServiceRequest();
        }
        else {
          switch(choice) {
            case "m": printMap(); break;
            case "p": pathFindingInterface(currentUser); break;
            case "s": stationTrainSearchInterface(currentUser); break;
          }
          if(currentUser instanceof Passenger) {
            if(choice.equals("t"))
              ((Passenger) currentUser).getTicketsHistory();
          }
        }
      }
      else {
        switch(choice) {
          case "n": networkInterface((NetworkManager) currentUser); break;
          case "r": ((NetworkManager) currentUser).createTrainCompanyAccount(); break;
          case "q": exitRequested = true; break;
          default: io.printLn("Invalid input. Try again");
        }
      }
    }
  }

  private static void accountInterface(Object user) {
    if(user instanceof AnonymousUser) {
      // show options
      io.printLn("\n--- Authentication page ---");
      io.printLn("l) Login");
      io.printLn("r) Register");
      io.printLn("p) Reset password");

      // get input
      String choice = io.prompt("Enter your choice:").trim();

      // handle input
      switch(choice) {
        case "l": ((AnonymousUser) user).login(); break;
        case "r": ((AnonymousUser) user).register(); break;
        case "p": ((AnonymousUser) user).resetPassword(); break;
        default: io.printLn("Invalid input."); break;
      }
    }
    else {
      io.printLn("\n--- Authentication page ---");
      // network manager
      if(user instanceof NetworkManager) {
        // show options
        io.printLn("l) Logout");

        // get input
        String choice = io.prompt("Enter your choice:").trim();

        // handle input
        switch(choice) {
          case "l": ((AuthenticatedUser) user).logout(); break;
          default: io.printLn("Invalid input."); break;
        }
      }
      // end user
      else {
        // show options
        io.printLn("c) Change data");
        io.printLn("d) Delete account");
        io.printLn("l) Logout");

        // get input
        String choice = io.prompt("Enter your choice:").trim();

        switch(choice) {
          case "c": ((EndUser) user).changeData(); break;
          case "d": ((EndUser) user).deleteAccount(); break;
          case "l": ((AuthenticatedUser) user).logout(); break;
          default: io.printLn("Invalid input."); break;
        }
      }
    }
  }

  private static Path_Finding pathFindingInterface(Object user) {
    Optional<Junction> from = network.getJunctionByName(io.prompt("From station:"));
    Optional<Junction> to = network.getJunctionByName(io.prompt("To station:"));
    LocalTime after; //these can be optional, parsing them immediately will break
    LocalTime before; 
    String maybeAfter = io.prompt("After time:");
    String maybeBefore = io.prompt("Before time:");
      
    if (maybeAfter != "") {
      try {
        after = LocalTime.parse(maybeAfter);
      } catch (Exception e) {
        io.println("Invalid time, defaulting to none");
        after = null;
      }
    } else after = null;

    if (maybeBefore != "") {
      try {
        before = LocalTime.parse(maybeBefore);
      } catch (Exception e) {
        io.println("Invalid time, defaulting to none");
        before = null;
      }
    } else before = null;


    Path_Finding out = null;
    try{
      out = new Path_Finding(user, from.orElse(null), to.orElse(null), after, before);
    } catch (Exception e) {
      out = null; //Make sure no half-baked stuff is getting sent
    } finally {
      return out;
    }
  }

  private static void networkInterface(NetworkManager user) {
    io.printLn("\n--- Network Management page ---");
    io.printLn("Current Network");
    user.viewNetwork();

    // show options
    io.printLn("c) Create");
    io.printLn("e) Edit");
    io.printLn("d) Delete");

    // get input
    String choice = io.prompt("Enter your choice:").trim();
    String choice1; //used later
    String elementId; //used later

    switch(choice) {
      case "c": // create
        // show options
        io.printLn("s) Station");
        io.printLn("j) Junction");
        io.printLn("l) Line");

        // get input
        choice1 = io.prompt("Enter your choice:").trim();

        // handle input
        switch(choice1) {
          case "s": user.createStation(); break; //station
          case "j": user.createJunction(); break; //junction
          case "l": user.createLine(); break; //line
          default: io.printLn("Invalid input."); break;
        }

        break;
      case "e": // edit
        // show options
        io.printLn("s) Station");
        io.printLn("j) Junction");
        io.printLn("l) Line");

        // get input
        choice1 = io.prompt("Enter your choice:").trim();
        elementId = io.prompt("Enter element id:").trim();

        // handle input
        switch(choice1) {
          case "s": user.editStation(elementId); break; //station
          case "j": user.editJunction(elementId); break; //junction
          case "l": user.editLine(elementId); break; //line
          default: io.printLn("Invalid input."); break;
        }

        break;
      case "d": // delete
        // show options
        io.printLn("s) Station");
        io.printLn("j) Junction");
        io.printLn("l) Line");

        // get input
        choice1 = io.prompt("Enter your choice:").trim();
        elementId = io.prompt("Enter element id:").trim();

        // handle input
        switch(choice1) {
          case "s": user.deleteStation(elementId); break; //station
          case "j": user.deleteJunction(elementId); break; //junction
          case "l": user.deleteLine(elementId); break; //line
          default: io.printLn("Invalid input."); break;
        }

        break;
      default: io.printLn("Invalid input."); break;
    }
  }

  private static void stationTrainSearchInterface (Object user) {
    
    String searchTerm = io.prompt("Search: ");

    Optional<Junction> junc = network.getJunctionByName(searchTerm);
    Optional<ServiceSet> trains = serviceManagement.getServiceSet(searchTerm);

    if (junc.isPresent()) {
      io.print("The query matched the Station:\n");
      io.print(junc.get().getName() + "\t°: " + junc.get().getLocation().latitude + "  " + junc.get().getLocation().longitude+ "\n");
    } else {
      io.print("Query did not match any stations\n\n");
    }

    if (trains.isPresent()) {
      io.print("The query matched the Train\n");
      io.print(trains.get().getCompany().getUsername() + " " + trains.get().getId() + "\n");
    } else {
      io.print("The query did not match any trains\n");
    }

  }
  
  public static void printMap () {

    Map<String, Junction> juncs = network.getJunctions();
    StringBuilder outLine = new StringBuilder();

    for (Map.Entry<String, Junction> entry : juncs.entrySet()) {
      Junction junc = entry.getValue();
      List<Junction> neighList = junc.getNeighbours();
      outLine.append(junc.getName() + ":\t");
      for (Junction neighbour : neighList) {
        outLine.append(neighbour.getName() + "\t"); 
      }
      outLine.append("\n");
    }
    
    System.out.print(outLine.toString());

  }
}
