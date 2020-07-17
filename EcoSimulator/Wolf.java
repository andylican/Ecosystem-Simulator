/**
 * Wolf.java
 * Version 1
 * @author Andy Li
 * December 8, 2018
 * This class is used to create a wolf object which is on of the 3 species in our ecosystem.
 * Each wolf has their unique movement and pathfinding methods, can repreduce, can fight other wolves, can trample plants, and can eat sheep.
 * Also wolves implement the comparable interface to determine which wolf wins the duel.
 */

import java.util.Queue;
import java.util.LinkedList;
class Wolf extends Animal implements Comparable{
  
  /**
   * Wolf
   * This constructor takes in the spawn health and the maximun health and creates a new wolf by calling the constructor on the super class Animal with the same parameters.
   * @param 2 Integers representing the wolf's spawn health and the wolf's maximun health.
   */
  Wolf(int health, int maxHealth) {
    super(health,maxHealth);
  }
  
  /**
   * compareTo
   * This method is part of the comparable interface and it return an integer from -1 to 1 depending on if this wolf has more health, less health,
   * or the same amount of health as the other wolf (the wolf this one is being compared to).
   * @param An object representing the wolf that this wolf is being compared to. The object is casted as a wolf later as the comparable interface only allows on Object superclass as the parameter for this method.
   * @return An integer representing the resulting value form the comparision. If this wolf has more health that the other wolf, the return value is 1,
   * if this wolf has less health than the other wolf, the return value is -1, and if both wolves have the same amount of health, the return value is 0.
   */
  public int compareTo(Object o) {
    Wolf w = (Wolf)o;
    if(this.getHealth()>w.getHealth()) {
      return 1;
    } else if(this.getHealth()<w.getHealth()) {
      return -1;
    } else {
      return 0;
    }
  }
  
  
  /**
   * eatSheep
   * This method takes in a sheep as a parameter and transfers the sheep's nutritionla value (health) to the wolf's health.
   * The wolf will obtain its maximun health if the sheep provides enough health gain to give the wolf more health than its maximum.
   * @param A Sheep that will be eaten by the wolf.
   */
  public void eatSheep (Sheep s) {
    this.setHealth(Math.min(this.getHealth()+s.getHealth(),this.getMaxHealth()));
  }
  
   /**
   * reproduce
   * This method takes in another wolf, the simulation, and the map as parameters and causes the 2 wolves to both lose 10 health and create a baby wolf with 10 health in a random square.
   * @param Another Wolf who will reproduce with this wolf, a World representing the current simulation, and a 2D array of type Life that represents the map of the world.
   */
  public void reproduce(Wolf w, World sim, Life[][] newMap) {
    if((sim.getEmptyCount() != 0)) {  
      int wolf = 0;
      while(wolf == 0) {
        int row = (int)(Math.random()*newMap.length);
        int column = (int)(Math.random()*newMap[0].length);
        if(newMap[row][column] == null) {
          newMap[row][column] = new Wolf(10,this.getMaxHealth());
          wolf++;
        }
      }
      sim.setEmptyCount(sim.getEmptyCount()-1);
      this.setHealth(this.getHealth()-10);
      w.setHealth(w.getHealth()-10);
    }
  }
  
  /**
   * fight
   * This method simulates a duel between this wolf and another wolf, and used the compareTo method to apply the damage correctly.
   * The wolf with less health will take 10 damage and the one with more health will remain undamaged. If both wolves have the same health, they both take 10 damage.
   * @param Another Wolf that is going to be dueling this wolf.
   */
  public void fight(Wolf w) {
    if(this.compareTo(w)>0) {
      w.setHealth(w.getHealth()-10);
    } else if(this.compareTo(w)<0) {
      this.setHealth(this.getHealth()-10);
    } else {
      this.setHealth(this.getHealth()-10);
      w.setHealth(w.getHealth()-10);
    }
  }
  
