package departure;

import java.time.LocalTime;

import service_mangement.ServiceSet;
import service_mangement.ServiceStep;

/**
 * @class A Departure represents a specific service leaving a certain junctionat a certain time
 */
public record Departure (
  ServiceStep serviceStep,
  ServiceSet serviceSet,
  LocalTime time
) {}
