/**
 * Sheep.java
 * Version 1
 * @author Andy Li
 * December 8, 2018
 * This class is used to create a sheep object which is on of the 3 species in our ecosystem. 
 * Each sheep has their unique movement and pathfinding methods, can repreduce, and can eat plants.
 */

import java.util.Queue;
import java.util.LinkedList;
class Sheep extends Animal {
  
  /**
   * Sheep
   * This constructor takes in the spawn health and the maximun health and creates a new sheep by calling the constructor on the super class Animal with the same parameters.
   * @param 2 Integers representing the sheep's spawn health and the sheep's maximun health.
   */
  Sheep(int health, int maxHealth) {
    super(health,maxHealth);
  }
  
  /**
   * eatPlant
   * This method takes in a plant as a parameter and transfers the plant's nutritionla value (health) to the sheep's health.
   * The sheep will obtain its maximun health if the plant provides enough health gain to give the sheep more health than its maximum.
   * @param A Plant that will be eaten by the sheep.
   */
  public void eatPlant(Plant p) {
    this.setHealth(Math.min(this.getHealth() + p.getHealth(),this.getMaxHealth()));
  }
  
  /**
   * reproduce
   * This method takes in another sheep, the simulation, and the map as parameters and causes the 2 sheep to both lose 10 health and create a baby sheep with 10 health in a random square.
   * If any of the 2 sheep has 20 health or less, they do not reproduce and they do not lose 10 health.
   * @param Another Sheep who will reproduce with this sheep, a World representing the current simulation, and a 2D array of type Life that represents the map of the world.
   */
  public void reproduce(Sheep s, World sim, Life[][] newMap) {
    
    //Reproduction will only happen is there is enough space and both sheep have more than 20 health.
    if((this.getHealth() > 20) && (s.getHealth() > 20) && (sim.getEmptyCount() != 0)) {
      int sheepSpawned = 0;
      
      //Keeps trying 2 random coordinates for the baby sheep to spawn and will terminate if the coordinate is empty so the baby sheep will spawn.
      while(sheepSpawned == 0) {
        int row = (int)(Math.random()*newMap.length);
        int column = (int)(Math.random()*newMap[0].length);
        if(newMap[row][column] == null) {
          newMap[row][column] = new Sheep(10,this.getMaxHealth());
          sheepSpawned++;
        }
      }
      sim.setEmptyCount(sim.getEmptyCount()-1);
      this.setHealth(this.getHealth()-10);
      s.setHealth(s.getHealth()-10);
    }
  }
  
  /**
   * move
   * This methid takes in the current row and column of the sheep and will move the sheep to an adjacent square. The method for pathfinding is called here, which will decide where it will move.
   * If no target is found, it will move a random direction, but never in the direction it just came from in order to allow it to wander further.
   * @param 2 integers representing the current row and column of the sheep, along with a World representing the current simulation and a 2D integer away that represents the 4 different directions the sheep can move.
   */
  public void move(int row, int column, World sim, int[][]moves) {
    
    //Declare and initalize variables. This sheep's moved value is set to true so it isn't moved twice in the World class.
    //The moved boolean is used in the while loop below, and the boolean noTarget holds true if a pathfinding search has been done and no target was found.
    Life[][] newMap = sim.getMap();
    boolean moved = false;
    this.setMoved(true);
    int direction = -1;
    boolean noTarget = false;
    
    //Keeps looping and finding a direction until the sheep is able to move and has moved
    while(!moved) {
      
      //If a pathfinding search hasn't been performed yet, perform it.
      if(!noTarget) {
        direction = this.findTarget(newMap,row,column,sim,moves);
        if(direction == -1) {
          noTarget = true;
        }
      }
      
      //If no target was found in the pathfinding algorithm, generate a random direction
      if(noTarget) {
        direction = (int)(Math.random()*5);
      }
      
      //If the sheep moves (a direction of 4 means it stays still)
      if(direction != 4) {
        
        //A new row and new column representing the new coordinates of the sheep. The direction is an integer and is used as an index for the 2D moves array, with each number from 0-3 representing one of the 4 directions.
        int newRow = row+moves[direction][0];
        int newColumn = column+moves[direction][1];
        
        //If the new coordinates are within boundaries and the sleected direction does not lead back to the previous square it came from (if it is not pursuing a target)
        if((sim.inBounds(newRow,newColumn)) && ((direction != this.getPrevDirection()) || (!noTarget))) {
          
          //If the new square is empty or contains a plant
          if(!(newMap[newRow][newColumn] instanceof Wolf) && !(newMap[newRow][newColumn] instanceof Sheep)) {
            
            //Calls the eat plant method if it is a plant
            if(newMap[newRow][newColumn] instanceof Plant) {
              Plant p = (Plant)newMap[newRow][newColumn];
              this.eatPlant(p);
            }
            
            //Moves the sheep to the new square, replacing and killing the plant there if there is one, and makes the previous square the sheep came from empty.
            //Also calls a method to set the previous direction of the sheep to the one it came from (for example if the sheep travelled to the right, the sheep would not be able to travel left next turn if it isn't currently pursuing a target).
            newMap[newRow][newColumn] = newMap[row][column];
            newMap[row][column] = null;
            moved = true;
            this.setPrevDirection(this.oppositeDirection(direction));
          } else if(newMap[newRow][newColumn] instanceof Sheep) {
            
            //If the new location contains another sheep, call the reproduce method
            Sheep s = (Sheep)newMap[newRow][newColumn];
            this.reproduce(s,sim, newMap);
            moved = true;
          } else {
            Wolf w = (Wolf)newMap[newRow][newColumn];
            w.eatSheep(this);
            newMap[row][column] = null;
            moved = true;
          }
        }
      } else {
        //If the sheep choses not to move, it still counts as a move and the previous direction would be it standing still, so next turn it will have to move.
        this.setPrevDirection(4);
        moved = true;
      }
    }
    
    //Updates the map to the simulation
    sim.setMap(newMap);
  }
  
