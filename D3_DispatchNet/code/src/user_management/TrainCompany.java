package user_management;

import service_management.Dispatch;
import service_management.ServiceSet;
import service_management.ServiceStatus;
import service_management.ServiceStep;
import service_management.ServiceType;
import service_management.StopData;
import service_management.TrainType;
import infrastructure.Junction;
import main.Main;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import IO_operations.IO;
import csv_database.CsvDatabase;

/**
 * @class TrainCompany
 * @brief Class representing a train company, a type of end user who can
 * additionally create service requests and manage their account.
 */
public class TrainCompany extends EndUser {
    
    /**
     * @brief Constructor for TrainCompany class
     * @param username The username of the train company
     * @param email The email of the train company
     * @param password The password of the train company
     * @param userManagement The user management instance
     */
    public TrainCompany(
        String username, String email, String password, UserManagement userManagement
    ) {
        this(UUID.randomUUID().toString(),username,email,password,userManagement);
    }
    /**
     * @brief Constructor for TrainCompany class with explicit id
     */
    public TrainCompany(
        String id, String username, String email, String password, UserManagement userManagement
    ) {
        super(id, username, email, password, UserType.TrainCompany, userManagement);
    }
    

    /**
     * @brief Creates and submits a new service request.
     *        Prompts the user to select or create a ServiceType, then build the
     *        route step-by-step, and add one or more Dispatches before submitting.
     */
    public void createServiceRequest() {

        if(Main.getNetwork().getJunctions().size() < 2) {
            userManagement.displayError("There are fewer than 2 junctions in the network, it's not possible to create a serviceRequest at this time");
            return;
        }
        
        // --- select or create the service type ---
        ServiceType serviceType = selectOrCreateServiceType();
        if (serviceType == null) return;

        // --- build the list of steps ---
        List<ServiceStep> steps = new ArrayList<>();
        userManagement.print("Add route steps one by one. Enter 'done' when finished (minimum 2 steps required).\n");

        while (true) {
            // list available neighbhoring junctions for reference (or all the junctions if it's the first one)
            for(var junction : (steps.size() > 0) ? steps.getLast().getJunction().getNeighbours() : Main.getNetwork().getJunctions().values()) {
                    userManagement.print(String.format("%s: %s [%s]",
                    junction.getId(), junction.getName(),
                    junction.getStationData().map(station -> "Station").orElse("Junction") + "\n")
                );
                junction.getStationData().ifPresent(station -> {
                    String.join(",",station.getPlatforms());
                });
            }

            String junctionId = userManagement.prompt(
                "Enter junction ID for step " + (steps.size() + 1) + " (or 'done' to finish):"
            );

            if (junctionId.equalsIgnoreCase("done")) {
                if (steps.size() < 2) {
                    userManagement.displayError("A service must have at least 2 steps.");
                    continue;
                }
                break;
            }
            
            Junction junction = Main.getNetwork().getJunctions().get(junctionId);
            if (junction == null) {
                userManagement.displayError("Junction not found");
                continue;
            }



            // parse travel minutes to this junction
            int travelMinutes;
            try {
                travelMinutes = Integer.parseInt(
                    userManagement.prompt("Enter travel time to this junction (minutes):")
                );
            } catch (NumberFormatException e) {
                userManagement.displayError("Invalid travel time. Please enter a whole number.");
                continue;
            }

            // ask whether the train stops here
            String stopsHere = userManagement.prompt("Does the train stop here? (yes/no):");
            Optional<StopData> stopData = Optional.empty();

            if (stopsHere.equalsIgnoreCase("yes")) {
                String platform = userManagement.prompt("Enter platform identifier:");
                int waitMinutes;
                try {
                    waitMinutes = Integer.parseInt(
                        userManagement.prompt("Enter wait time at this stop (minutes):")
                    );
                } catch (NumberFormatException e) {
                    userManagement.displayError("Invalid wait time. Defaulting to 0.");
                    waitMinutes = 0;
                }
                stopData = Optional.of(new StopData(platform, waitMinutes));
            }

            steps.add(new ServiceStep(UUID.randomUUID().toString(), junction, stopData, travelMinutes));
            userManagement.print("Step added. Total steps so far: " + steps.size() + "\n");
        }

        // --- add dispatches ---
        List<Dispatch> dispatches = new ArrayList<>();

        while (true) {
            String input = userManagement.prompt(
                "Enter train number for dispatch " + (dispatches.size() + 1) + " (or 'done' to finish):"
            );

            if (input.equalsIgnoreCase("done")) {
                if (dispatches.isEmpty()) {
                    userManagement.displayError("At least one dispatch is required.");
                    continue;
                }
                break;
            }

            int trainNumber;
            try {
                trainNumber = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                userManagement.displayError("Invalid train number. Please enter a whole number.");
                continue;
            }

            // parse dispatch time in HH:MM format
            LocalTime dispatchTime;
            try {
                dispatchTime = LocalTime.parse(
                    userManagement.prompt("Enter dispatch time (HH:MM):")
                );
            } catch (DateTimeParseException e) {
                userManagement.displayError("Invalid time format. Use HH:MM (e.g. 08:30).");
                continue;
            }

            // collect running days
            Set<DayOfWeek> runningDays = EnumSet.noneOf(DayOfWeek.class);
            userManagement.print("Enter running days (MON, TUE, WED, THU, FRI, SAT, SUN), one per line. Enter 'done' when finished.\n");
            while (true) {
                String day = userManagement.prompt("Running day (or 'done'):").toUpperCase();
                if (day.equals("DONE")) break;
                try {
                    switch (day) {
                        case "MON" -> runningDays.add(DayOfWeek.MONDAY);
                        case "TUE" -> runningDays.add(DayOfWeek.TUESDAY);
                        case "WED" -> runningDays.add(DayOfWeek.WEDNESDAY);
                        case "THU" -> runningDays.add(DayOfWeek.THURSDAY);
                        case "FRI" -> runningDays.add(DayOfWeek.FRIDAY);
                        case "SAT" -> runningDays.add(DayOfWeek.SATURDAY);
                        case "SUN" -> runningDays.add(DayOfWeek.SUNDAY);
                    }
                } catch (IllegalArgumentException e) {
                    userManagement.displayError("Invalid day. Use MON, TUE, WED, THU, FRI, SAT or SUN.");
                }
            }

            if (runningDays.isEmpty()) {
                userManagement.displayError("A dispatch must run on at least one day.");
                continue;
            }

            dispatches.add(new Dispatch(trainNumber, dispatchTime, runningDays));
            userManagement.print("Dispatch added. Total dispatches so far: " + dispatches.size() + "\n");
        }

        // --- assemble and submit the service request ---
        ServiceSet serviceSet = new ServiceSet(
            this,
            serviceType,
            ServiceStatus.PendingApproval,
            steps,
            dispatches
        );

        Main.getServiceManagement().addServiceRequest(serviceSet);
        userManagement.print("Service request submitted with ID: " + serviceSet.getId() + "\n");
    }

