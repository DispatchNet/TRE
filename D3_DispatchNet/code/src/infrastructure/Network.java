package infrastructure;

import java.util.Map;
import java.util.Optional;

import service_management.ServiceSet;

import java.util.HashMap;
import java.util.List;
import java.lang.IllegalArgumentException;
import java.util.Collections;

/**
 * @class Network
 * @brief A Class representing the network.
 * Provides an interface to add/remove elements from the network
 */
public class Network {

  /** 
   * @enum Represents the kind of errors which may happen when an element is edited /
   */
  public enum EditError {
    IN_USE,
    NOT_FOUND
  }


  Map<String,Junction> junctions;
  Map<String,Line> lines;
  Map<String,ServiceSet> services;

  public Network() {
    this.junctions = new HashMap<>();
    this.lines = new HashMap<>();
  }

  /**
   * @brief Adds a Junction to the network
   * @usecase 21,22
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
   * @usecase 22
   * @throws IllegalArguemtneException if the line is already in the network
   */
  public void addLine(Line line) {
    if (this.lines.containsKey(line.getId())){
      throw new IllegalArgumentException(
        String.format("cannote add Line %s twice",line.getId())
      );
    }
    this.lines.put(line.getId(),line);
  }

  /**
   * @brief get the map of lines.
   * @usecase 13,15
   * @return An unmodifiable view of the lines
   */
  public Map<String,Line> getLines() {
    return Collections.unmodifiableMap(lines);
  }

  /**
   * @brief get the map of junctions.
   * @usecase 13,15
   * @return An unmodifiable view of the junctions
   */
  public Map<String,Junction> getJunctions() {
    return Collections.unmodifiableMap(junctions);
  }

  /**
   * @breif Search a junction on it's network by it's name
   * @usecase{UC14}
   * @param name the name of the junction
   * @return the Junction if's found, Optional.empty() otherwise
   */
  public Optional<Junction> getJunctionByName(String name) {
    for (Junction jct : this.getJunctions().values()) {
      if(jct.getName().strip().equalsIgnoreCase(name)) {
        return Optional.of(jct);
      }
    }
    return Optional.empty();
  }
  
  /**
   * @brief remove a junction from the network
   * @usecase 26,27
   * @param id the id of the jucntion
   * @return If any error occured, the error
   * @implNote Also removes all lines connected to the deleted junction, as well as adjacencies from neighbhoring junctions
   * @see EditError
   */
  public Optional<EditError> removeJunction(String id) {
    if (!this.junctions.containsKey(id)){
      return Optional.of(EditError.NOT_FOUND);
    }
    
    Junction jct = this.junctions.get(id);
    List<Line> lines = jct.getConnectedLines();

    if (jct.getServiceSets().size() > 0) {
      return Optional.of(EditError.IN_USE);
    }
    
    this.junctions.remove(id);

    for(var line : lines) {
      this.lines.remove(line.getId());
      Junction otherJunction = line.getJunction1() != jct ? line.getJunction1() : line.getJunction2();
      otherJunction.connectedLines.remove(line);
    }

    return Optional.empty();
  }

  /**
   * @brief remove a junction from the network
   * @usecase 28
   * @param id the id of the jucntion
   * @return If any error occured, the error, otherwise Optional.empty()
   * @implNote Also removes this line from the known adjacencies of it's connected lines
   * @see EditError
   */
  public Optional<EditError> removeLine(String id) {
    if (!this.lines.containsKey(id)){
      return Optional.of(EditError.NOT_FOUND);
    }
    
    Line line = this.lines.get(id);
    if (line.isUsed()) {
      return Optional.of(EditError.IN_USE);
    };

    this.lines.remove(id);

    //remove adjacencies
    line.junction1.connectedLines.remove(line);
    line.junction2.connectedLines.remove(line);

    return Optional.empty();
  }

}
