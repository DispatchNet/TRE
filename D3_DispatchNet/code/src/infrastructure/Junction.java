package infrastructure;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import service_mangement.ServiceSet;

/**
 * @brief A Class representing a junction's data.
 */
public class Junction {
  GeoCoordinate location;
  Optional<StationData> stationData;
  String name;
  List<Line> connectedLines;
  List<ServiceSet> services;
  
  /**
   * Create a Junction
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
    this.location = location;
    this.stationData = stationData;
    this.name = name;
    this.connectedLines = new ArrayList<>();
    this.services = new ArrayList<>();
  }
  
  /**
   * Get this Junction's Location
   * @return this junction's location
   */
  public GeoCoordinate getLocation() {
    return location;
  }

  /**
   * Set this Junction's Location
   */
  public void setLocation(GeoCoordinate location) {
    this.location = location;
  }

  /**
   * Get this Junction's StationData
   * @return this junction's station data
   * @see StationData
   */
  public Optional<StationData> getStationData() {
    return stationData;
  }

  /**
   * Set this Junction's station data
   */
  public void setStationData(Optional<StationData> stationData) {
    this.stationData = stationData;
  }

  /**
   * Check if this Junction is a Station
   * @return {@code true} if it's a station, {@code false} otherwise
   */
  public boolean isStation() {
    return this.stationData.isPresent();
  }

  /**
   *  Gets this Junction's name
   */
  public String getName() {
    return name;
  }
  
  /**
   *  Set this Junction's name
   * 
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   *  Get this junction's connected lines
   */
  public List<Line> getConnectedLines() {
    return connectedLines;
  }

  /**
   *  Get this junction's services
   */
  public List<ServiceSet> getServices() {
    return services;
  }
};
