package departure;

import java.time.LocalTime;

import service_management.ServiceSet;
import service_management.ServiceStep;

/**
 * @class Departure
 * @brief Represents a specific service departing from a junction at a given time.
 */
public final class Departure {

  private final ServiceStep serviceStep;
  private final ServiceSet serviceSet;
  private final LocalTime time;

  /**
   * @brief Constructs a new Departure.
   *
   * @param serviceStep The service step associated with the departure.
   * @param serviceSet The service set containing the service.
   * @param time The scheduled departure time.
   */
  public Departure(ServiceStep serviceStep, ServiceSet serviceSet, LocalTime time) {
    this.serviceStep = serviceStep;
    this.serviceSet = serviceSet;
    this.time = time;
  }

  /**
   * @brief Gets the service step associated with this departure.
   * @return The service step.
   */
  public ServiceStep getServiceStep() {
    return serviceStep;
  }

  /**
   * @brief Gets the service set containing this departure.
   * @return The service set.
   */
  public ServiceSet getServiceSet() {
    return serviceSet;
  }

  /**
   * @brief Gets the scheduled departure time.
   * @return The departure time.
   */
  public LocalTime getTime() {
    return time;
  }
}