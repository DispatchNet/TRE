package csv_database;

import infrastructure.GeoCoordinate;
import infrastructure.Junction;
import infrastructure.Line;
import infrastructure.Network;
import infrastructure.StationData;
import ticketing.Ticket;
import user_management.AuthenticatedUser;
import user_management.Passenger;
import user_management.TrainCompany;
import user_management.NetworkManager;
import user_management.UserManagement;
import user_management.UserType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @class CsvDatabase
 * @brief CSV-based persistence layer for authenticated users and infrastructure data.
 * 
 * @details If a CSV file is to be added add everywhere there is "(add if necessary)"
 * as well as in the read/write methods.
 */
public class CsvDatabase {
    // File paths for CSV storage
    private static final Path DATA_DIR = Paths.get("csv_database", "data");
    // CSV files (add if necessary)
    private static final Path USER_FILE = DATA_DIR.resolve("authenticated_users.csv");
    private static final Path JUNCTION_FILE = DATA_DIR.resolve("junctions.csv");
    private static final Path STATION_FILE = DATA_DIR.resolve("station_data.csv");
    private static final Path LINE_FILE = DATA_DIR.resolve("lines.csv");
    private static final Path TICKET_FILE = DATA_DIR.resolve("tickets.csv");

    /**
     * @brief Constructor for CsvDatabase.
     */
    public CsvDatabase() {
        // Ensure data directory and CSV files exist before any read/write operations.
        try {
            initializeFiles(); // Creates the data directory and header rows if needed.
        } catch (IOException e) {
            System.err.println("Failed to initialize CSV database: " + e.getMessage());
        }
    }

    /**
     * @brief Initializes the CSV files with headers.
     * @throws IOException if an error occurs while creating the files.
     */
    private void initializeFiles() throws IOException {
        // Create data directory if it doesn't exist
        if (!Files.exists(DATA_DIR)) {
            Files.createDirectories(DATA_DIR);
        }

        // Create CSV files with headers if they don't exist (add if necessary)
        createFileWithHeader(USER_FILE, "id,username,email,password,userType");
        createFileWithHeader(JUNCTION_FILE, "id,name,latitude,longitude,stationDataId");
        createFileWithHeader(STATION_FILE, "id,junctionId,platforms");
        createFileWithHeader(LINE_FILE, "id,junction1Id,junction2Id,lengthMeters,maxSpeedKpH,nTracks");
        createFileWithHeader(TICKET_FILE, "id,ownerId,description,status,history");
    }

    /**
     * @brief Creates a CSV file with a header if it doesn't exist.
     * @param path The path to the CSV file.
     * @param header The header line for the CSV file.
     * @throws IOException if an error occurs while creating the file.
     */
    private void createFileWithHeader(Path path, String header) throws IOException {
        // Only create the file if it doesn't exist, to avoid overwriting existing data
        if (!Files.exists(path)) {
            Files.write(path, Collections.singletonList(header), 
                StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW
            );
        }
    }