    /**
     * @brief Creates and registers a new ServiceType.
     *        Prompts for a commercial name and cents-per-km, then delegates to
     *        selectOrCreateTrainType() for the associated TrainType.
     * @return The created ServiceType
     */
    public ServiceType createServiceType() {
        String name = userManagement.prompt("Enter commercial name for the new service type:");

        int centsPerKm;
        while (true) {
            try {
                centsPerKm = Integer.parseInt(
                    userManagement.prompt("Enter price in cents per km:")
                );
                break;
            } catch (NumberFormatException e) {
                userManagement.displayError("Invalid value. Please enter a whole number.");
            }
        }

        TrainType trainType = selectOrCreateTrainType();
        if (trainType == null) return null;

        ServiceType serviceType = new ServiceType(
            UUID.randomUUID().toString(), name, trainType, centsPerKm
        );

        Main.getServiceManagement().addServiceType(serviceType);
        userManagement.print("Service type created: " + serviceType.getId() + " (" + name + ")\n");
        return serviceType;
    }

    /**
     * @brief Creates and registers a new TrainType.
     *        Prompts for identifier, seated capacity, standing capacity,
     *        and whether the train carries passengers.
     * @return the created TrainType
     */
    public TrainType createTrainType() {
        String identifier = userManagement.prompt("Enter identifier for the new train type:");

        int seated;
        while (true) {
            try {
                seated = Integer.parseInt(
                    userManagement.prompt("Enter seated capacity:")
                );
                break;
            } catch (NumberFormatException e) {
                userManagement.displayError("Invalid value. Please enter a whole number.");
            }
        }

        int standing;
        while (true) {
            try {
                standing = Integer.parseInt(
                    userManagement.prompt("Enter standing capacity:")
                );
                break;
            } catch (NumberFormatException e) {
                userManagement.displayError("Invalid value. Please enter a whole number.");
            }
        }

        String passengerInput = userManagement.prompt("Can this train carry passengers? (yes/no):");
        boolean isPassenger = passengerInput.equalsIgnoreCase("yes");

        TrainType trainType = new TrainType(identifier,seated,standing,isPassenger);

        Main.getServiceManagement().addTrainType(trainType);
        userManagement.print("Train type created: " + identifier + "\n");
        return trainType;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * @brief Prompts the user to either select an existing ServiceType or create a new one.
     * @return the chosen or newly created ServiceType, or null if the user cancels
     */
    private ServiceType selectOrCreateServiceType() {
        var serviceTypes = Main.getServiceManagement().getServiceTypes();

        if (!serviceTypes.isEmpty()) {
            userManagement.print("Existing service types:\n");
            for (var entry : serviceTypes.entrySet()) {
                userManagement.print("  [" + entry.getKey() + "] "
                    + entry.getValue().getCommercialName()
                    + " — " + entry.getValue().getCentsPerKm() + " c/km\n");
            }

            String choice = userManagement.prompt(
                "Enter an existing service type ID to use it, or 'new' to create one:"
            );

            if (!choice.equalsIgnoreCase("new")) {
                var found = Main.getServiceManagement().getServiceType(choice);
                if (found.isPresent()) return found.get();
                userManagement.displayError("Service type ID not found. Creating a new one instead.");
            }
        } else {
            userManagement.print("No service types exist yet. Please create one.\n");
        }

        // fall through to creation
        return createServiceType();
    }
    /**
     * @brief Prompts the user to either select an existing TrainType or create a new one.
     * @return the chosen or newly created TrainType
     */
    private TrainType selectOrCreateTrainType() {
        var trainTypes = Main.getServiceManagement().getTrainTypes();

        if (!trainTypes.isEmpty()) {
            userManagement.print("Existing train types:\n");
            for (var entry : trainTypes.entrySet()) {
                userManagement.print("  [" + entry.getKey() + "] "
                    + entry.getValue().getIdentifier()
                    + " — seated: " + entry.getValue().getSeatedCapacity()
                    + ", standing: " + entry.getValue().getStandingCapacity() + "\n" );
            }

            String choice = userManagement.prompt(
                "Enter an existing train type identifier to use it, or 'new' to create one:"
            );

            if (!choice.equalsIgnoreCase("new")) {
                var found = Main.getServiceManagement().getTrainType(choice);
                if (found.isPresent()) return found.get();
                userManagement.displayError("Train type not found. Creating a new one instead.");
            }
        } else {
            userManagement.print("No train types exist yet. Please create one.\n");
        }

        // fall through to creation
        return createTrainType();
    }

    //Unit test
    public static void main(String args[]) {
        TrainCompany trainCompany = new TrainCompany("-", "-", "-", new UserManagement(new IO(), new CsvDatabase()));
        trainCompany.createServiceRequest();
    }
}
