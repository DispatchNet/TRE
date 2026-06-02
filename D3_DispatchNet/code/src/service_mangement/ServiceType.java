package service_mangement;

/**
 * @class ServiceType
 * @brief A Class defining a ServiceType
 * 
 */
class ServiceType {
  final String id;
  String commercial_name;
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
    this.commercial_name = commercial_name;
    this.trainType = trainType;
    this.centsPerKm = centsPerKm;
  }

  public String getId() {
    return id;
  }

  public String getCommercial_name() {
    return commercial_name;
  }

  public TrainType getTrainType() {
    return trainType;
  }

  public int getCentsPerKm() {
    return centsPerKm;
  }

  public void setCommercial_name(String commercial_name) {
    this.commercial_name = commercial_name;
  }

  public void setTrainType(TrainType trainType) {
    this.trainType = trainType;
  }

  public void setCentsPerKm(int centsPerKm) {
    this.centsPerKm = centsPerKm;
  }
}
