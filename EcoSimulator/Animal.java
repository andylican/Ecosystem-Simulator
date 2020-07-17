/**
 * Animal.java
 * Version 1
 * @author Andy Li
 * December 8, 2018
 * This class is used as an abstract superclass for the 2 animal species in the simulation: the sheep and the wolf.
 * It contains the shared methods and variables for both the sheep and wolf that are not including in the Life superclass.
 */

abstract class Animal extends Life {
  private int maxHealth;
  private boolean moved = false;
  private int previousDirection = 4;
  
  /**
   * Animal
   * This constructor takes in the spawn health and maximun health parameters to create an animal, and is called inside the constructors of all its subclasses.
   * @param 2 integers representing the animal's starting health and the animal's maximum health.
   */
  Animal(int health, int maxHealth) {
    super(health);
    this.maxHealth = maxHealth;
  }
  
  /**
   * getMaxHealth
   * This method is a getter for the maximum health of the animal. The purpose of a getter is to allow other classes to access a private variable, allowing for more control over how variables can be accessed and prevents direct modification.
   * @return An integer representing the animal's maximum health.
   */
  public int getMaxHealth() {
    return maxHealth;
  }
  
  /**
   * hasMoved
   * This method is a getter for if the animal has moved or not, and has the same function as all other getters.
   * @return A boolean that is true if the animal has already moved this turn.
   */
  public boolean hasMoved() {
    return moved;
  }
  
  /**
   * setMoved
   * This method is a setter for if the animal has moved or not. The purpose of a setter is to allow other classes to update a private variable, allowing for more control over how variables can be modified and prevents direct modification.
   * @param A boolean which will be set as the boolean value of moved.
   */
  public void setMoved(boolean b) {
    moved = b;
  }
  
  /**
   * getPrevDirection
   * This method is a getter for the animal's previous travel direction, and has the same function as all other getters.
   * @return An integer representing the animal's previous travel direction.
   */
  public int getPrevDirection() {
    return previousDirection;
  }
  
  /**
   * setPrevDirection
   * This method is a setter for the animal's prevoius travel direction, and has the same function as all other getters.
   * @param An integer representing the most recent direction the animal has travelled, which will become the previous direction for the next turn.
   */
  public void setPrevDirection(int direction) {
    previousDirection = direction;
  }
  
  /**
   * oppositeDirection
   * This method takes in a direction and returns it's opposite direction. This is because when an animal moves right for example, then the next turn it should not be able to move left to its previous position.
   * @param An integer representing the direction of the animal.
   * @return An integer representing the opposite direction of the one in the parameter.
   */
  public int oppositeDirection (int direction) {
    if(direction == 0) {
      return 2;
    } else if(direction == 1) {
      return 3;
    } else if(direction == 2) {
      return 0;
    } else {
      return 1;
    }
  }
  
  /**
   * move
   * This abstract method moves an animal, and is able to use dynmic dispatch to execute the move method unique to each animal, as this class is a superclass of the Wolf and Sheep class.
   * @param 2 integers representing the current row and column, and a 2D integer array representing the possible moves an animal can make (4 directions).
   */
  abstract public void move(int row, int column, World sim, int[][]moves);
}