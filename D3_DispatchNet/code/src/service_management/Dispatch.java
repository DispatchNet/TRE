package service_management;

import java.util.Set;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * @class Dispatch
 * @brief A class representing an individual dispatch for a service along a ServiceSet
 * @see ServiceSet
 */
public class Dispatch {
  final int trainNumber;
  LocalTime dispatchTime;
  Set<DayOfWeek> runningDays;

  /**
   * @brief Constructor for Dispatch, applies to the first departure 
   * @param trainNumber The unique train number for this dispatch
   * @param dispatchTime The time for this dispatch
   * @param runningDays The for which this dispatch is valid
   */
  public Dispatch(int trainNumber, LocalTime dispatchTime, Set<DayOfWeek> runningDays) {
    this.trainNumber = trainNumber;
    this.dispatchTime = dispatchTime;
    this.runningDays = runningDays;
  }

  /**
   * @brief Get the train number
   * @return the train number
   */
  public int getTrainNumber() {
    return trainNumber;
  }

  /**
   * @brief Get the dispatch time
   * @return a local time
   */
  public LocalTime getDispatchTime() {
    return dispatchTime;
  }

  /**
   * @brief Set the dispatch time
   * @param dispatchTime a local time
   */
  public void setDispatchTime(LocalTime dispatchTime) {
    this.dispatchTime = dispatchTime;
  }

  /**
   * @brief Get the running days
   * @return the runnig days
   */
  public Set<DayOfWeek> getRunningDays() {
    return runningDays;
  }

  /**
   * @brief Set the runnig days
   * @param runningDays the runnig days
   */
  public void setRunningDays(Set<DayOfWeek> runningDays) {
    this.runningDays = runningDays;
  }
}
