/**
 * Life.java
 * Version 1
 * @author Andy Li
 * December 8, 2018
 * This class is used as an abstract superclass for the 3 species in the simulation: the plant, the sheep, and the wolf.
 * It contains the shared methods and variables for all 3 species.
 */
abstract class Life {
  private int health;
  
  /**
   * Life
   * This constructor takes in a health parameter and creates a species with that much health, and is called inside the constructors of all its subclasses.
   * @param An integer repsresenting the health of the species.
   */
  Life (int health) {
    this.health = health;
  } 
  
  /**
   * getHealth
   * This method is a getter for the health of the species. The purpose of a getter is to allow other classes to access a private variable, allowing for more control over how variables can be accessed and prevents direct modification.
   * @return An integer representing the health of the species.
   */
  public int getHealth() {
    return health;
  }
  
  /**
   * setHealth
   * This method is a setter for the health of the species. The purpose of a setter is to allow other classes to update a private variable, allowing for more control over how variables can be modified and prevents direct modification.
   * @param An integer representing the updated health of the species.
   */
  public void setHealth(int health) {
    this.health = health;
  }
  
}