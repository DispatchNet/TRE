package infrastructure;

import java.util.UUID;
/**
 * @class Line
 * @brief A Class representing a line between 2 Junction elements.
 */
public class Line {
  private final String id;
  Junction junction1;
  Junction junction2;
  int lengthMeters;
  int maxSpeedKpH;
  int nTracks;
  
  /**
   * @brief Constructs a Line with generated id
   * @param j1 A Junction in the network
   * @param j2 A Junction in the network
   * @param lengthMeters The length of this line in meters
   * @param maxSpeedKpH The max speed of this line in Kilometers per hour
   * @param nTracks The number of tracks this line has
   * 
   * @throws NullPointerException if the any junction is null.
   * @implNote Also adds this line to the junction's adjacencies.
   */
  public Line(Junction j1, Junction j2, int lengthMeters, int maxSpeedKpH, int nTracks) {
    this(UUID.randomUUID().toString(), j1, j2, lengthMeters, maxSpeedKpH, nTracks);
  }

  /**
   * @brief Constructs a Line with explicit id
   * 
   * @throws NullPointerException if the any junction is null.
   * @implNote Also adds this line to the junction's adjacencies.
   */
  public Line(String id, Junction j1, Junction j2, int lengthMeters, int maxSpeedKpH, int nTracks) {
    if (j1 == null || j2 == null) {
      throw new NullPointerException("Line can't be connected to null junction");
    };

    this.id = id == null ? UUID.randomUUID().toString() : id;
    j1.connectedLines.add(this);
    j2.connectedLines.add(this);
    
    this.junction1 = j1;
    this.junction2 = j2;
    this.lengthMeters = lengthMeters;
    this.maxSpeedKpH = maxSpeedKpH;
    this.nTracks = nTracks;
  }

  /**
   * @brief Gets the unique identifier for this line
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @brief Gets one of the junctions this line ends at.
   * @return the junction
   */
  public Junction getJunction1() {
    return junction1;
  }
  
  /**
   * @brief Gets one of the junctions this line ends at.
   * @return the junction
   */
  public Junction getJunction2() {
    return junction2;
  }
  
  /**
   * @brief Gets one of the length of this line in meters.
   * @return the length
   */
  public int getLengthMeters() {
    return lengthMeters;
  }
  
  /**
   * @brief Gets the max speed in kilometers per hour
   * @return The maximum speed
   */
  public int getMaxSpeedKpH() {
    return maxSpeedKpH;
  }
  
  /**
   * @brief Gets the number of tracks of this line
   * @return the number of tracks
   */
  public int getnTracks() {
    return nTracks;
  }

  /**
   * @brief Replaces junction1 of this Line.
   * @param junction the new junction.
   * @implNote also removes this line from the junction's adjacencies.
   * @throws NullPointerExcpetion if the junction is null
   */
  public void setJunction1(Junction junction) {
    if (junction == null) {
      throw new NullPointerException("Line can't be connected to null Junction");
    };
    this.junction1.connectedLines.remove(this);
    this.junction1 = junction;
    this.junction1.connectedLines.add(this);
  }
  
  /**
   * @brief Replaces junction2 of this Line.
   * @param junction the new junction.
   * @implNote also removes this line from the junction's adjacencies.
   * @throws NullPointerExcpetion if the junction is null
   */
  public void setJunction2(Junction junction) {
    if (junction == null) {
      throw new NullPointerException("Line can't be connected to null Junction");
    };
    this.junction2.connectedLines.remove(this);
    this.junction2 = junction;
    this.junction2.connectedLines.add(this);
  }

  /**
   * @brief Sets the length of this Line.
   * @param lengthMeters the length.
   */
  public void setLengthMeters(int lengthMeters) {
    this.lengthMeters = lengthMeters;
  }

  /**
   * @brief Sets the max speed of this line in km/h.
   * @param maxSpeedKpH the maximum speed.
   */
  public void setMaxSpeedKpH(int maxSpeedKpH) {
    this.maxSpeedKpH = maxSpeedKpH;
  }

  /**
   * @brief Sets the number of tracks of this line.
   * @param nTracks the number of tracks of this line.
   */
  public void setnTracks(int nTracks) {
    this.nTracks = nTracks;
  }
  
/**
 * @public returns whether a junction is in junction1 or junction2
 * @param jun a junciton pointer 
 * @reutrn a booleanean
 */
  public boolean hasJunction(Junction jun) {
    return jun == this.junction1 || jun == this.junction2; 
  }

  static void main () {//Unit test

  }
}
