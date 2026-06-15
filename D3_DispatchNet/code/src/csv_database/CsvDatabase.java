package csv_database;

import infrastructure.GeoCoordinate;
import infrastructure.Junction;
import infrastructure.Line;
import infrastructure.Network;
import infrastructure.StationData;
import service_management.Dispatch;
import service_management.ServiceManagement;
import service_management.ServiceSet;
import service_management.ServiceStatus;
import service_management.ServiceStep;
import service_management.ServiceType;
import service_management.StopData;
import service_management.TrainType;
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
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    private static final Path USER_FILE         = DATA_DIR.resolve("authenticated_users.csv");
    private static final Path JUNCTION_FILE     = DATA_DIR.resolve("junctions.csv");
    private static final Path STATION_FILE      = DATA_DIR.resolve("station_data.csv");
    private static final Path LINE_FILE         = DATA_DIR.resolve("lines.csv");
    private static final Path TICKET_FILE       = DATA_DIR.resolve("tickets.csv");
    private static final Path TRAIN_TYPE_FILE   = DATA_DIR.resolve("train_types.csv");
    private static final Path SERVICE_TYPE_FILE = DATA_DIR.resolve("service_types.csv");
    private static final Path SERVICE_SET_FILE  = DATA_DIR.resolve("service_sets.csv");
    private static final Path SERVICE_STEP_FILE = DATA_DIR.resolve("service_steps.csv");
    private static final Path DISPATCH_FILE     = DATA_DIR.resolve("dispatches.csv");

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
        createFileWithHeader(TRAIN_TYPE_FILE,   "identifier,seatedCapacity,standingCapacity,isPassenger");
        createFileWithHeader(SERVICE_TYPE_FILE, "id,commercialName,trainTypeIdentifier,centsPerKm");
        createFileWithHeader(SERVICE_SET_FILE,  "id,companyId,serviceTypeId,status,isRequest");
        createFileWithHeader(SERVICE_STEP_FILE, "id,serviceSetId,stepIndex,junctionId,travelMinutes,platform,waitMinutes");
        createFileWithHeader(DISPATCH_FILE,     "serviceSetId,trainNumber,dispatchTime,runningDays");
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
     * @brief Loads ticket records from the CSV file and assigns them to their owners.
     * @param userManagement The user management instance used to resolve ticket owners.
     * @throws IOException If the ticket CSV file cannot be read.
     */
    public void loadTickets(UserManagement userManagement) throws IOException {
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

            passenger.addLoadedTicket(new Ticket(id, passenger, description, status, history));
        }
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

    /**
     * @brief Saves all passenger tickets from UserManagement to the ticket CSV file.
     * @param userManagement The user management instance containing tickets to persist.
     */
    public void saveTickets(UserManagement userManagement) {
        List<Ticket> allTickets = new ArrayList<>();
        for (AuthenticatedUser user : userManagement.getAuthenticatedUsers()) {
            if (user instanceof Passenger passenger) {
                allTickets.addAll(passenger.getAllTickets());
            }
        }
        saveTickets(allTickets);
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
     * @brief Loads all service management data from CSV files.
     *        Must be called after loadNetwork() and loadAuthenticatedUsers() so that
     *        Junction and TrainCompany references can be resolved.
     * @param serviceManagement The ServiceManagement instance to populate.
     * @param userManagement    Used to resolve TrainCompany owners by ID.
     * @param network           Used to resolve Junction references in ServiceSteps.
     */
    public void loadServiceManagement(
            ServiceManagement serviceManagement,
            UserManagement userManagement,
            Network network) {
        try {
            // Load in dependency order: TrainTypes have no deps, ServiceTypes need
            // TrainTypes, ServiceSets need ServiceTypes and TrainCompany users.
            loadTrainTypes(serviceManagement);
            loadServiceTypes(serviceManagement);
            loadServiceSets(serviceManagement, userManagement, network);
        } catch (IOException e) {
            System.err.println("Failed to load service management data: " + e.getMessage());
        }
    }

    /**
     * @brief Saves all service management data to CSV files.
     * @param serviceManagement The ServiceManagement instance to persist.
     */
    public void saveServiceManagement(ServiceManagement serviceManagement) {
        try {
            saveTrainTypes(serviceManagement);
            saveServiceTypes(serviceManagement);
            saveServiceSets(serviceManagement);
        } catch (IOException e) {
            System.err.println("Failed to save service management data: " + e.getMessage());
        }
    }

    /**
     * @brief Loads train types from train_types.csv.
     * @param serviceManagement The destination for the loaded TrainType objects.
     * @throws IOException If the CSV file cannot be read.
     */
    private void loadTrainTypes(ServiceManagement serviceManagement) throws IOException {
        for (String line : readCsvRecords(TRAIN_TYPE_FILE)) {
            String[] v = line.split(",", -1);
            if (v.length < 4) continue;

            String  identifier       = v[0];
            int     seatedCapacity   = Integer.parseInt(v[1]);
            int     standingCapacity = Integer.parseInt(v[2]);
            boolean isPassenger      = Boolean.parseBoolean(v[3]);

            serviceManagement.addTrainType(
                new TrainType(identifier, seatedCapacity, standingCapacity, isPassenger));
        }
    }

    /**
     * @brief Saves all registered train types to train_types.csv.
     * @param serviceManagement Source of the TrainType objects to persist.
     * @throws IOException If the CSV file cannot be written.
     */
    private void saveTrainTypes(ServiceManagement serviceManagement) throws IOException {
        List<String> output = new ArrayList<>();
        output.add("identifier,seatedCapacity,standingCapacity,isPassenger");

        for (TrainType tt : serviceManagement.getTrainTypes().values()) {
            output.add(String.join(",",
                escapeCsv(tt.getIdentifier()),
                Integer.toString(tt.getSeatedCapacity()),
                Integer.toString(tt.getStandingCapacity()),
                Boolean.toString(tt.isPassenger())
            ));
        }

        Files.write(TRAIN_TYPE_FILE, output, StandardCharsets.UTF_8,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * @brief Loads service types from service_types.csv.
     *        Depends on train types already being present in serviceManagement.
     * @param serviceManagement Source of TrainType lookups; destination for ServiceType objects.
     * @throws IOException If the CSV file cannot be read.
     */
    private void loadServiceTypes(ServiceManagement serviceManagement) throws IOException {
        for (String line : readCsvRecords(SERVICE_TYPE_FILE)) {
            String[] v = line.split(",", -1);
            if (v.length < 4) continue;

            String id             = v[0];
            String commercialName = v[1];
            String trainTypeId    = v[2];
            int    centsPerKm     = Integer.parseInt(v[3]);

            // Resolve the TrainType reference; skip if not found to keep data consistent.
            Optional<TrainType> trainType = serviceManagement.getTrainType(trainTypeId);
            if (trainType.isEmpty()) continue;

            serviceManagement.addServiceType(
                new ServiceType(id, commercialName, trainType.get(), centsPerKm));
        }
    }

    /**
     * @brief Saves all registered service types to service_types.csv.
     * @param serviceManagement Source of the ServiceType objects to persist.
     * @throws IOException If the CSV file cannot be written.
     */
    private void saveServiceTypes(ServiceManagement serviceManagement) throws IOException {
        List<String> output = new ArrayList<>();
        output.add("id,commercialName,trainTypeIdentifier,centsPerKm");

        for (ServiceType st : serviceManagement.getServiceTypes().values()) {
            output.add(String.join(",",
                escapeCsv(st.getId()),
                escapeCsv(st.getCommercialName()),
                escapeCsv(st.getTrainType().getIdentifier()),
                Integer.toString(st.getCentsPerKm())
            ));
        }

        Files.write(SERVICE_TYPE_FILE, output, StandardCharsets.UTF_8,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * @brief Holds the raw CSV data for a single ServiceSet row, pending construction
     *        once all its steps and dispatches have been collected.
     */
    private static class PendingServiceSet {
        final String        id;
        final TrainCompany  company;
        final ServiceType   serviceType;
        final ServiceStatus status;
        final boolean       isRequest;
        final List<ServiceStep> steps     = new ArrayList<>();
        final List<Dispatch>    dispatches = new ArrayList<>();

        PendingServiceSet(String id, TrainCompany company, ServiceType serviceType,
                          ServiceStatus status, boolean isRequest) {
            this.id          = id;
            this.company     = company;
            this.serviceType = serviceType;
            this.status      = status;
            this.isRequest   = isRequest;
        }
    }

    /**
     * @brief Loads service sets (both active and pending requests) together with
     *        their steps and dispatches from the three related CSV files.
     *        All three files are read in full before any ServiceSet is constructed,
     *        so each object is built exactly once with its complete step and dispatch
     *        lists — no package-private field access required.
     * @param serviceManagement Destination for loaded ServiceSet objects.
     * @param userManagement    Used to resolve TrainCompany owners by ID.
     * @param network           Used to resolve Junction references by ID in steps.
     * @throws IOException If any CSV file cannot be read.
     */
    private void loadServiceSets(
            ServiceManagement serviceManagement,
            UserManagement userManagement,
            Network network) throws IOException {

        // ── Pass 1: read service_sets.csv into pending holders ────────────────
        // PendingServiceSet accumulates steps and dispatches before the real
        // ServiceSet is constructed, avoiding any need to mutate its fields.
        Map<String, PendingServiceSet> pending = new HashMap<>();

        for (String line : readCsvRecords(SERVICE_SET_FILE)) {
            String[] v = line.split(",", -1);
            if (v.length < 5) continue;

            String id            = v[0];
            String companyId     = v[1];
            String serviceTypeId = v[2];
            ServiceStatus status;
            try {
                status = ServiceStatus.valueOf(v[3]);
            } catch (IllegalArgumentException e) {
                continue; // skip rows with an unrecognised status
            }
            boolean isRequest = Boolean.parseBoolean(v[4]);

            // Resolve owner – must be a TrainCompany.
            AuthenticatedUser owner = userManagement.getAuthenticatedUserById(companyId);
            if (!(owner instanceof TrainCompany company)) continue;

            // Resolve service type.
            Optional<ServiceType> serviceType = serviceManagement.getServiceType(serviceTypeId);
            if (serviceType.isEmpty()) continue;

            pending.put(id, new PendingServiceSet(id, company, serviceType.get(), status, isRequest));
        }

        // ── Pass 2: read service_steps.csv into the matching pending holder ───
        // Rows arrive in CSV order; stepIndex in the file preserves the original ordering.
        for (String line : readCsvRecords(SERVICE_STEP_FILE)) {
            String[] v = line.split(",", -1);
            if (v.length < 7) continue;

            String stepId        = v[0];
            String serviceSetId  = v[1];
            // v[2] is stepIndex – present in the file for human readability only
            String junctionId    = v[3];
            int    travelMinutes = Integer.parseInt(v[4]);
            String platform      = v[5]; // blank -> not stopping
            String waitStr       = v[6]; // blank -> not stopping

            PendingServiceSet holder = pending.get(serviceSetId);
            if (holder == null) continue;

            Junction junction = network.getJunctions().get(junctionId);
            if (junction == null) continue;

            Optional<StopData> stopData = Optional.empty();
            if (!platform.isBlank() && !waitStr.isBlank()) {
                stopData = Optional.of(new StopData(platform, Integer.parseInt(waitStr)));
            }

            holder.steps.add(new ServiceStep(stepId, junction, stopData, travelMinutes));
        }

        // ── Pass 3: read dispatches.csv into the matching pending holder ──────
        for (String line : readCsvRecords(DISPATCH_FILE)) {
            String[] v = line.split(",", -1);
            if (v.length < 4) continue;

            String         serviceSetId = v[0];
            int            trainNumber  = Integer.parseInt(v[1]);
            LocalTime      dispatchTime = LocalTime.parse(v[2]);
            Set<DayOfWeek> runningDays  = deserializeDaysOfWeek(v[3]);

            PendingServiceSet holder = pending.get(serviceSetId);
            if (holder == null) continue;

            holder.dispatches.add(new Dispatch(trainNumber, dispatchTime, runningDays));
        }

        // ── Pass 4: construct each ServiceSet once with its complete children ─
        // Only at this point do we call the ServiceSet constructor, so the
        // immutable lists it wraps already contain all steps and dispatches.
        for (PendingServiceSet holder : pending.values()) {
            ServiceSet serviceSet = new ServiceSet(
                holder.id, holder.company, holder.serviceType, holder.status,
                holder.steps, holder.dispatches);

            // Register via ServiceManagement's own methods to keep its internal
            // status bookkeeping consistent.
            if (holder.isRequest) {
                serviceManagement.addServiceRequest(serviceSet);
            } else {
                serviceManagement.addService(serviceSet);
            }
        }
    }

    /**
     * @brief Saves active service sets, pending service requests, their steps,
     *        and their dispatches to three CSV files in a single pass.
     * @param serviceManagement Source of the data to persist.
     * @throws IOException If any CSV file cannot be written.
     */
    private void saveServiceSets(ServiceManagement serviceManagement) throws IOException {
        List<String> setOutput  = new ArrayList<>();
        List<String> stepOutput = new ArrayList<>();
        List<String> dispOutput = new ArrayList<>();

        setOutput.add("id,companyId,serviceTypeId,status,isRequest");
        stepOutput.add("id,serviceSetId,stepIndex,junctionId,travelMinutes,platform,waitMinutes");
        dispOutput.add("serviceSetId,trainNumber,dispatchTime,runningDays");

        // Active service sets (isRequest = false) …
        for (ServiceSet set : serviceManagement.getServiceSets().values()) {
            appendServiceSetRows(set, false, setOutput, stepOutput, dispOutput);
        }
        // … and pending requests (isRequest = true).
        for (ServiceSet set : serviceManagement.getServiceRequests().values()) {
            appendServiceSetRows(set, true, setOutput, stepOutput, dispOutput);
        }

        Files.write(SERVICE_SET_FILE,  setOutput,  StandardCharsets.UTF_8,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(SERVICE_STEP_FILE, stepOutput, StandardCharsets.UTF_8,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(DISPATCH_FILE,     dispOutput, StandardCharsets.UTF_8,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * @brief Appends one ServiceSet's rows to the three in-memory accumulator lists.
     * @param set       The ServiceSet to serialise.
     * @param isRequest True when the set lives in serviceRequests, false for serviceSets.
     * @param setOut    Accumulator for service_sets.csv rows.
     * @param stepOut   Accumulator for service_steps.csv rows.
     * @param dispOut   Accumulator for dispatches.csv rows.
     */
    private void appendServiceSetRows(
            ServiceSet set,
            boolean isRequest,
            List<String> setOut,
            List<String> stepOut,
            List<String> dispOut) {

        // service_sets.csv row
        setOut.add(String.join(",",
            escapeCsv(set.getId()),
            escapeCsv(set.getCompany().getId()),
            escapeCsv(set.getType().getId()),
            escapeCsv(set.getStatus().name()),
            Boolean.toString(isRequest)
        ));

        // service_steps.csv – one row per step; stepIndex records list position
        List<ServiceStep> steps = set.getSteps();
        for (int i = 0; i < steps.size(); i++) {
            ServiceStep step = steps.get(i);
            String platform    = "";
            String waitMinutes = "";
            if (step.isStopping()) {
                StopData sd = step.getStopData().get();
                platform    = sd.getPlatform();
                waitMinutes = Integer.toString(sd.getWaitMinutes());
            }
            stepOut.add(String.join(",",
                escapeCsv(step.getId()),
                escapeCsv(set.getId()),
                Integer.toString(i),
                escapeCsv(step.getJunction().getId()),
                Integer.toString(step.getTravelMinutes()),
                escapeCsv(platform),
                escapeCsv(waitMinutes)
            ));
        }

        // dispatches.csv – one row per dispatch
        for (Dispatch d : set.getDispatches()) {
            dispOut.add(String.join(",",
                escapeCsv(set.getId()),
                Integer.toString(d.getTrainNumber()),
                escapeCsv(d.getDispatchTime().toString()),
                escapeCsv(serializeDaysOfWeek(d.getRunningDays()))
            ));
        }
    }

    /**
     * @brief Serialises a set of DayOfWeek values to a semicolon-separated string.
     * @param days The set of running days to serialise.
     * @return A semicolon-separated string of day names, e.g. "MONDAY;WEDNESDAY".
     */
    private String serializeDaysOfWeek(Set<DayOfWeek> days) {
        if (days == null || days.isEmpty()) return "";
        return days.stream()
                   .map(DayOfWeek::name)
                   .collect(Collectors.joining(";"));
    }

    /**
     * @brief Deserialises a semicolon-separated string back to a set of DayOfWeek values.
     * @param serialized The serialised string from the CSV field.
     * @return The corresponding set of DayOfWeek values (empty set on blank input).
     */
    private Set<DayOfWeek> deserializeDaysOfWeek(String serialized) {
        Set<DayOfWeek> result = new HashSet<>();
        if (serialized == null || serialized.isBlank()) return result;
        for (String token : serialized.split(";")) {
            if (!token.isBlank()) {
                try {
                    result.add(DayOfWeek.valueOf(token.trim()));
                } catch (IllegalArgumentException ignored) {
                    // skip unrecognised tokens
                }
            }
        }
        return result;
    }
}