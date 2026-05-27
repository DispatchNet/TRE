#include <string>
#include <optional>
#include <time.h>
#include "infrastructure.hpp"

typedef uint32_t cents;
typedef uint32_t minutes;
typedef TODOTYPE TrainCompany;

/**
 * @brief A Class defining a TrainType 
 */
class TrainType {
  std::string identifier;
  uint16_t seatedCapacity;
  uint16_t standingCapacity;
  bool isPassenger;

  public:
  uint16_t getSeatedCapacity();
  uint16_t getStandingCapacity();
  uint16_t getTotalCapacity();
  std::string* getIdentifier();
  bool isPassenger();
  std::string getDetails();
};

///@todo METHDODS UP ALL BELOW

/**
 * @brief A Class defining a ServiceType
 * 
 */
class ServiceType {
  std::string commercial_name;
  TrainType* trainType;
  cents priceKm;
};

/**
 * @brief A Class defining a ServiceType
 * 
 */
class StopData {
  std::string* platform;
  minutes waitTime;
};

/**
 * @brief A Class defining a ServiceStep
 * 
 */
class ServiceStep{
  Junction* junction;
  std::optional<StopData> stopData;
  minutes travelTime;
};


enum class ServiceStatus {
  Effective,
  PendingApproval
};

/**
 * @brief A Class defining a ServiceSet
 * 
 */
class ServiceSet {
  TrainCompany* company;
  ServiceType type;
  ServiceStatus status;
  std::vector<ServiceStep> steps;
  std::vector<Dispatch> dispatches;
};

class Dispatch {
  time_t time;
  bool days[7];
  uint32_t trainNumber;
};

enum class Weekday {
  Monday = 0,
  Tuesday,
  Wednesday,
  Thursday,
  Friday,
  Saturday,
  Sunday
};