  /**
   * findTarget
   * This method performs a breadth first search (goes from closest to furthest squares) and returns the direction that moves the sheep towards the nearest and highest priority target.
   * It prioritizes s other sheep for reproduction and then plants for health gain if it is not able to reproduce.
   * @param 2 Integers representing the current row and column of the sheep, a 2D array of type Life representing the world map,
   * a 2D array of integers representing the possible moves the sheep can perform (4 directions) and a World representing the current simulation.
   * @return An integer which is the direction for the sheep to travel to the target.
   */
  public int findTarget(Life[][] newMap, int row, int column, World sim, int[][] moves) {
    
    //Initialize variables. A queue is used for the breadth first search and the integer array currentPos is used to represent the current position of the search along with its distance from the sheep's location.
    //A visited array is also used to prevent searching the same square more than once. The variable nextDireciton is the direction the sheep should travel in, and is the return value.
    int[] currentPos = new int[3];
    currentPos[0] = row;
    currentPos[1] = column;
    Queue<int[]>q = new LinkedList<>();
    q.add(currentPos);
    boolean foundSheep = false;
    boolean foundPlant = false;
    boolean[][] visited = new boolean[newMap.length][newMap[0].length];
    visited[row][column] = true;
    int nextDirection = -1;
    
    //While there are more squares to search and a sheep has not been found
    while((!q.isEmpty()) && (!foundSheep)) {
      
      //Current position array is the first element in the queue
      currentPos = q.remove();
      
      //The sheep has a search range of 5, which means that it will search for any targets that are 5 moves/squares or less away from its position.
      if(currentPos[2] <= 5) {
        
        //Loops through the 4 directions
        for(int i=0; i<4; i++) {
          
          //Makes coordintes for the new row and column
          int newRow = currentPos[0]+moves[i][0];
          int newColumn = currentPos[1]+moves[i][1];
          
          //If the new coordinates are in the map boundaries and they haven't been searched yet
          if(sim.inBounds(newRow,newColumn)) {
            if(!visited[newRow][newColumn]) {
              
              //If this sheep has more than 20 health and if the search finds another sheep with more than 20 health, it sets the variable nextDirection to i,
              //which is accurate as i is the direction the search has taken to find the current target, and the search only checks squares further away from the sheep than the current one.
              //foundSheep is also set to true which allows the termination of the search.
              if(this.getHealth()>20) {
                if(newMap[newRow][newColumn] instanceof Sheep) {
                  if(newMap[newRow][newColumn].getHealth()>20) {
                    nextDirection = i;
                    foundSheep = true;
                  }
                } 
              } 
              
              //If a sheep has not been found (as another sheep takes priority over a plant) and a plant has not been found yet (to ensure this is the closest plant)
              //This updates the nextDirection but does not terminate the loop as if there is a sheep that is found later and further away, it will take priotity over this and will change the nextDirection value.
              if(newMap[newRow][newColumn] instanceof Plant && !foundSheep && !foundPlant) {
                nextDirection = i;
                foundPlant = true;
              }
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
    
    //Returns the direction that the sheep should travel in to reach its target.
    return nextDirection;
  }
}