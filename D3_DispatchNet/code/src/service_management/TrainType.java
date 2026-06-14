package service_management;

/**
 * @class TrainType
 * @brief A Class defining a TrainType 
 */
public class TrainType {
  String identifier;
  int seatedCapacity;
  int standingCapacity;
  boolean isPassenger;

  /**
   * @brief constructs for a TrainType
   * @param identifier this TraniType's unique identifier
   * @param seatedCapacity this TrainType's number of seats
   * @param standingCapacity this TrainType's number of standing places
   * @param isPassenger whther or not this TrainType can carry passengers
   */
  public TrainType(String identifier, int seatedCapacity, int standingCapacity, boolean isPassenger) {
    this.identifier = identifier;
    this.seatedCapacity = seatedCapacity;
    this.standingCapacity = standingCapacity;
    this.isPassenger = isPassenger;
  }

  /**
   * @brief Get the total seated capacity for this train type
   * @return seated capacity
   */
  public int getSeatedCapacity() {
    return this.seatedCapacity;
  }
  
  /**
  * @brief Get the total standing capacity for this train type
  * @return The train's standing capacity
  */
  public int getStandingCapacity() {
    return this.standingCapacity;
  }
  
  /**
  * @brief Get the total overall capacity for this train type
  * @return total capacity
  */
  public int getTotalCapacity() {
    return this.getSeatedCapacity() + this.getStandingCapacity();
  }

  /**
  * @brief Get this train's identifier string
  * @return The train's identifier
  */
  public String getIdentifier() {
    return this.identifier;
  }

  /**
   * @brief Get whether this train can carry passengers or not
   * @return true if the train can carry passengers, otherwise false
   */
  public boolean isPassenger() {
    return this.isPassenger;
  }
}
