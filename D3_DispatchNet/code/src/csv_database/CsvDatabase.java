package csv_database;

import infrastructure.GeoCoordinate;
import infrastructure.Junction;
import infrastructure.Line;
import infrastructure.Network;
import infrastructure.StationData;
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

    /**
     * @brief Constructor for CsvDatabase.
     */
    public CsvDatabase() {
        // Ensure data directory and files exist
        try {
            initializeFiles();
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
            // Read CSV records, parse them into AuthenticatedUser objects, and return the list
            return readCsvRecords(USER_FILE).stream()
                .map(line -> parseUser(line, userManagement))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Failed to load authenticated users: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * @brief Saves authenticated users to the CSV file.
     * @param users The list of authenticated users to save.
     */
    public void saveAuthenticatedUsers(List<AuthenticatedUser> users) {
        try {
            // init output
            List<String> output = new ArrayList<>();

            // add header
            output.add("id,username,email,password,userType");

            // convert each user to a CSV line and add to output
            for (AuthenticatedUser user : users) {
                output.add(createUserCsvLine(user));
            }

            // write output to file, overwriting existing content
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
            // Load station data and junctions first to build the necessary mappings for lines
            Map<String, StationData> stationDataById = loadStationData().stream()
                .collect(Collectors.toMap(StationData::getId, station -> station, (first, second) -> second));

            List<Junction> junctions = loadJunctions(stationDataById);
            
            Map<String, Junction> junctionById = junctions.stream()
                .collect(Collectors.toMap(Junction::getId, junction -> junction, (first, second) -> second));

            // Load lines using the junction mappings
            for (Junction junction : junctions) {
                network.addJunction(junction);
            }

            // Load lines after junctions to ensure we can resolve junction references
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
            // save elements
            saveStationData(network);
            saveJunctions(network);
            saveLines(network);
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
        // If the file doesn't exist or is empty, return an empty list
        if (!Files.exists(source)) {
            return Collections.emptyList();
        }

        // Read all lines, skip the header, and filter out blank lines
        List<String> lines = Files.readAllLines(source, StandardCharsets.UTF_8);

        // If there are no lines or only the header, return an empty list
        if (lines.isEmpty()) {
            return Collections.emptyList();
        }

        return lines.stream().skip(1).filter(line -> !line.isBlank()).collect(Collectors.toList());
    }

    /**
     * @brief Parses a CSV line into an AuthenticatedUser object.
     * @param line The CSV line to parse.
     * @param userManagement The user management instance.
     * @return The parsed AuthenticatedUser object, or an empty Optional if parsing fails.
     */
    private Optional<AuthenticatedUser> parseUser(String line, UserManagement userManagement) {
        // Split the line into values, allowing for empty fields
        String[] values = line.split(",", -1);

        // Validate that we have at least the expected number of fields (5 in this case)
        if (values.length < 5) {
            return Optional.empty();
        }

        // Extract values
        String id = values[0];
        String username = values[1];
        String email = values[2];
        String password = values[3];
        // Parse user type, handling invalid values
        UserType userType;
        try {
            userType = UserType.valueOf(values[4]);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }

        // Create the appropriate AuthenticatedUser subclass based on user type
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
        // init output
        List<String> output = new ArrayList<>();

        // add header
        output.add("id,junctionId,platforms");

        // convert each station data to a CSV line and add to output
        for (Junction junction : network.getJunctions()) {
            junction.getStationData().ifPresent(stationData ->
                output.add(String.join(",",
                    escapeCsv(stationData.getId()),
                    escapeCsv(junction.getId()),
                    escapeCsv(serializePlatforms(stationData.getPlatforms()))
                ))
            );
        }

        // write output to file, overwriting existing content
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
        // init output
        List<String> output = new ArrayList<>();
        
        // add header
        output.add("id,name,latitude,longitude,stationDataId");
        
        // convert each junction to a CSV line and add to output
        for (Junction junction : network.getJunctions()) {
            String stationDataId = junction.getStationData().
                map(StationData::getId).orElse("");

            GeoCoordinate location = junction.getLocation();
            
            output.add(String.join(",",
                escapeCsv(junction.getId()),
                escapeCsv(junction.getName()),
                Float.toString(location.latitude),
                Float.toString(location.longitude),
                escapeCsv(stationDataId)
            ));
        }

        // write output to file, overwriting existing content
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
        // init output
        List<String> output = new ArrayList<>();

        // add header
        output.add("id,junction1Id,junction2Id,lengthMeters,maxSpeedKpH,nTracks");

        // convert each line to a CSV line and add to output
        for (Line line : network.getLines()) {
            output.add(String.join(",",
                escapeCsv(line.getId()),
                escapeCsv(line.getJunction1().getId()),
                escapeCsv(line.getJunction2().getId()),
                Integer.toString(line.getLengthMeters()),
                Integer.toString(line.getMaxSpeedKpH()),
                Integer.toString(line.getnTracks())
            ));
        }

        // write output to file, overwriting existing content
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
        // init result list
        List<StationData> result = new ArrayList<>();

        // read CSV records, parse them into StationData objects, and add to result list
        for (String line : readCsvRecords(STATION_FILE)) {
            // Split the line into values, allowing for empty fields
            String[] values = line.split(",", -1);

            // Validate that we have at least the expected number of fields (3 in this case)
            if (values.length < 3) {
                continue;
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
        // init result list
        List<Junction> result = new ArrayList<>();

        // read CSV records, parse them into Junction objects, and add to result list
        for (String line : readCsvRecords(JUNCTION_FILE)) {
            // Split the line into values, allowing for empty fields
            String[] values = line.split(",", -1);

            // Validate that we have at least the expected number of fields (5 in this case)
            if (values.length < 5) {
                continue;
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
        // init result list
        List<Line> result = new ArrayList<>();

        // read CSV records, parse them into Line objects, and add to result list
        for (String line : readCsvRecords(LINE_FILE)) {
            // Split the line into values, allowing for empty fields
            String[] values = line.split(",", -1);

            // Validate that we have at least the expected number of fields (6 in this case)
            if (values.length < 6) {
                continue;
            }

            // Extract values
            String id = values[0];
            Junction junction1 = junctionsById.get(values[1]);
            Junction junction2 = junctionsById.get(values[2]);
            int lengthMeters = Integer.parseInt(values[3]);
            int maxSpeedKpH = Integer.parseInt(values[4]);
            int nTracks = Integer.parseInt(values[5]);

            // If either junction reference is invalid, skip this line
            if (junction1 == null || junction2 == null) {
                continue;
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
        // If the input is null, we return an empty string to avoid writing "null" in the CSV
        if (input == null) {
            return "";
        }

        // Escape double quotes by replacing them with two double quotes
        String value = input.replace("\"", "\"\"");

        // If the value contains a comma, double quote, or newline, we need to wrap it in double quotes
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
        // If the input is null or blank, we return an empty set
        if (serialized == null || serialized.isBlank()) {
            return Collections.emptySet();
        }

        // Split the string by semicolons, trim whitespace, and collect into a set
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
        // If the input is null or empty, we return an empty string
        if (platforms == null || platforms.isEmpty()) {
            return "";
        }

        return String.join(";", platforms);
    }
}
