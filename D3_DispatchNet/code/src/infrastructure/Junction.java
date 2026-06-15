package infrastructure;

import java.util.Optional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import departure.Departure;
import service_management.ServiceSet;

/**
 * @class Junction
 * @brief A Class representing a junction's data.
 */
public class Junction {
  private final String id;
  GeoCoordinate location;
  Optional<StationData> stationData;
  String name;
  List<Line> connectedLines;
  List<ServiceSet> services;
  
  /**
   * @brief Create a Junction with generated id
   * @param location the location of this station as GPS coordinates
   * @param stationData the data for this station or {@code Optional.empty()}.
   * @param name the name of this station
   * @implNote Also initializes empty connectedLines and services lists to keep note on relations
   */
  public Junction(
    GeoCoordinate location, 
    Optional<StationData> stationData,
    String name,
    List<Line> connectedLines,
    List<ServiceSet> services
  ) {
    this(UUID.randomUUID().toString(), location, stationData, name, connectedLines, services);
  }

  /**
   * @brief Create a Junction with explicit id
   */
  public Junction(
    String id,
    GeoCoordinate location, 
    Optional<StationData> stationData,
    String name,
    List<Line> connectedLines,
    List<ServiceSet> services
  ) {
    this.id = id == null ? UUID.randomUUID().toString() : id;
    this.location = location;
    this.stationData = stationData;
    this.name = name;
    this.connectedLines = connectedLines == null ? new ArrayList<>() : new ArrayList<>(connectedLines);
    this.services = services == null ? new ArrayList<>() : new ArrayList<>(services);
  }
  
  /**
   * @brief Get the unique identifier of this Junction
   * @return this junction's id
   */
  public String getId() {
    return id;
  }

  /**
   * @brief Get this Junction's Location
   * @return this junction's location
   */
  public GeoCoordinate getLocation() {
    return location;
  }

  /**
   * @brief Set this Junction's Location
   */
  public void setLocation(GeoCoordinate location) {
    this.location = location;
  }

  /**
   * @brief Get this Junction's StationData
   * @return this junction's station data
   * @see StationData
   */
  public Optional<StationData> getStationData() {
    return stationData;
  }

  /**
   * @brief Set this Junction's station data
   */
  public void setStationData(Optional<StationData> stationData) {
    this.stationData = stationData;
  }

  /**
   * @brief Check if this Junction is a Station
   * @return {@code true} if it's a station, {@code false} otherwise
   */
  public boolean isStation() {
    return this.stationData.isPresent();
  }

  /**
   * @brief Gets this Junction's name
   * @return this junction's name
   */
  public String getName() {
    return name;
  }
  
  /**
   * @brief Set this Junction's name
   * @param name the new name for this junction
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @brief Get this junction's connected lines
   * @return this junction's connected lines
   */
  public List<Line> getConnectedLines() {
    return Collections.unmodifiableList(connectedLines);
  }

  /**
   * @brief Get this junction's services
   * @return this junction's services
   */
  public List<ServiceSet> getServices() {
    return Collections.unmodifiableList(services);
  }

  /**
   * @brief Get the departures from this junction
   * @implNote Includes services that don't stop at this junction
   * @return the departures
   */
  public List<Departure> getDepartures() {
    return getServices().stream().map(
      service -> {
        var steps = service.getSteps();
        var dispatches = service.getDispatches();
        var departures = new ArrayList<Departure>();
        int total_travel_time = 0;

        for (var step : steps) {
          total_travel_time += step.getTravelMinutes();
          total_travel_time += step.getStopData().map(val -> val.getWaitMinutes()).orElse(0);
          if(step.getJunction() == this) {
            for (var dispatch : dispatches) {
                departures.add(
                  new Departure(step,service,dispatch.getDispatchTime().plusMinutes(total_travel_time)
                )
              );
            }
          }
        }

        return departures;
      }
    ).flatMap(departures -> departures.stream()).toList();
  }

  /**
   * @brief Get the line from this junction to another, if it exists 
   * @param other The other junction the line should connect to 
   * @return a Line pointer, NULL if there is no such line
   */ 
  public Line getToOther (Junction other) {
    
    Line connection = null;
    for (int i = 0; i<this.connectedLines.size(); i++) {
      if (connectedLines.get(i).hasJunction(other)) connection = connectedLines.get(i);
    }

    return connection;

  }

  public List<Junction> getNeighbours() {
    return this.getConnectedLines().stream().map(
      line -> line.getJunction1() == this ? line.getJunction2() : line.getJunction1()
    ).toList();
  }

  static void main () {// Unit test

  }
};
