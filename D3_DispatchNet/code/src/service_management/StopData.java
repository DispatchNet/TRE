package service_management;

/**
 * @class StopData
 * @brief A Class defining the stop information for a ServiceStep
 * @see ServiceStep
 */
public class StopData {
  String platform;
  int waitMinutes;

  /**
   * @brief Constructor for StopData
   * @param platform The platform used in this stop
   * @param waitMinutes The wait minutes before the service continues
   */
  public StopData(String platform, int waitMinutes) {
    this.platform = platform;
    this.waitMinutes = waitMinutes;
  }

  /**
   * @brief Get the platform used at this stop
   * @return the platform identifer
   */
  public String getPlatform() {
    return platform;
  }
  
  /**
   * @brief Set the platform used in this stop
   * @param platform a platform identifier
   */
  public void setPlatform(String platform) {
    this.platform = platform;
  }
  
  /**
   * @brief Get the wait minutes at this stop
   * @return the wait time in minutes
   */
  public int getWaitMinutes() {
    return waitMinutes;
  }

  /**
   * @brief Set the wait minutes at this stop
   * @param waitMinutes the wait time in minutes
   */
  public void setWaitMinutes(int waitMinutes) {
    this.waitMinutes = waitMinutes;
  }

}
