/**
 * Plant.java
 * Version 1
 * @author Andy Li
 * December 8, 2018
 * This class is used to create a plant object. Since a plant has no additional features in this simulation compared to the Life superclass, there is only a constructor.
 */

class Plant extends Life {
  
  /**
   * Plant
   * This constructor takes in a health parameter and creates a plant with that health value by calling a constructor on the superclass Life with the same parameters.
   * @param An integer representing the health of the plant to be created.
   */
  Plant (int health) {
    super(health);
  }
}