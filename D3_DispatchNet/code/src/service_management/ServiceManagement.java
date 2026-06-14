package service_management;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @class ServiceManagement
 * @brief A management class that provides functionality to edit the network.
 *        Maintains the collections of service sets, service requests, service types,
 *        and train types that make up the rail network.
 */
public class ServiceManagement {
  Map<String, ServiceSet> serviceSets = new HashMap<>();
  Map<String, ServiceSet> serviceRequests = new HashMap<>();
  Map<String, ServiceType> serviceTypes = new HashMap<>();
  Map<String, TrainType> trainTypes = new HashMap<>();

  /**
   * @brief Submits a new service request pending approval.
   *        The service set is stored in the requests map with PendingApproval status.
   * @param serviceSet the ServiceSet being requested for addition to the network
   */
  public void addServiceRequest(ServiceSet serviceSet) {
    serviceSet.setStatus(ServiceStatus.PendingApproval);
    serviceRequests.put(serviceSet.id, serviceSet);
  }

  /**
   * @brief Approves a pending service request and promotes it to an active service set.
   *        The service is moved from the requests map to the active service sets map
   *        and its status is updated to Effective.
   * @param id the ID of the service request to approve
   */
  public void approveServiceRequest(String id) {
    ServiceSet serviceSet = serviceRequests.remove(id);
    if (serviceSet != null) {
      serviceSet.setStatus(ServiceStatus.Effective);
      serviceSets.put(id, serviceSet);
    }
  }

  /**
   * @brief Rejects and removes a pending service request.
   *        The service set is removed from the requests map and discarded.
   * @param id the ID of the service request to reject
   */
  public void rejectServiceRequest(String id) {
    serviceRequests.remove(id);
  }

  /**
   * @brief Registers a new train type in the network.
   * @param trainType the TrainType to add
   */
  public void addTrainType(TrainType trainType) {
    trainTypes.put(trainType.getIdentifier(), trainType);
  }

  /**
   * @brief Registers a new service type.
   * @param serviceType the ServiceType to add
   */
  public void addServiceType(ServiceType serviceType) {
    serviceTypes.put(serviceType.getId(), serviceType);
  }

  /**
   * @brief Returns all registered train types.
   * @return an unmodifiable view of the train types map
   */
  public Map<String, TrainType> getTrainTypes() {
    return java.util.Collections.unmodifiableMap(trainTypes);
  }

  /**
   * @brief Returns all registered service types.
   * @return an unmodifiable view of the service types map
   */
  public Map<String, ServiceType> getServiceTypes() {
    return java.util.Collections.unmodifiableMap(serviceTypes);
  }

  /**
   * @brief Directly registers an already-effective ServiceSet (used during deserialization).
   *        Unlike addServiceRequest, this does not change the status of the set.
   * @param serviceSet the ServiceSet to register as effective
   */
  public void addServiceSet(ServiceSet serviceSet) {
    serviceSets.put(serviceSet.id, serviceSet);
  }

  /**
   * @brief Returns all effective service sets.
   * @return an unmodifiable view of the service sets map
   */
  public Map<String, ServiceSet> getServiceSets() {
    return java.util.Collections.unmodifiableMap(serviceSets);
  }

  /**
   * @brief Returns all pending service requests.
   * @return an unmodifiable view of the service requests map
   */
  public Map<String, ServiceSet> getServiceRequests() {
    return java.util.Collections.unmodifiableMap(serviceRequests);
  }

  /**
   * @brief Retrieves an active service set by its ID.
   * @param id the ID of the service set to retrieve
   * @return an Optional containing the ServiceSet with the given ID, or empty if not found
   */
  public Optional<ServiceSet> getServiceSet(String id) {
    return Optional.ofNullable(serviceSets.get(id));
  }

  /**
   * @brief Retrieves a pending service request by its ID.
   * @param id the ID of the service request to retrieve
   * @return an Optional containing the ServiceSet request with the given ID, or empty if not found
   */
  public Optional<ServiceSet> getServiceRequest(String id) {
    return Optional.ofNullable(serviceRequests.get(id));
  }

  /**
   * @brief Retrieves a train type by its identifier.
   * @param id the identifier of the TrainType to retrieve
   * @return an Optional containing the TrainType with the given identifier, or empty if not found
   */
  public Optional<TrainType> getTrainType(String id) {
    return Optional.ofNullable(trainTypes.get(id));
  }

  /**
   * @brief Retrieves a service type by its ID.
   * @param id the ID of the ServiceType to retrieve
   * @return an Optional containing the ServiceType with the given ID, or empty if not found
   */
  public Optional<ServiceType> getServiceType(String id) {
    return Optional.ofNullable(serviceTypes.get(id));
  }
}