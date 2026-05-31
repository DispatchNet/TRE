package infrastructure;

import java.util.Set;

/**
 * @class StationData
 * @brief A Class containing a station's data.
 * @see Junction
 */
public class StationData {
  Set<String> platforms;
  
  /**
   * @brief Constructs a StationsData object
   * @param platforms A set of platforms
   */
  StationData(Set<String> platforms) {
    this.platforms = platforms;
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
