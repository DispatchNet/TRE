#include <optional>
#include <string>
#include <vector>
#include <set>
#include <memory>
#include <tuple>

typedef int Meters;
typedef int KilometersPerHour;

typedef void TODOTYPE;
typedef TODOTYPE ServiceSet;

/**
 * @brief A Class representing a GPS coordinate.
 */
class GeoCoordinate {
  float latitude, longitude;
};

/**
 * @brief A Class representing a junction's data.
 */
class Junction {
  GeoCoordinate location;
  std::optional<StationData> stationData;
  std::string name;
  std::vector<Line*> connectedLines;
  std::vector<ServiceSet*>* services;
};

/**
 * @brief A Class containing a station's data.
 * Used by Junction
 */
class StationData {
  std::vector<std::string> platforms;
};

/**
 * @brief A Class representing a line between 2 Junction elements.
 */
class Line {
  std::tuple<Junction*,Junction*> junctions;
  Meters length;
  KilometersPerHour maxSpeed;
  int nTracks;
  
  public:
  std::tuple<Junction*,Junction*>* getJunctions();
  Meters getLength();
  KilometersPerHour getMaxSpeed();
};

/**
 * @brief A Class representing the network.
 * Provides an interface to add/remove elements from the network
 */
class Network {
  std::vector<Junction> junctions;
  std::vector<Line> lines;

  public:
  void addLine(Line line);
  void removeLine(Line *line);
  void addJunction(Junction junction);
  void removeJunction(Junction* junction);
  std::vector<Junction>* getJunctions();
  std::vector<Line>* getLines();
};