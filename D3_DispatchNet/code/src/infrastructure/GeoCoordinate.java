package infrastructure;

/**
 * @class GeoCoordinate
 * @brief A simple Class representing a GPS coordinate.
 */
public class GeoCoordinate {
  public float latitude, longitude;

  public GeoCoordinate() {
  }

  public GeoCoordinate(float latitude, float longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }
}
