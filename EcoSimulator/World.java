/**
 * World.java
 * Version 1
 * @author Andy Li
 * December 8, 2018
 * This class is used to create a World object, which represents the simulation itself and contains the
 * map, the statistics and counts of each species, and is the only class aside from DisplayGrid
 * that directly interacts with the EcoSimulator main class, in that it is the only object that is
 * created in that class, and the species objects are created in this class.
 */

import java.util.Arrays;
class World {
  
  //Initialize private variables
  private int rows;
  private int columns;
  private int plantCount;
  private int sheepCount;
  private int wolfCount;
  private int emptyCount;
  private int turnCount;
  private int plantHealth;
  private int sheepHealth;
  private int wolfHealth;
  private Life[][] map;
  private int[][]moves = {{1,0},{0,1},{-1,0},{0,-1}};
  
  /**
   * World
   * This constructor create an instance of this class, which will set the initial values of
   * health and size to the class variables, and will also call the creation methods to spawn
   * the starting amounts of each species.
   * @param 8 integers which represent the parameters of the simulation based on user input in the EcoSimulator class.
   */
  World(int rows, int columns, int plants, int sheep, int wolves, int plantHealth, int sheepHealth, int wolfHealth) {
    this.rows = rows;
    this.columns = columns;
    emptyCount = rows*columns;
    plantCount = plants;
    sheepCount = sheep;
    wolfCount = wolves;
    this.plantHealth = plantHealth;
    this.sheepHealth = sheepHealth;
    this.wolfHealth = wolfHealth;
    map = new Life[rows][columns];
    createPlants(plantCount);
    createSheep(sheepCount);
    createWolves(wolfCount);
  }
  
  /**
   * getMap
   * This method is a getter for the map array. The purpose of a getter is to allow other classes to access a private variable, allowing for more control over how variables can be accessed and prevents direct modification.
   * Note that it creates a copy of it then returns it so other classes are not able to directly modify it without passing it through the getters and setters.
   * @return A 2D array of type Life that represents the world map that contains all the species and their locations.
   */
  public Life[][] getMap() {
    Life[][] newMap = new Life[rows][columns];
    for(int i=0; i<rows; i++) {
      newMap[i] = map[i].clone();
    }
    return newMap;
  }
  
  /**
   * setMap
   * This method is a setter for the map array. The purpose of a setter is to allow other classes to update a private variable, allowing for more control over how variables can be modified and prevents direct modification.
   * @param A 2D array of type Life that represents the update world map.
   */
  public void setMap(Life[][] newMap) {
    for(int i=0; i<rows; i++) {
      map[i] = newMap[i].clone();
    }
  }
  
  /**
   * getEmptyCount
   * This method is a getter for the amount of empty squares in the map, and has the same function as all other getters.
   * @return An integer representing the amount of empty squares in the world map.
   */
  public int getEmptyCount() {
    return emptyCount;
  }
  
  /**
   * setEmptyCount
   * This method is a setter for the amount of empty squares in the map, and has the same function as all other setters.
   * @param An integer representing the amount of empty squares in the map.
   */
  public void setEmptyCount(int emptyCount) {
    this.emptyCount = emptyCount;
  }
  
  /**
   * getTurnCount
   * This method is a getter for the amount of turns elapsed in the simulation, and has the same function as all other getters.
   * @return An integer representing the amount of turns elapsed in the simulation.
   */
  public int getTurnCount() {
    return turnCount;
  }
  
  /**
   * getPlantCount
   * This method is a getter for the amount of plants on the map, and has the same function as all other getters.
   * @return An integer representing the amount of plants on the map.
   */
  public int getPlantCount() {
    return plantCount;
  }
  
  /**
   * getSheepCount
   * This method is a getter for the amount of sheep on the map, and has the same function as all other getters.
   * @return An integer representing the amount of sheep on the map.
   */
  public int getSheepCount() {
    return sheepCount;
  }
  
  /**
   * getWolfCount
   * This method is a getter for the amount of wolves on the map, and has the same function as all other getters.
   * @return An integer repreesenting the amount of wolves on the map.
   */
  public int getWolfCount() {
    return wolfCount;
  }
  
  /**
   * inBounds
   * This method takes in a row and column and checkts to see if it is within the boundaries of the map.
   * @param 2 integers representing the current row and column.
   * @retun A boolean that is true if the row and column are within the boundaries of the map.
   */
  public boolean inBounds(int r, int c) {
    return ((r>=0) && (r<rows) && (c>=0) && (c<columns));
  }
  
  /**
   * moveAnimals
   * This method loops through the entire map once and will call the move method on the object if it is an animal, and will call the correct
   * move method based on the type of animal using dynamic dispatch.
   */
  public void moveAnimals() {
    for(int i=0; i<rows; i++) {
      for(int j=0; j<columns; j++) {
        if((map[i][j] instanceof Animal)) {
          if(!((Animal)map[i][j]).hasMoved()) {
            ((Animal)map[i][j]).move(i,j,this,moves);
          }
        }
      }
    } 
  }
  
