package service_management;

import java.util.Optional;

import infrastructure.Junction;

/**
 * @class ServiceStep
 * @brief A Class defining a ServiceStep
 * 
 */
public class ServiceStep{
  final String id;
  Junction junction;
  Optional<StopData> stopData;
  int travelMinutes;
  
  /**
   * @brief Construct a ServiceStep
   * @param id The ID of this serviceStep
   * @param junction the junction of this serviceStep
   * @param stopData the data of this serviceStep (Optional)
   * @param travelMinutes the minutes of travel to this destination
   * @see StopData
   */
  public ServiceStep(String id, Junction junction, Optional<StopData> stopData, int travelMinutes) {
    this.id = id;
    this.junction = junction;
    this.stopData = stopData;
    this.travelMinutes = travelMinutes;
  }

  /**
   * @brief Get this serviceStep's ID
   * @return
   */
  public String getId() {
    return id;
  }

  /**
   * @brief Get this serviceStep's Junction
   * @return junction
   */
  public Junction getJunction() {
    return junction;
  }

  /**
   * @brief Get this serviceStep's stopData 
   * @implNote Optional.empty() if not stopping
   * @return stopData
   */
  public Optional<StopData> getStopData() {
    return stopData;
  }

  /**
   * @brief Get the travel minutes to this destination.
   * @return the travel time in minutes
   */
  public int getTravelMinutes() {
    return travelMinutes;
  }

  /**
   * @brief Get whether the train is stopping at this destination.
   * @return true if the train is stopping, false otherwise
   */
  public boolean isStopping() {
    return stopData.isPresent();
  }
}
