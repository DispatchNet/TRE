package infrastructure;

import java.util.Set;
import java.util.UUID;

/**
 * @class StationData
 * @brief A Class containing a station's data.
 * @see Junction
 */
public class StationData {
  private final String id;
  private Set<String> platforms;
  
  /**
   * @brief Constructs a StationData object with a generated id
   * @param platforms A set of platforms
   */
  public StationData(Set<String> platforms) {
    this(UUID.randomUUID().toString(), platforms);
  }

  /**
   * @brief Constructs a StationData object with an explicit id
   * @param id The unique identifier of the station data
   * @param platforms A set of platforms
   */
  public StationData(String id, Set<String> platforms) {
    this.id = id == null ? UUID.randomUUID().toString() : id;
    this.platforms = platforms;
  }
  
  /**
   * @brief Gets the unique identifier of this station data
   * @return the identifier
   */
  public String getId() {
    return id;
  }
  
  /**
   * @brief Gets the platforms of this station
   * @return the platforms
   */
  public Set<String> getPlatforms() {
    return platforms;
  }

  /**
   * @brief Sets the platforms of this station
   * @param platforms the platforms
   */
  public void setPlatforms(Set<String> platforms) {
    this.platforms = platforms;
  }
};
