package world;
import infrastructure.Network;
import service_management.ServiceManagement;
import user_management.UserManagement;

/**
 * @class holds all globally relevant information
 */
public class World {
  static final Network network = new Network();
  static final ServiceManagement serviceManagement = new ServiceManagement();
  static final UserManagement userManagement = new UserManagement();
 
  static public Network getNetwork() {
    return network;
  }

  static public ServiceManagement getServiceManagement() {
    return serviceManagement;
  }
  
  static public UserManagement getUserManagement() {
    return userManagement;
  }
  
}
