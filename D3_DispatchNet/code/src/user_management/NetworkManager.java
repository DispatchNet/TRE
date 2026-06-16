package user_management;

import infrastructure.GeoCoordinate;
import infrastructure.Junction;
import infrastructure.Line;
import infrastructure.StationData;
import main.Main;
import service_management.ServiceManagement;
import infrastructure.Network;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @class NetworkManager 
 * @brief Class representing a network manager, a type of authenticated user who 
 * can manage the network and review service requests.
 */
public class NetworkManager extends AuthenticatedUser {
    // The network instance this manager operates on
    private final Network network = Main.getNetwork();
    private final ServiceManagement serviceManagement = Main.getServiceManagement();

    /**
     * @brief Constructor for NetworkManager class
     * @param username The username of the network manager
     * @param email The email of the network manager
     * @param password The password of the network manager
     */
    public NetworkManager(
        String username, String email, String password, UserManagement userManagement
    ) {
        super(username, email, password, UserType.NetworkManager, userManagement);
    }

    /**
     * @brief Constructor for NetworkManager class with explicit id
     */
    public NetworkManager(
        String id, String username, String email, String password, 
        UserManagement userManagement
    ) {
        super(
            id, username, email, password, UserType.NetworkManager, userManagement
        );
    }

    /**
     * @brief Views the transportation network
     * @usecase{UC16}
     */
    public void viewNetwork() {
        // Simple network view: print junctions and lines
        userManagement.print("Junctions:\n");
        for (var j : network.getJunctions().values()) {
            userManagement.print(" - " + j.getId() + " : " + j.getName() + "\n");
        }

        userManagement.print("Lines:\n");
        for (var l : network.getLines().values()) {
            userManagement.print(" - " + l.getId() + " : " + 
                l.getJunction1().getId() + " <-> " + l.getJunction2().getId() + "\n");
        }
    }

    /**
     * @brief Creates a new station in the transportation network
     * @usecase{UC20}
     */
    public void createStation() {
        // Insert station data
        Junction j = insertStationData();

        // add to network if creation successful
        if (j != null) {
            network.addJunction(j);
            userManagement.print("Station created: " + j.getId() + "\n");
        }
    }

    /**
     * @brief Creates a new junction in the transportation network
     * @usecase{UC21}
     */
    public void createJunction() {
        // Insert junction data
        Junction j = insertJunctionData();

        // add to network if creation successful
        if (j != null) {
            network.addJunction(j);
            userManagement.print("Junction created: " + j.getId() + "\n");
        }
    }

    /**
     * @brief Creates a new line in the transportation network
     * @usecase{UC22}
     */
    public void createLine() {
        // Insert line data
        Line l = insertLineData();

        // add to network if creation successful
        if (l != null) {
            network.addLine(l);
            userManagement.print("Line created: " + l.getId() + "\n");
        }
    }

    /**
     * @brief Edits an existing station in the transportation network
     * @usecase{UC23}
     * @param station The station to edit
     */
    public void editStation(String station) {
        // Edit a station (identified by junction id)
        var jct = network.getJunctions().get(station);

        // station not found (get returns null if id not found)
        if (jct == null) {
            userManagement.displayError("Station not found");
            return;
        }

        // check if junction is in use (services list non-empty)
        if (jct.getServiceSets().size() > 0) {
            userManagement.displayError(
                "Station is used by services and cannot be edited"
            );
            return;
        }

        // prompt for new data
        Junction newJ = insertStationData();

        // error during data insertion (e.g. invalid input or cancel)
        if (newJ == null)
            return;

        // apply editable fields to existing junction (preserve id and connected lines/services)
        jct.setName(newJ.getName());
        jct.setLocation(newJ.getLocation());
        jct.setStationData(newJ.getStationData());

        // print confirmation
        userManagement.print("Station edited: " + jct.getId() + "\n");
    }

    /**
     * @brief Edits an existing junction in the transportation network. Cannot become a station.
     * @usecase{UC24}
     * @param junction The junction to edit
     */
    public void editJunction(String junction) {
        // Edit a junction (identified by junction id)
        var jct = network.getJunctions().get(junction);

        // junction not found (get returns null if id not found)
        if (jct == null) {
            userManagement.displayError("Junction not found");
            return;
        }

        // check if junction is in use (services list non-empty)
        if (jct.getServiceSets().size() > 0) {
            userManagement.displayError(
                "Junction is used by services and cannot be edited"
            );
            return;
        }

        // prompt for new data
        Junction newJ = insertJunctionData();

        // error during data insertion (e.g. invalid input or cancel)
        if (newJ == null)
            return;

        // apply editable fields to existing junction (preserve id and connected lines/services)
        jct.setName(newJ.getName());
        jct.setLocation(newJ.getLocation());

        // print confirmation
        userManagement.print("Junction edited: " + jct.getId() + "\n");
    }

