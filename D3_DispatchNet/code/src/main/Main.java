package main;

import java.time.LocalTime;
import java.util.Optional;

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

    System.out.println("Thank you for using DispatchNet. Goodbye!");
  }

  private static void runInterface() {
    System.out.println("--- Welcome to DispatchNet! ---");

    boolean exitRequested = false;

    while(!exitRequested) {
      System.out.println("\n--- DispatchNet Homepage ---");

      // get current user
      Object currentUser = userManagement.isAuthenticated() ? 
        userManagement.getAuthenticatedUser() : 
        userManagement.getSession().getAnonymousUser();

      // common options
      System.out.println("a) Account");
      // common options (not for Network Manager)
      if(! (currentUser instanceof NetworkManager)) {
        // Passenger and Anonymous only
        if(!(currentUser instanceof TrainCompany)) {
          System.out.println("p) Path finding");
          System.out.println("s) Search for station or train");
        }
        // Passenger only
        if(currentUser instanceof Passenger)
          System.out.println("t) Ticket history");
        // TrainCompany only
        else if(currentUser instanceof TrainCompany)
          System.out.println("s) Service");
      }
      // NetworkManager only
      else {
        System.out.println("n) Network");
        System.out.println("r) Register Train Company");
      }
      // common option
      System.out.println("q) Quit");

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
          default: System.out.println("Invalid input. Try again");
        }
      }
    }
  }

  private static void accountInterface(Object user) {
    if(user instanceof AnonymousUser) {
      // show options
      System.out.println("\n--- Authentication page ---");
      System.out.println("l) Login");
      System.out.println("r) Register");
      System.out.println("p) Reset password");

      // get input
      String choice = io.prompt("Enter your choice:").trim();

      // handle input
      switch(choice) {
        case "l": ((AnonymousUser) user).login(); break;
        case "r": ((AnonymousUser) user).register(); break;
        case "p": ((AnonymousUser) user).resetPassword(); break;
        default: System.out.println("Invalid input."); break;
      }
    }
    else {
      System.out.println("\n--- Authentication page ---");
      // network manager
      if(user instanceof NetworkManager) {
        // show options
        System.out.println("l) Logout");

        // get input
        String choice = io.prompt("Enter your choice:").trim();

        // handle input
        switch(choice) {
          case "l": ((AuthenticatedUser) user).logout(); break;
          default: System.out.println("Invalid input."); break;
        }
      }
      // end user
      else {
        // show options
        System.out.println("c) Change data");
        System.out.println("d) Delete account");
        System.out.println("l) Logout");

        // get input
        String choice = io.prompt("Enter your choice:").trim();

        switch(choice) {
          case "c": ((EndUser) user).changeData(); break;
          case "d": ((EndUser) user).deleteAccount(); break;
          case "l": ((AuthenticatedUser) user).logout(); break;
          default: System.out.println("Invalid input."); break;
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
        after = null;
      }
    } else after = null;

    if (maybeBefore != "") {
      try {
        before = LocalTime.parse(maybeBefore);
      } catch (Exception e) {
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
    System.out.println("\n--- Network Management page ---");
    System.out.println("Current Network");
    user.viewNetwork();

    // show options
    System.out.println("c) Create");
    System.out.println("e) Edit");
    System.out.println("d) Delete");

    // get input
    String choice = io.prompt("Enter your choice:").trim();
    String choice1; //used later
    String elementId; //used later

    switch(choice) {
      case "c": // create
        // show options
        System.out.println("s) Station");
        System.out.println("j) Junction");
        System.out.println("l) Line");

        // get input
        choice1 = io.prompt("Enter your choice:").trim();

        // handle input
        switch(choice1) {
          case "s": user.createStation(); break; //station
          case "j": user.createJunction(); break; //junction
          case "l": user.createLine(); break; //line
          default: System.out.println("Invalid input."); break;
        }

        break;
      case "e": // edit
        // show options
        System.out.println("s) Station");
        System.out.println("j) Junction");
        System.out.println("l) Line");

        // get input
        choice1 = io.prompt("Enter your choice:").trim();
        elementId = io.prompt("Enter element id:").trim();

        // handle input
        switch(choice1) {
          case "s": user.editStation(elementId); break; //station
          case "j": user.editJunction(elementId); break; //junction
          case "l": user.editLine(elementId); break; //line
          default: System.out.println("Invalid input."); break;
        }

        break;
      case "d": // delete
        // show options
        System.out.println("s) Station");
        System.out.println("j) Junction");
        System.out.println("l) Line");

        // get input
        choice1 = io.prompt("Enter your choice:").trim();
        elementId = io.prompt("Enter element id:").trim();

        // handle input
        switch(choice1) {
          case "s": user.deleteStation(elementId); break; //station
          case "j": user.deleteJunction(elementId); break; //junction
          case "l": user.deleteLine(elementId); break; //line
          default: System.out.println("Invalid input."); break;
        }

        break;
      default: System.out.println("Invalid input."); break;
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

}
