package service_mangement;

import java.util.List;
import user_management.TrainCompany;

/**
 * @class ServiceSet
 * @brief A Class defining a ServiceSet
 * @see ServiceStep
 * @see Dispatch
 */
public class ServiceSet {
  TrainCompany company;
  ServiceType type;
  ServiceStatus status;
  List<ServiceStep> steps;
  List<Dispatch> dispatches;
}