    /**
     * @brief Edits an existing line in the transportation network
     * @usecase{UC25}
     * @param line The line to edit
     */
    public void editLine(String line) {
        // Edit a line (identified by line id)
        var ln = network.getLines().get(line);

        // line not found (get returns null if id not found)
        if (ln == null) {
            userManagement.displayError("Line not found");
            return;
        }

        // check if line is in use (services list non-empty)
        if (ln.isUsed()) {
            userManagement.displayError(
                "Line is used by services and cannot be edited"
            );
            return;
        }

        // prompt for updated line data using the same insertion flow as creation
        Line newLine = insertLineData();
        if (newLine == null) {
            return;
        }

        // apply editable fields to existing line (preserve existing id)
        ln.setJunction1(newLine.getJunction1());
        ln.setJunction2(newLine.getJunction2());
        ln.setLengthMeters(newLine.getLengthMeters());
        ln.setMaxSpeedKpH(newLine.getMaxSpeedKpH());
        ln.setnTracks(newLine.getnTracks());

        // print confirmation
        userManagement.print("Line edited: " + ln.getId() + "\n");
    }

    /**
     * @brief Deletes an existing station in the transportation network
     * @usecase{UC26}
     * @param station The station to delete
     */
    public void deleteStation(String station) {
        // Delete a station (junction that has station data)
        var jct = network.getJunctions().get(station);

        // station not found (get returns null if id not found)
        if (jct == null) {
            userManagement.displayError("Station not found");
            return;
        }

        // check if junction is a station (has station data)
        if (!jct.isStation()) {
            userManagement.displayError(
                "Specified junction is not a station"
            );
            return;
        }

        // check if station is in use (services list non-empty)
        if (jct.getServiceSets().size() > 0) {
            userManagement.displayError(
                "Station is used by services and cannot be deleted"
            );
            return;
        }

        // attempt deletion and handle return values
        var res = network.removeJunction(station);

        if (res.isPresent()) {
            // if present, deletion failed: check reason
            if (res.get() == Network.EditError.IN_USE) { // in use
                userManagement.displayError(
                    "Station is used by services and cannot be deleted"
                );
            }
            else { // not found
                userManagement.displayError("Station not found");
            }
        }
        else { // success
            // print confirmation
            userManagement.print("Station deleted: " + station + "\n");
        }
    }

    /**
     * @brief Deletes an existing junction in the transportation network
     * @usecase{UC27}
     * @param junction The junction to delete
     */
    public void deleteJunction(String junction) {
        // Delete a junction (identified by junction id)
        var jct = network.getJunctions().get(junction);

        // junction not found (get returns null if id not found)
        if (jct == null) {
            userManagement.displayError("Junction not found");
            return;
        }

        // check if junction is in use (services list non-empty)
        if (jct.getServiceSets().size() > 0) {
            userManagement.displayError(
                "Junction is used by services and cannot be deleted"
            );
            return;
        }

        // attempt deletion and handle return values
        var res = network.removeJunction(junction);

        if (res.isPresent()) {
            // if present, deletion failed: check reason
            if (res.get() == Network.EditError.IN_USE) { // in use
                userManagement.displayError(
                    "Junction is used by services and cannot be deleted"
                );
            } 
            else { // not found
                userManagement.displayError("Junction not found");
            }
        } 
        else { // success
            // print confirmation
            userManagement.print("Junction deleted: " + junction+ "\n");
        }
    }

    /**
     * @brief Deletes an existing line in the transportation network
     * @usecase{UC28}
     * @param line The line to delete
     */
    public void deleteLine(String line) {
        // Delete a line (identified by line id)
        var ln = network.getLines().get(line);

        // line not found (get returns null if id not found)
        if (ln == null) {
            userManagement.displayError("Line not found");
            return;
        }

        // check if line is in use
        if (ln.isUsed()) {
            userManagement.displayError(
                "Line is used by services and cannot be deleted"
            );
            return;
        }

        // attempt deletion and handle return values
        var res = network.removeLine(line);

        if (res.isPresent()) {
            // if present, deletion failed: check reason
            if (res.get() == Network.EditError.IN_USE) { // in use
                userManagement.displayError(
                    "Line is used by services and cannot be deleted"
                );
            }
            else { // not found
                userManagement.displayError("Line not found");
            }
        } 
        else { // success
            // print confirmation
            userManagement.print("Line deleted: " + line + "\n");
        }
    }