  /**
   * move
   * This methid takes in the current row and column of the wolf and will move the wolf to an adjacent square. The method for pathfinding is called here, which will decide where it will move.
   * If no target is found, it will move a random direction, but never in the direction it just came from in order to allow it to wander further.
   * @param 2 integers representing the current row and column of the sheep, along with a World representing the current simulation and a 2D integer away that represents the 4 different directions the sheep can move.
   */
  public void move(int row, int column, World sim, int[][]moves) {
    
        
    //Declare and initalize variables. This wolf's moved value is set to true so it isn't moved twice in the World class.
    //The moved boolean is used in the while loop below, and the boolean noTarget holds true if a pathfinding search has been done and no target was found.
    Life[][] newMap = sim.getMap();
    boolean moved = false;
    this.setMoved(true);
    int counter = 0;
    boolean noTarget = false;
    int direction = -1;
    
      //Keeps looping and finding a direction until the wolf is able to move and has moved
    while(!moved) {
      
      //If a pathfinding search hasn't been performed yet, perform it.
      if(!noTarget) {
        direction = findTarget(newMap,row,column,sim,moves);
        if(direction == -1) {
          noTarget = true;
        }
      } 
      
      //If no target was found in the pathfinding algorithm, generate a random direction
      if(noTarget) {
        direction = (int)(Math.random()*5);
      } 
      
      //If the wolf moves (a direction of 4 means it stays still)
      if(direction != 4) {
        
        //A new row and new column representing the new coordinates of the wolf. The direction is an integer and is used as an index for the 2D moves array, with each number from 0-3 representing one of the 4 directions.
        int newRow = row+moves[direction][0];
        int newColumn = column+moves[direction][1];
        
        //If the new coordinates are within boundaries and the sleected direction does not lead back to the previous square it came from (if it is not pursuing a target)
        if((sim.inBounds(newRow,newColumn)) && ((direction != this.getPrevDirection()) || (!noTarget))) {
          
          //If the next square is empty, a plant, or a sheep
          if(!(newMap[newRow][newColumn] instanceof Wolf)) {
            
            //If the next square is a sheep, call the eatSheep method
            if(newMap[newRow][newColumn] instanceof Sheep) {
              Sheep s = (Sheep)newMap[newRow][newColumn];
              this.eatSheep(s);
            }
            
            //Moves the wolf to the new square, replacing and killing the plant or sheep there if there is one, and makes the previous square the wolf came from empty.
            //Also calls a method to set the previous direction of the wolf to the one it came from (for example if the wolf travelled to the right, the wolf would not be able to travel left next turn if it isn't currently pursuing a target).
            newMap[newRow][newColumn] = newMap[row][column];
            newMap[row][column] = null;
            this.setPrevDirection(this.oppositeDirection(direction));
          } else {
            
            //If the new square contains another wolf, reproduce with it if both wolves have more than 20 health, and if not, then duel it.
            Wolf w = (Wolf)newMap[newRow][newColumn];
            if((this.getHealth() > 20) && (w.getHealth() > 20)) {
              this.reproduce(w,sim, newMap);
            } else {
              this.fight(w);
              
              //Kills the wolf that has 0 or less health from the duel
              if(this.getHealth()<=0) {
                newMap[row][column] = null;
              }
              if(w.getHealth()<=0) {
                newMap[newRow][newColumn] = newMap[row][column];
                newMap[row][column] = null;
              }
            }
          }
          moved = true;
        }
      } else {
        
        //If the wolf choses not to move, it still counts as a move and the previous direction would be it standing still, so next turn it will have to move.
        moved = true;
        this.setPrevDirection(4);
      }
    }
    
    
    //Updates the map to the simulation
    sim.setMap(newMap);
  }
  
   
  /**
   * findTarget
   * This method performs a breadth first search (goes from closest to furthest squares) and returns the direction that moves the wolf towards the nearest and highest priority target.
   * It prioritizes other wolves for reproduction and then sheep for health gain if it is not able to reproduce.
   * @param 2 Integers representing the current row and column of the wolf, a 2D array of type Life representing the world map,
   * a 2D array of integers representing the possible moves the wolf can perform (4 directions) and a World representing the current simulation.
   * @return An integer which is the direction for the wolf to travel to the target.
   */
  public int findTarget(Life[][] newMap, int row, int column, World sim, int[][] moves) {
    
      
    //Initialize variables. A queue is used for the breadth first search and the integer array currentPos is used to represent the current position of the search along with its distance from the wolf's location.
    //A visited array is also used to prevent searching the same square more than once. The variable nextDireciton is the direction the wolf should travel in, and is the return value.
    int nextDirection = -1;
    boolean foundSheep = false;
    boolean foundWolf = false;
    int[] currentPos = new int[3];
    currentPos[0] = row;
    currentPos[1] = column;
    boolean[][] visited = new boolean[newMap.length][newMap[0].length];
    visited[row][column] = true;
    Queue<int[]>q = new LinkedList<>();
    q.add(currentPos);
    
    //While there are more squares to search and a wolf has not been found
    while((!q.isEmpty()) && (!foundWolf)) {
      
      //Current position array is the first element in the queue
      currentPos = q.remove();
      
      //The wolf has a search range of 20, which means that it will search for any targets that are 20 moves/squares or less away from its position.
      if(currentPos[2]<=20) {
        
         //Loops through the 4 directions
        for(int i=0; i<4; i++) {
          
          //Makes coordintes for the new row and column
          int newRow = currentPos[0]+moves[i][0];
          int newColumn = currentPos[1]+moves[i][1];
          
          //If the new coordinates are in the map boundaries and they haven't been searched yet
          if(sim.inBounds(newRow,newColumn)) {
            if(!visited[newRow][newColumn]) {
              
               
              //If this wolf has more than 20 health and if the search finds another wolf with more than 20 health, it sets the variable nextDirection to i,
              //which is accurate as i is the direction the search has taken to find the current target, and the search only checks squares further away from the wolf than the current one.
              //foundWolf is also set to true which allows the termination of the search.
              if(this.getHealth() > 20) {
                if(newMap[newRow][newColumn] instanceof Wolf) {
                  if(newMap[newRow][newColumn].getHealth()>20) {
                    nextDirection = i;
                    foundWolf = true;
                  }
                } 
              } 
              
                  
              //If a wolf has not been found (as another wolf takes priority over a sheep) and a sheep has not been found yet (to ensure this is the closest sheep)
              //This updates the nextDirection but does not terminate the loop as if there is a wolf that is found later and further away, it will take priotity over this and will change the nextDirection value.
              if((newMap[newRow][newColumn] instanceof Sheep) && (!foundSheep) && (!foundWolf)) {
                nextDirection = i;
                foundSheep = true;
              } 
              
              //Adds the new position array to the queue
              int[] newPos = new int[3];
              newPos[0] = newRow;
              newPos[1] = newColumn;
              newPos[2] = currentPos[2]+1;
              visited[newRow][newColumn] = true;
              q.add(newPos);
            }
          }
        }
      } 
    }
    
    //Returns the direction that the sheep should travel in to reach its target.
    return nextDirection;
  }
}