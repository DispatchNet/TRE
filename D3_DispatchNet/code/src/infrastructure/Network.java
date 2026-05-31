package infrastructure;

import java.util.List;
import service_mangement.ServiceSet;

/**
 * @brief A Class representing the network.
 * Provides an interface to add/remove elements from the network
 */
public class Network {
  List<Junction> junctions;
  List<Line> lines;
  List<ServiceSet> services;

  public void addJunction(Junction junction) {
    this.junctions.add(junction);
  }

  /**
   * Adds a Line to the network and updates relevant adjacencies
   * @param j1 A Junction in the network
   * @param j2 A Junction in the network
   * @param lengthMeters The length of this line in meters
   * @param maxSpeedKpH The max speed of this line in Kilometers per hour
   * @param nTracks The number of tracks this line has
   */
  public void addLine(Junction j1, Junction j2, int lengthMeters, int maxSpeedKpH, int nTracks) {
    this.lines.add(
      new Line(
        j1,j2,lengthMeters,maxSpeedKpH,nTracks
      )
    );
  }

}