package main;
import infrastructure.Network;
import service_management.ServiceManagement;
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

  public static void main(String[] args) {
    System.out.println("Welcome to DispatchNet!");

    // Load objects from CSV files
    csvDatabase.loadNetwork(network);
    csvDatabase.loadAuthenticatedUsers(userManagement); // also loads tickets (so must be done after network)

    

    // Save objects to CSV files
    csvDatabase.saveNetwork(network);
    csvDatabase.saveAuthenticatedUsers(userManagement.getAuthenticatedUsers());
    try {
        csvDatabase.saveTickets(userManagement);
    } catch (Exception e) {
        System.err.println("Failed to save tickets: " + e.getMessage());
    }
  }
}
