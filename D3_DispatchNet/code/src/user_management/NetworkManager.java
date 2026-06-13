package user_management;

import infrastructure.GeoCoordinate;
import infrastructure.Junction;
import infrastructure.Line;
import infrastructure.StationData;
import infrastructure.Network;

import java.util.HashSet;
import java.util.Set;

/**
 * @class NetworkManager 
 * @brief Class representing a network manager, a type of authenticated user who 
 * can manage the network and review service requests.
 */
public class NetworkManager extends AuthenticatedUser {
    // The network instance this manager operates on
    private final Network network = new Network(); // TODO: move to container class
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
     */
    public void viewNetwork() {
        // Simple network view: print junctions and lines
        System.out.println("Junctions:");
        for (var j : network.getJunctions().values()) {
            System.out.println(" - " + j.getId() + " : " + j.getName());
        }

        System.out.println("Lines:");
        for (var l : network.getLines().values()) {
            System.out.println(" - " + l.getId() + " : " + 
                l.getJunction1().getId() + " <-> " + l.getJunction2().getId());
        }
    }

    /**
     * @brief Creates a new station in the transportation network
     */
    public void createStation() {
        // Insert station data
        Junction j = insertStationData();

        // add to network if creation successful
        if (j != null) {
            network.addJunction(j);
            System.out.println("Station created: " + j.getId());
        }
    }

    /**
     * @brief Creates a new junction in the transportation network
     */
    public void createJunction() {
        // Insert junction data
        Junction j = insertJunctionData();

        // add to network if creation successful
        if (j != null) {
            network.addJunction(j);
            System.out.println("Junction created: " + j.getId());
        }
    }

    /**
     * @brief Creates a new line in the transportation network
     */
    public void createLine() {
        // Insert line data
        Line l = insertLineData();

        // add to network if creation successful
        if (l != null) {
            network.addLine(l);
            System.out.println("Line created: " + l.getId());
        }
    }

    /**
     * @brief Edits an existing station in the transportation network
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
        if (jct.getServices().size() > 0) {
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
        System.out.println("Station edited: " + jct.getId());
    }

    /**
     * @brief Edits an existing junction in the transportation network. Cannot become a station.
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
        if (jct.getServices().size() > 0) {
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
        System.out.println("Junction edited: " + jct.getId());
    }

    /**
     * @brief Edits an existing line in the transportation network
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
        System.out.println("Line edited: " + ln.getId());
    }

    /**
     * @brief Deletes an existing station in the transportation network
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
        if (jct.getServices().size() > 0) {
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
            System.out.println("Station deleted: " + station);
        }
    }

    /**
     * @brief Deletes an existing junction in the transportation network
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
        if (jct.getServices().size() > 0) {
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
            System.out.println("Junction deleted: " + junction);
        }
    }

    /**
     * @brief Deletes an existing line in the transportation network
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
            System.out.println("Line deleted: " + line);
        }
    }

    /*
     * @brief Prompt the user to insert station data and build a Junction with StationData
     * Implements the Insert Station Data flow: requests name, coordinates and platforms,
     * validates uniqueness and returns the created Junction or null on cancel/error.
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
        System.out.println("Available junctions:");
        for (var j : network.getJunctions().values()) {
            System.out.println(" - " + j.getId() + " : " + j.getName());
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
     * @brief Reviews a service request
     * @param serviceRequest The service request to review
     */
    public void reviewServiceRequest(String serviceRequest) {
        // TODO: implement service request review
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
        UserManagement userManagement = new UserManagement();
        new AnonymousUser(userManagement).login();

        NetworkManager networkManager = 
            (NetworkManager) userManagement.
            getAuthenticatedUserByUsername("Admin");
        
        System.out.println("Creating a new train company account...");
        networkManager.createTrainCompanyAccount();
    }
}
