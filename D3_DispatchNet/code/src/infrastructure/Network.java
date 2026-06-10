package infrastructure;

import java.util.Map;

import service_management.ServiceSet;

import java.util.HashMap;
import java.lang.IllegalArgumentException;
import java.util.Collections;

/**
 * @class Network
 * @brief A Class representing the network.
 * Provides an interface to add/remove elements from the network
 */
public class Network {
  Map<String,Junction> junctions;
  Map<String,Line> lines;
  Map<String,ServiceSet> services;

  public Network() {
    this.junctions = new HashMap<>();
    this.lines = new HashMap<>();
  }

  /**
   * @brief Adds a Junction to the network
   * @param junction The junction to add
   * @throws InvalidArgumentException The junction is already present in the map
   */
  public void addJunction(Junction junction) {
    if (this.junctions.containsKey(junction.getId())){
      throw new IllegalArgumentException(
        String.format("cannot add Junction %s twice",junction.getId())
      );
    }
    this.junctions.put(junction.getId(),junction);
  }

  /**
   * @brief Adds an existing Line object to the network.
   * @param line The line to add
   * @throws IllegalArguemtneException if the line is already in the network
   */
  public void addLine(Line line) {
    if (this.lines.containsKey(line.getId())){
      throw new IllegalArgumentException(
        String.format("cannote add Lien %s twice",line.getId())
      );
    }
    this.lines.put(line.getId(),line);
  }

  /**
   * @brief get the map of lines.
   * @return An unmodifiable view of the lines
   */
  public Map<String,Line> getLines() {
    return Collections.unmodifiableMap(lines);
  }

  /**
   * @brief get the map of junctions.
   * @return An unmodifiable view of the junctions
   */
  public Map<String,Junction> getJunctions() {
    return Collections.unmodifiableMap(junctions);
  }

  //TODO: Remove methods
  
}
