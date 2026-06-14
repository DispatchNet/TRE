package main;
import infrastructure.Network;
import service_management.ServiceManagement;
import user_management.AnonymousUser;
import user_management.AuthenticatedUser;
import user_management.EndUser;
import user_management.NetworkManager;
import user_management.Passenger;
import user_management.TrainCompany;
import user_management.UserManagement;
import IO_operations.IO;
import csv_database.CsvDatabase;

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

      // common options (not for Network Manager)
      if(! (currentUser instanceof NetworkManager)) {
        System.out.println("a) Account");
        System.out.println("p) Path finding");

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
      if(! (currentUser instanceof NetworkManager)) {
        switch(choice) {
          case "a": accountInterface(currentUser); break;
          case "p": pathFindingInterface(currentUser); break;
          case "q": exitRequested = true; break;
          default: break;
        }
        if(currentUser instanceof Passenger) {
          if(choice == "t")
            ((Passenger) currentUser).getTicketsHistory();
        }
        else if(currentUser instanceof TrainCompany) {
          if(choice == "s")
            ((TrainCompany) currentUser).createServiceRequest();
        }
      }
      else {
        switch(choice) {
          case "a": accountInterface(currentUser); break;
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

  private static void pathFindingInterface(Object user) {} //TODO: add when done

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
}
