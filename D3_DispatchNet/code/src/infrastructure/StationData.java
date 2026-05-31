package infrastructure;

import java.util.Set;

/**
 * A Class containing a station's data.
 * @see Junction
 */
public class StationData {
  Set<String> platforms;
  
  /**
   * Constructs a StationsData object
   * @param platforms A set of platforms
   */
  StationData(Set<String> platforms) {
    this.platforms = platforms;
  }

  public Set<String> getPlatforms() {
    return platforms;
  }

  public void setPlatforms(Set<String> platforms) {
    this.platforms = platforms;
  }
};