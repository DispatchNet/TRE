package service_management;

import java.util.List;
import java.util.UUID;
import java.util.Collections;
import user_management.TrainCompany;

/**
 * @class ServiceSet
 * @brief A Class defining a set of services along a common route
 * @see ServiceStep
 * @see Dispatch
 */
public class ServiceSet {
  final String id;
  TrainCompany company;
  ServiceType type;
  ServiceStatus status;
  List<ServiceStep> steps;
  List<Dispatch> dispatches;
  
  /**
   * @brief Construct a serviceSet
   * @param id This serviceSet's ID
   * @param company The Company that runs this serivce
   * @param type This service's type
   * @param status This services's status (PENDING REVIEW)
   * @param steps This service's steps
   * @param dispatches This service's dispatches
   */
  public ServiceSet(
    String id,
    TrainCompany company,
    ServiceType type,
    ServiceStatus status,
    List<ServiceStep> steps,
    List<Dispatch> dispatches
  ) {
    this.id = id;
    this.company = company;
    this.type = type;
    this.status = status;
    this.steps = steps;
    this.dispatches = dispatches;
  }

  /**
   * @brief Construct a serviceSet without an explicit ID
   * @param company The Company that runs this serivce
   * @param type This service's type
   * @param status This services's status (PENDING REVIEW)
   * @param steps This service's steps
   * @param dispatches This service's dispatches
   */
  public ServiceSet(
    TrainCompany company,
    ServiceType type,
    ServiceStatus status,
    List<ServiceStep> steps,
    List<Dispatch> dispatches
  ) {
    this(
      UUID.randomUUID().toString(),
      company,
      type,
      status,
      steps,
      dispatches
    );
  }
  /**
   * Get this service's operator
   * @return the company
   */
  public TrainCompany getCompany() {
    return company;
  }
  /**
   * Get this service's type
   * @return the type
   */
  public ServiceType getType() {
    return type;
  }

  /**
   * @brief get the status of this service
   * @return the service's status
   */
  public ServiceStatus getStatus() {
    return status;
  }
  /**
   * @brief set the status of this service
   * @param status the new status for this service
   */
  public void setStatus(ServiceStatus status) {
    this.status = status;
  }


  /**
   * @breif Get this service's steps.
   * @return An immutable ordered list of the steps that make up this service
   * @see ServiceStep
   */
  public List<ServiceStep> getSteps() {
    return Collections.unmodifiableList(steps);
  }

  /**
   * @brief Get this service's dispatches.
   * @return An immutable list of this service's dispatches
   * @see Dispatch
   */
  public List<Dispatch> getDispatches() {
    return Collections.unmodifiableList(dispatches);
  }
  
}
