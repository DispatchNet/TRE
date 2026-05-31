package service_mangement;

import java.util.Set;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * @class Dispatch
 * @brief A class representing an individual dispatch for a service along a ServiceSet
 * @see ServiceSet
 */
public class Dispatch {
  int train_number;
  LocalTime dispatchTime;
  Set<DayOfWeek> runningDays;
}