  /**
   * updateStatus
   * This method loops through the entire map and will update the rest of the statuses of the animals aside from movement.
   * It will drain 1 health from each animal each turn, will update the amounts of each species and print them, will
   * eliminate an animal from the map if it's health is 0, and will terminate the simulation if a species has become extinct.
   */
  public void updateStatus() {
    //Initialize animal counts and update turn count
    wolfCount = 0;
    sheepCount = 0;
    plantCount = 0;
    turnCount++;
    
    //Loops through the map
    for(int i=0; i<rows; i++) {
      for(int j=0; j<columns; j++) {
        
        //If there is an animal on the current square, it sets its moved boolean to false, updates the 
        //count of that species, and drains 1 health from it. If it is a plant it just updates the plant count.
        if((map[i][j] instanceof Animal)) {
          ((Animal)map[i][j]).setMoved(false);
          if(map[i][j] instanceof Wolf) {
            wolfCount++;
          } else {
            sheepCount++;
          }
          map[i][j].setHealth(map[i][j].getHealth()-1);
          if(map[i][j].getHealth() == 0) {
            map[i][j] = null;
          }
        } else if(map[i][j] instanceof Plant) {
          plantCount ++;
        }
      }
    }
    
    //Finds the amount of empty squares
    emptyCount = rows*columns - wolfCount - sheepCount - plantCount;
    
    //Statistics of the current turn and species counts.
    System.out.println("\nNumber of turns elapsed: "+turnCount);
    System.out.println("Current number of plants: " +plantCount);
    System.out.println("Current number of sheep: "+sheepCount);
    System.out.println("Current number of wolves: "+wolfCount);
  }
  
  /**
   * hasExtinction
   * This method checks the amount of each species and returns a boolean that holds true if any species is extinct.
   * @return A boolean that is true if any species is extinct.
   */
  public boolean hasExtinction() {
    if((sheepCount == 0) || (plantCount == 0) || (wolfCount == 0)){ 
      return true;
    }
    return false;
  }
  
  /**
   * createPlants
   * This method creates plants in random squares based on the amount specified, and if there is not enouhg room it will
   * set the amount of plants to be created to be equal to the amount of empty squares to prevent an infinite loop.
   * @param An integer representing the amount of plants to be created.
   */
  public void createPlants(int plantsNeeded) {
    
    //Keeps looping until the amount of new plants spawned (plantsSpawned) is equal to amount amount needed to be spawned (plantsNeeded).
    int plantsSpawned = 0;
    plantsNeeded= Math.min(plantsNeeded,emptyCount);
    while(plantsSpawned<plantsNeeded) {
      int row = (int)(Math.random()*rows);
      int column = (int)(Math.random()*columns);
      if(map[row][column] == null) {
        map[row][column] = new Plant(plantHealth);
        plantsSpawned++;
      }
    }
    emptyCount -= plantsNeeded;
  }
  
  /**
   * createSheep
   * This method creates sheep in random squares based on the amount specified, and works the same way as createPlants but with sheep instead.
   * @param An integer representing the amount of plants to be created.
   */
  public void createSheep(int sheepNeeded) {
    
    //Does same thing as createPlants but the sheep constructor has a parameter for maximum health which is 1.5 times the spawn health.
    int sheepSpawned = 0;
    sheepNeeded = Math.min(sheepNeeded,emptyCount);
    while(sheepSpawned<sheepNeeded) {
      int row = (int)(Math.random()*rows);
      int column = (int)(Math.random()*columns);
      if(map[row][column] == null) {
        map[row][column] = new Sheep(sheepHealth,(int)(sheepHealth*1.5));
        sheepSpawned++;
      }
    }
    emptyCount -= sheepNeeded;
  }
  
  /**
   * createWolves
   * This method creates wolves in random squares based on the amount specified, and works the same way as createSheep but with wolves instead.
   * @param An integer representing the amount of plants to be created.
   */
  public void createWolves(int wolvesNeeded) {
    
    //Does the exact same thing as createSheep including setting maximum health for each wolf to 1.5 times the spawn health.
    int wolvesSpawned = 0;
    wolvesNeeded = Math.min(wolvesNeeded,emptyCount);
    while(wolvesSpawned<wolvesNeeded) {
      int row = (int)(Math.random()*rows);
      int column = (int)(Math.random()*columns);
      if(map[row][column] == null) {
        map[row][column] = new Wolf(wolfHealth,(int)(wolfHealth*1.5));
        wolvesSpawned++;
      }
    }
    emptyCount -= wolvesNeeded;
  }
}