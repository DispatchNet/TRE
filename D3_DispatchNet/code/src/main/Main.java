package main;
import infrastructure.Network;
import service_management.ServiceManagement;
import user_management.AuthenticatedUser;
import user_management.EndUser;
import user_management.Passenger;
import user_management.UserManagement;

import java.util.stream.Stream;

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
    System.out.println("Welcome to DispatchNet!");

    // Load objects from CSV files
    csvDatabase.loadNetwork(network);
    csvDatabase.loadAuthenticatedUsers(userManagement); // also loads tickets

    // Start the interactive interface flow
    runInterface();

    // Save objects to CSV files before exiting
    csvDatabase.saveNetwork(network);
    csvDatabase.saveAuthenticatedUsers(userManagement.getAuthenticatedUsers());
    try {
      csvDatabase.saveTickets(
        Main.getUserManagement().getAuthenticatedUsers().stream()
          .flatMap(user -> {
            if(user instanceof Passenger) {
              return ((Passenger)user).getTickets().stream();
            } else {
              return Stream.empty();
            }
          }).toList()
      );
    } catch (Exception e) {
      System.err.println("Failed to save tickets: " + e.getMessage());
    }

    System.out.println("Thank you for using DispatchNet. Goodbye!");
  }

  private static void runInterface() {
    boolean exitRequested = false;

    while (!exitRequested) {
      if (!userManagement.isAuthenticated()) {
        exitRequested = showAnonymousMenu();
      } else {
        exitRequested = showAuthenticatedMenu();
      }
    }
  }

  private static boolean showAnonymousMenu() {
    System.out.println("\n--- DispatchNet Guest Interface ---");
    System.out.println("1) Register");
    System.out.println("2) Login");
    System.out.println("3) Reset password");
    System.out.println("4) Exit");

    String choice = io.prompt("Enter your choice:").trim();
    switch (choice) {
      case "1" -> userManagement.getSession().getAnonymousUser().register();
      case "2" -> userManagement.getSession().getAnonymousUser().login();
      case "3" -> userManagement.getSession().getAnonymousUser().resetPassword();
      case "4" -> {
        return true;
      }
      default -> System.out.println("Invalid choice. Please enter 1, 2, 3 or 4.");
    }

    return false;
  }

  private static boolean showAuthenticatedMenu() {
    AuthenticatedUser currentUser = userManagement.getAuthenticatedUser();
    System.out.println("\n--- DispatchNet User Interface ---");
    System.out.println("Logged in as: " + currentUser.getUsername() + " (" + currentUser.getUserType() + ")");
    System.out.println("1) View profile");
    System.out.println("2) Change profile");
    System.out.println("3) Logout");
    System.out.println("4) Exit");
    if (currentUser instanceof Passenger) {
      System.out.println("5) View ticket history");
    }

    String choice = io.prompt("Enter your choice:").trim();
    switch (choice) {
      case "1" -> {
        if (currentUser instanceof EndUser endUser) {
          endUser.viewData();
        } else {
          System.out.println("Profile viewing is not available for this user type.");
        }
      }
      case "2" -> {
        if (currentUser instanceof EndUser endUser) {
          endUser.changeData();
        } else {
          System.out.println("Profile editing is not available for this user type.");
        }
      }
      case "3" -> {
        userManagement.logoutUser();
        System.out.println("You have been logged out.");
      }
      case "4" -> {
        return true;
      }
      case "5" -> {
        if (currentUser instanceof Passenger passenger) {
          passenger.getTicketsHistory();
        } else {
          System.out.println("Invalid choice. Please enter a valid option.");
        }
      }
      default -> System.out.println("Invalid choice. Please enter a valid option.");
    }

    return false;
  }
}
