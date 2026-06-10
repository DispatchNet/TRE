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
   * @brief Get the total seated capacity for this train type
   * @return seated capacity
   */
  int getSeatedCapacity() {
    return this.seatedCapacity;
  }
  
  /**
  * @brief Get the total standing capacity for this train type
  * @return The train's standing capacity
  */
  int getStandingCapacity() {
    return this.standingCapacity;
  }
  
  /**
  * @brief Get the total overall capacity for this train type
  * @return total capacity
  */
  int getTotalCapacity() {
    return this.getSeatedCapacity() + this.getStandingCapacity();
  }

  /**
  * @brief Get this train's identifier string
  * @return The train's identifier
  */
  String getIdentifier() {
    return this.identifier;
  }

  /**
   * @brief Get whether this train can carry passengers or not
   * @return true if the train can carry passengers, otherwise false
   */
  boolean isPassenger() {
    return this.isPassenger;
  }
}
