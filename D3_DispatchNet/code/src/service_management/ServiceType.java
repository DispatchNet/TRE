package service_management;
/**
 * @class ServiceType
 * @brief A Class defining a ServiceType
 * 
 */
public class ServiceType {
  final String id;
  String commercialName;
  TrainType trainType;
  int centsPerKm;

  /**
   * @brief Construct a ServiceType
   * @param id the id of this service type
   * @param commercial_name the commercial name used by this service type
   * @param trainType the train type used to run this ServiceType
   * @param centsPerKm used to calculate ticket prices
   */
  public ServiceType(String id, String commercial_name, TrainType trainType, int centsPerKm) {
    this.id = id;
    this.commercialName = commercial_name;
    this.trainType = trainType;
    this.centsPerKm = centsPerKm;
  }

  /**
   * @brief Returns the unique identifier of this ServiceType
   * @return the id of this ServiceType
   */
  public String getId() {
    return id;
  }

  /**
   * @brief Returns the commercial name of this ServiceType
   * @return the commercial name of this ServiceType
   */
  public String getCommercialName() {
    return commercialName;
  }

  /**
   * @brief Returns the TrainType associated with this ServiceType
   * @return the TrainType used to run this ServiceType
   */
  public TrainType getTrainType() {
    return trainType;
  }

  /**
   * @brief Returns the price rate of this ServiceType
   * @return the price in cents per kilometer used to calculate ticket prices
   */
  public int getCentsPerKm() {
    return centsPerKm;
  }

  /**
   * @brief Sets the commercial name of this ServiceType
   * @param commercialName the new commercial name to assign to this ServiceType
   */
  public void setCommercialName(String commercialName) {
    this.commercialName = commercialName;
  }

  /**
   * @brief Sets the TrainType of this ServiceType
   * @param trainType the new TrainType to assign to this ServiceType
   */
  public void setTrainType(TrainType trainType) {
    this.trainType = trainType;
  }

  /**
   * @brief Sets the price rate of this ServiceType
   * @param centsPerKm the new price in cents per kilometer used to calculate ticket prices
   */
  public void setCentsPerKm(int centsPerKm) {
    this.centsPerKm = centsPerKm;
  }
}