    /*
     * @brief Prompt the user to insert station data and build a Junction with StationData
     * Implements the Insert Station Data flow: requests name, coordinates and platforms,
     * validates uniqueness and returns the created Junction or null on cancel/error.
     * @usecase{UC20}
     * @usecase{UC23}
     */
    private Junction insertStationData() {
        // Loop until valid input is provided 
        while (true) {
            // Prompt for station name
            String name = userManagement.prompt("Enter station name:");

            // check name uniqueness among junctions
            boolean nameTaken = network.getJunctions().values().stream()
                .anyMatch(j -> j.getName().equals(name));
            // if name is taken, show error and restart loop
            if (nameTaken) {
                userManagement.displayError(
                    "Name already in use by another junction"
                );
                continue;
            }

            // prompt for coordinates
            String latS = userManagement.prompt("Enter latitude (float):");
            String lonS = userManagement.prompt("Enter longitude (float):");
            
            // parse coordinates and handle invalid input
            float lat, lon;
            try {
                lat = Float.parseFloat(latS);
                lon = Float.parseFloat(lonS);
            } catch (NumberFormatException e) {
                userManagement.displayError("Invalid coordinates");
                continue;
            }

            // prompt for platform identifiers
            String platformsRaw = userManagement.prompt(
                "Enter platform identifiers separated by ';' (e.g. 1;1-East):"
            );

            // split and trim platform identifiers
            String[] tokens = platformsRaw.split(";");

            Set<String> platforms = new HashSet<>();

            for (String t : tokens) {
                String trimmed = t.trim();
                if (!trimmed.isEmpty()) 
                    platforms.add(trimmed);
            }

            // check uniqueness of platform identifiers (size of set should match number of tokens)
            if (platforms.size() != tokens.length) {
                userManagement.displayError(
                    "Platform identifiers must be locally unique"
                );
                continue;
            }

            // build and return Junction with StationData
            StationData sd = new StationData(platforms);
            Junction j = new Junction(
                new GeoCoordinate(lat, lon), java.util.Optional.of(sd), name,
                null, null
            );

            return j;
        }
    }

    /*
     * @brief Prompt the user to insert junction data and build a Junction. 
     * Not a station (no station data).
     * @usecase{UC21}
     * @usecase{UC24}
     */
    private Junction insertJunctionData() {
        // Loop until valid input is provided
        while (true) {
            // Prompt for junction name
            String name = userManagement.prompt("Enter junction name:");

            // check name uniqueness among junctions
            boolean nameTaken = network.getJunctions().values().stream()
                .anyMatch(j -> j.getName().equals(name));
            
            // if name is taken, show error and restart loop
            if (nameTaken) {
                userManagement.displayError(
                    "Name already in use by another junction"
                );
                continue;
            }

            // prompt for coordinates
            String latS = userManagement.prompt("Enter latitude (float):");
            String lonS = userManagement.prompt("Enter longitude (float):");
            // parse coordinates and handle invalid input
            float lat, lon;
            try {
                lat = Float.parseFloat(latS);
                lon = Float.parseFloat(lonS);
            } catch (NumberFormatException e) {
                userManagement.displayError(
                    "Invalid coordinates"
                );
                continue;
            }

            // build and return Junction without station data
            return new Junction(
                new GeoCoordinate(lat, lon), java.util.Optional.empty(), name, 
                null, null
            );
        }
    }