    /**
     * @brief Loads authenticated users from the CSV file.
     * @param userManagement The user management instance.
     * @return A list of authenticated users.
     */
    public List<AuthenticatedUser> loadAuthenticatedUsers(UserManagement userManagement) {
        try {
            // Read all data rows from the users CSV and convert each line to an AuthenticatedUser.
            // USER_FILE is the path to the authenticated_users.csv file.
            return readCsvRecords(USER_FILE).stream()
                .map(line -> parseUser(line, userManagement)) // parse each CSV row with the current UserManagement context
                .filter(Optional::isPresent) // skip invalid or malformed records
                .map(Optional::get)
                .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Failed to load authenticated users: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * @brief Loads ticket records from the CSV file.
     * @param userManagement The user management instance used to resolve ticket owners.
     * @return A list of Ticket objects loaded from storage.
     * @throws IOException If the ticket CSV file cannot be read.
     */
    public List<Ticket> loadTickets(UserManagement userManagement) throws IOException {
        List<Ticket> result = new ArrayList<>();

        for (String line : readCsvRecords(TICKET_FILE)) {
            String[] values = line.split(",", -1);
            if (values.length < 5) {
                continue;
            }

            String id = values[0];
            String ownerId = values[1];
            String description = values[2];
            Ticket.TicketStatus status;
            try {
                status = Ticket.TicketStatus.valueOf(values[3]);
            } catch (IllegalArgumentException e) {
                status = Ticket.TicketStatus.INACTIVE;
            }

            List<String> history = deserializeList(values[4]);
            AuthenticatedUser owner = userManagement.getAuthenticatedUserById(ownerId);
            if (!(owner instanceof Passenger passenger)) {
                continue;
            }

            result.add(new Ticket(id, passenger, description, status, history));
        }

        return result;
    }

    /**
     * @brief Saves ticket objects to the ticket CSV file.
     * @param tickets The list of tickets to persist.
     */
    public void saveTickets(List<Ticket> tickets) {
        try {
            List<String> output = new ArrayList<>();
            output.add("id,ownerId,description,status,history");

            for (Ticket ticket : tickets) {
                output.add(String.join(",",
                    escapeCsv(ticket.getId()),
                    escapeCsv(ticket.getOwner() != null ? ticket.getOwner().getId() : ""),
                    escapeCsv(ticket.getDescription()),
                    escapeCsv(ticket.getStatus().name()),
                    escapeCsv(serializeList(ticket.getHistory()))
                ));
            }

            Files.write(TICKET_FILE, output, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to save tickets: " + e.getMessage());
        }
    }

    /**
     * @brief Saves authenticated users to the CSV file.
     * @param users The list of authenticated users to save.
     */
    public void saveAuthenticatedUsers(List<AuthenticatedUser> users) {
        try {
            // Build the CSV content in memory before writing it to disk.
            List<String> output = new ArrayList<>();

            // Add the header row to the CSV output.
            output.add("id,username,email,password,userType");

            // Convert each authenticated user into a CSV formatted string.
            for (AuthenticatedUser user : users) {
                output.add(createUserCsvLine(user));
            }

            // Write the CSV data to USER_FILE, creating it if necessary and replacing any existing file contents.
            Files.write(USER_FILE, output, StandardCharsets.UTF_8, 
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            System.err.println("Failed to save authenticated users: " + e.getMessage());
        }
    }

    /**
     * @brief Loads the network data from the CSV files.
     */
    public void loadNetwork(Network network) {
        try {
            // Load station data first. The returned list is converted to a map by ID so junctions can resolve station references.
            Map<String, StationData> stationDataById = loadStationData().stream()
                .collect(Collectors.toMap(StationData::getId, station -> station, (first, second) -> second));

            // Load junctions using the stationDataById mapping to attach optional station metadata.
            List<Junction> junctions = loadJunctions(stationDataById);
            
            // Convert junction list to a map by ID for line resolution.
            Map<String, Junction> junctionById = junctions.stream()
                .collect(Collectors.toMap(Junction::getId, junction -> junction, (first, second) -> second));

            // Add all junction objects to the network before loading lines.
            for (Junction junction : junctions) {
                network.addJunction(junction);
            }

            // Load lines after junctions to ensure each line can find its endpoints by ID.
            for (Line line : loadLines(junctionById)) {
                network.addLine(line);
            }
        } catch (IOException e) {
            System.err.println("Failed to load network data: " + e.getMessage());
        }
    }

    /**
     * @brief Saves the network data to the CSV files.
     * @param network The network to save.
     */
    public void saveNetwork(Network network) {
        try {
            // Save each network component in dependency order.
            saveStationData(network); // station data can be referenced by junctions
            saveJunctions(network);   // junctions can be referenced by lines
            saveLines(network);       // lines reference junction IDs
        } catch (IOException e) {
            System.err.println("Failed to save network data: " + e.getMessage());
        }
    }

    /**
     * @brief Reads CSV records from a file.
     * @param source The path to the CSV file.
     * @return A list of CSV records.
     * @throws IOException If an I/O error occurs.
     */
    private List<String> readCsvRecords(Path source) throws IOException {
        // If the file doesn't exist, there are no records to read.
        if (!Files.exists(source)) {
            return Collections.emptyList();
        }

        // Read all lines using UTF-8 encoding. The first line is the CSV header.
        List<String> lines = Files.readAllLines(source, StandardCharsets.UTF_8);

        // If the file is empty, return no records.
        if (lines.isEmpty()) {
            return Collections.emptyList();
        }

        // Skip the first line (header) and ignore blank lines in the CSV data.
        return lines.stream().skip(1).filter(line -> !line.isBlank()).collect(Collectors.toList());
    }

    /**
     * @brief Parses a CSV line into an AuthenticatedUser object.
     * @param line The CSV line to parse.
     * @param userManagement The user management instance.
     * @return The parsed AuthenticatedUser object, or an empty Optional if parsing fails.
     */
    private Optional<AuthenticatedUser> parseUser(String line, UserManagement userManagement) {
        // Split a CSV row into fields. The -1 parameter preserves trailing empty values.
        String[] values = line.split(",", -1);

        // Validate that the CSV row contains at least the expected user columns.
        if (values.length < 5) {
            return Optional.empty();
        }

        // Extract values by column index.
        String id = values[0];
        String username = values[1];
        String email = values[2];
        String password = values[3];
        UserType userType;
        try {
            // Convert the user type string into the corresponding enum value.
            userType = UserType.valueOf(values[4]);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }

        // Instantiate the correct subclass based on the parsed userType.
        switch (userType) {
            case Passenger:
                return Optional.of(new Passenger(id, username, email, password, userManagement));
            case TrainCompany:
                return Optional.of(new TrainCompany(id, username, email, password, userManagement));
            case NetworkManager:
                return Optional.of(new NetworkManager(id, username, email, password, userManagement));
            default:
                return Optional.empty();
        }
    }

    /**
     * @brief Creates a CSV line for an AuthenticatedUser object.
     * @param user The user for which to create a CSV line.
     * @return The CSV line as a string.
     */
    private String createUserCsvLine(AuthenticatedUser user) {
        return String.join(",",
            escapeCsv(user.getId()),
            escapeCsv(user.getUsername()),
            escapeCsv(user.getEmail()),
            escapeCsv(user.getPassword()),
            escapeCsv(user.getUserType().name())
        );
    }

    /**
     * @brief Saves the station data to a CSV file.
     * @param network The network containing the station data.
     * @throws IOException If an I/O error occurs.
     */
    private void saveStationData(Network network) throws IOException {
        // Build the CSV file content for station data.
        List<String> output = new ArrayList<>();

        // Header row defines the columns for station_data.csv.
        output.add("id,junctionId,platforms");

        // For each junction with station data, add a row containing station ID, junction ID, and platform list.
        for (Junction junction : network.getJunctions().values()) {
            junction.getStationData().ifPresent(stationData ->
                output.add(String.join(",",
                    escapeCsv(stationData.getId()),
                    escapeCsv(junction.getId()),
                    escapeCsv(serializePlatforms(stationData.getPlatforms()))
                ))
            );
        }

        // Write the station data CSV file using UTF-8 and overwrite any prior contents.
        Files.write(STATION_FILE, output, StandardCharsets.UTF_8, 
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    /**
     * @brief Saves the junction data to a CSV file.
     * @param network The network containing the junction data.
     * @throws IOException If an I/O error occurs.
     */
    private void saveJunctions(Network network) throws IOException {
        // Build the CSV file content for junctions.
        List<String> output = new ArrayList<>();
        
        // Header row defines the columns for junctions.csv.
        output.add("id,name,latitude,longitude,stationDataId");
        
        // Convert each junction in the network to a CSV line.
        for (Junction junction : network.getJunctions().values()) {
            String stationDataId = junction.getStationData().
                map(StationData::getId).orElse(""); // optional station data reference

            GeoCoordinate location = junction.getLocation();
            
            output.add(String.join(",",
                escapeCsv(junction.getId()),
                escapeCsv(junction.getName()),
                Float.toString(location.latitude),
                Float.toString(location.longitude),
                escapeCsv(stationDataId)
            ));
        }

        // Write the junction CSV file using UTF-8 and overwrite any prior contents.
        Files.write(JUNCTION_FILE, output, StandardCharsets.UTF_8, 
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    /**
     * @brief Saves the line data to a CSV file.
     * @param network The network containing the line data.
     * @throws IOException If an I/O error occurs.
     */
    private void saveLines(Network network) throws IOException {
        // Build the CSV file content for line data.
        List<String> output = new ArrayList<>();

        // Header row defines the columns for lines.csv.
        output.add("id,junction1Id,junction2Id,lengthMeters,maxSpeedKpH,nTracks");

        // Convert each Line object in the network into a CSV row.
        for (Line line : network.getLines().values()) {
            output.add(String.join(",",
                escapeCsv(line.getId()),
                escapeCsv(line.getJunction1().getId()),
                escapeCsv(line.getJunction2().getId()),
                Integer.toString(line.getLengthMeters()),
                Integer.toString(line.getMaxSpeedKpH()),
                Integer.toString(line.getnTracks())
            ));
        }

        // Write the lines CSV file using UTF-8 and overwrite any prior contents.
        Files.write(LINE_FILE, output, StandardCharsets.UTF_8, 
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    /**
     * @brief Loads the station data from a CSV file.
     * @return A list of station data.
     * @throws IOException If an I/O error occurs.
     */
    private List<StationData> loadStationData() throws IOException {
        // Load station_data.csv into StationData objects.
        List<StationData> result = new ArrayList<>();

        // Each non-header row is a station record with id, junctionId, and platforms.
        for (String line : readCsvRecords(STATION_FILE)) {
            String[] values = line.split(",", -1); // preserve empty trailing fields

            // Validate that we have at least the expected number of fields (3 in this case)
            if (values.length < 3) {
                continue; // skip malformed rows
            }

            // Extract values
            String id = values[0];
            Set<String> platforms = deserializePlatforms(values[2]);

            // Create a StationData object and add to result list
            result.add(new StationData(id, platforms));
        }

        return result;
    }

    /**
     * @brief Loads the junction data from a CSV file.
     * @param stationDataById A map of station data by ID.
     * @return A list of junctions.
     * @throws IOException If an I/O error occurs.
     */
    private List<Junction> loadJunctions(Map<String, StationData> stationDataById) throws IOException {
        // Load junctions.csv and attach optional station metadata using the provided stationDataById map.
        List<Junction> result = new ArrayList<>();

        // read CSV records, parse them into Junction objects, and add to result list
        for (String line : readCsvRecords(JUNCTION_FILE)) {
            String[] values = line.split(",", -1); // preserve empty fields

            // Validate that we have at least the expected number of fields (5 in this case)
            if (values.length < 5) {
                continue; // skip malformed rows
            }

            // Extract values
            String id = values[0];
            String name = values[1];
            float latitude = Float.parseFloat(values[2]);
            float longitude = Float.parseFloat(values[3]);
            String stationDataId = values[4];

            // Create a Junction object and add to result list
            GeoCoordinate location = new GeoCoordinate(latitude, longitude);
            
            // If stationDataId is blank, we treat it as Optional.empty(), otherwise we look it up in the map
            Optional<StationData> stationData = stationDataId.isBlank() ? Optional.empty() : Optional.ofNullable(stationDataById.get(stationDataId));
            
            // Create a Junction object and add to result list
            result.add(new Junction(id, location, stationData, name, new ArrayList<>(), new ArrayList<>()));
        }

        return result;
    }

    /**
     * @brief Loads the line data from a CSV file.
     * @param junctionsById A map of junctions by ID.
     * @return A list of lines.
     * @throws IOException If an I/O error occurs.
     */
    private List<Line> loadLines(Map<String, Junction> junctionsById) throws IOException {
        // Load lines.csv and resolve endpoint junctions by their IDs.
        List<Line> result = new ArrayList<>();

        // read CSV records, parse them into Line objects, and add to result list
        for (String line : readCsvRecords(LINE_FILE)) {
            String[] values = line.split(",", -1); // preserve empty fields

            // Validate that we have at least the expected number of fields (6 in this case)
            if (values.length < 6) {
                continue; // skip malformed rows
            }

            // Extract values
            String id = values[0];
            Junction junction1 = junctionsById.get(values[1]);
            Junction junction2 = junctionsById.get(values[2]);
            int lengthMeters = Integer.parseInt(values[3]);
            int maxSpeedKpH = Integer.parseInt(values[4]);
            int nTracks = Integer.parseInt(values[5]);

            if (junction1 == null || junction2 == null) {
                continue; // skip lines referencing unknown junction IDs
            }

            // Create a Line object and add to result list
            result.add(new Line(id, junction1, junction2, lengthMeters, maxSpeedKpH, nTracks));
        }

        return result;
    }

    /**
     * @brief Escapes a string for use in a CSV file.
     * @param input The input string.
     * @return The escaped string.
     */
    private String escapeCsv(String input) {
        // If the input is null, return an empty CSV field instead of the string "null".
        if (input == null) {
            return "";
        }

        // Escape any quotes inside the value by doubling them, per CSV quoting rules.
        String value = input.replace("\"", "\"\"");

        // Wrap the field in quotes if it contains any special CSV characters.
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            value = "\"" + value + "\"";
        }

        return value;
    }

    /**
     * @brief Deserializes a string of platform names into a set.
     * @param serialized The serialized string.
     * @return The set of platform names.
     */
    private Set<String> deserializePlatforms(String serialized) {
        // If the stored field is empty, return an empty set of platforms.
        if (serialized == null || serialized.isBlank()) {
            return Collections.emptySet();
        }

        // The platform list is stored as semicolon-separated values.
        String[] tokens = serialized.split(";");
        Set<String> set = new HashSet<>();
        
        for (String token : tokens) {
            if (!token.isBlank()) {
                set.add(token.trim());
            }
        }

        return set;
    }

    /**
     * @brief Serializes a set of platform names into a string.
     * @param platforms The set of platform names.
     * @return The serialized string.
     */
    private String serializePlatforms(Set<String> platforms) {
        // Store platform names as a semicolon-separated string for the CSV file.
        if (platforms == null || platforms.isEmpty()) {
            return "";
        }

        return String.join(";", platforms);
    }

    /**
     * @brief Serializes a list of strings into a single CSV-safe value.
     * @param values The list of items.
     * @return The serialized string.
     */
    private String serializeList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return String.join(";", values);
    }

    /**
     * @brief Deserializes a semicolon-separated list from a CSV field.
     * @param serialized The serialized list string.
     * @return The deserialized values.
     */
    private List<String> deserializeList(String serialized) {
        if (serialized == null || serialized.isBlank()) {
            return new ArrayList<>();
        }

        List<String> result = new ArrayList<>();
        for (String item : serialized.split(";")) {
            if (!item.isBlank()) {
                result.add(item.trim());
            }
        }
        return result;
    }
}