    /*
     * @brief Prompt the user to insert line data and build a Line
     * @usecase{UC22}
     * @usecase{UC25}
     */
    private Line insertLineData() {
        // check that at least 2 junctions exist to connect with a line
        if (network.getJunctions().size() < 2) {
            userManagement.displayError(
                "At least two junctions are required to create a line"
            );
            return null;
        }

        // print available junctions for user reference
        userManagement.print("Available junctions:\n");
        for (var j : network.getJunctions().values()) {
            userManagement.print(" - " + j.getId() + " : " + j.getName() + "\n");
        }

        // prompt for junction ids to connect
        String j1id = userManagement.prompt("Enter id of junction 1:");
        String j2id = userManagement.prompt("Enter id of junction 2:");

        // get junctions by id
        var j1 = network.getJunctions().get(j1id);
        var j2 = network.getJunctions().get(j2id);

        // validate junction ids and handle not found
        if (j1 == null || j2 == null) {
            userManagement.displayError(
                "One or both junction ids not found"
            );
            return null;
        }

        // prompt for line attributes and handle invalid input
        try {
            int length = Integer.parseInt(
                userManagement.prompt("Enter length in meters:")
            );
            int maxSpeed = Integer.parseInt(
                userManagement.prompt("Enter max speed (km/h):")
            );
            int nTracks = Integer.parseInt(
                userManagement.prompt("Enter number of tracks:")
            );

            // build and return Line
            return new Line(j1, j2, length, maxSpeed, nTracks);
        } catch (NumberFormatException e) {
            userManagement.displayError("Invalid numeric input");
            return null;
        }
    }

     /**
     * @brief Reviews a pending service request, displaying its details and
     *        prompting the network manager to approve or reject it.
     *        On approval the request is promoted to an active service set.
     *        On rejection it is discarded.
     *        Notifies the owning train company by email in either case.
     * @usecase{UC32}
     * @param serviceRequestID The ID of the service request to review
     */
    public void reviewServiceRequest(String serviceRequestID) {
        // look up the request
        var maybeRequest = Optional.ofNullable(serviceManagement.getServiceRequests().get(serviceRequestID));

        // abort if not found
        if (maybeRequest.isEmpty()) {
            userManagement.displayError("Service request not found: " + serviceRequestID);
            return;
        }

        var request = maybeRequest.get();

        // build a human-readable summary of the request
        StringBuilder details = new StringBuilder();
        details.append("=== Service Request: ").append(serviceRequestID).append(" ===\n");
        details.append("Company  : ").append(request.getCompany().getUsername()).append("\n");
        details.append("Type     : ").append(request.getType().getCommercialName()).append("\n");
        details.append("Status   : ").append(request.getStatus()).append("\n");
        details.append("Steps    :\n");

        // list each step with junction name, travel time and stop data if present
        for (var step : request.getSteps()) {
            details.append("  - Junction: ").append(step.getJunction().getName());
            details.append("  Travel: ").append(step.getTravelMinutes()).append(" min");
            if (step.isStopping()) {
                details.append("  [STOP]");
            }
            details.append("\n");
        }

        details.append("Dispatches: ");
        for (var dispatch : request.getDispatches()) {
            details.append(dispatch.getTrainNumber()).append(":").append(dispatch.getDispatchTime().toString()).append("\n");
        }
        
        // display the summary
        userManagement.print(details + "\n");

        // prompt for approve / reject decision, loop until valid input
        while (true) {
            String decision = userManagement.prompt(
                "Approve or reject this request? (approve/reject):"
            );

            if (decision.equalsIgnoreCase("approve")) {
                // promote the request to an active service set
                serviceManagement.approveServiceRequest(serviceRequestID);
                userManagement.print("Service request approved: " + serviceRequestID + "\n");

                // notify the train company by email
                userManagement.sendEmail(new mail_service.Email(
                    request.getCompany().getEmail(),
                    "Service Request Approved",
                    "Your service request " + serviceRequestID + " has been approved " +
                    "and is now active."
                ));
                return;

            } else if (decision.equalsIgnoreCase("reject")) {
                // discard the request
                serviceManagement.rejectServiceRequest(serviceRequestID);
                userManagement.print("Service request rejected: " + serviceRequestID + "\n");

                // notify the train company by email
                userManagement.sendEmail(new mail_service.Email(
                    request.getCompany().getEmail(),
                    "Service Request Rejected",
                    "Your service request " + serviceRequestID + " has been rejected."
                ));
                return;

            } else {
                userManagement.displayError("Invalid input. Please enter 'approve' or 'reject'.");
            }
        }
    }

    /**
     * @brief Creates a new account for a train company
     */
    public void createTrainCompanyAccount() {
        new AnonymousUser(userManagement).register(true);
    }

    /**
     * @brief Main method for testing NetworkManager behavior.
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        // Test creating a new train company account
        UserManagement userManagement = Main.getUserManagement();
        new AnonymousUser(userManagement).login();

        NetworkManager networkManager = 
            (NetworkManager) userManagement.
            getAuthenticatedUserByUsername("Admin");
        
        userManagement.print("Creating a new train company account...\n");
        networkManager.createTrainCompanyAccount();
    }
}
