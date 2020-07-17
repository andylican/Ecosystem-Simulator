/**
 * EcoSimulator.java
 * Version 1
 * @author Andy Li
 * December 8, 2018
 * A program that simulates an ecosystem containing wolves, sheep, and plants, and uses many features
 * to make it as realistic and accurate as possible. This class contains the main method.
 * A list of additional features aside from the minimum requirements:
 * -Path finding algorithms for wolves and sheep: they will try to seek food and will look for their own species to reproduce instead of moving randomly. This algorithm performs a breadth first search.
 * -Maximum health: to prevent infinitely increasing health, each animal has a maximun health which is 1.5 times their normal health.
 * -Wolf reproduction: wolves can also reproduce just like sheep, so if 2 wolves collide, they reproduce of the conditions are met (both have over 20 health) but otherwise, they will duel.
 * -Images: used images instead of the coloured squares.
 * -Baby health set to 10 instead of 20: Since babies should be weaker and shouldn't be able to reproduce right after they are born, they start with 10 health and must eat to gain health before they can reproduce.
 * -Updated display on screen: The current number of each species and the current turn is constrantly updated on the dsplay screen, and also constantly printing on the console.
 * -Pause feature: Since the only way to exit the game is to press the x button, I set the simulation to pause at certain intervals to allow the user to review the statistics and also give them the option to terminate the simulation.
 * -Wandering: When any animal is not pursuiting a target, they move randomly, but I made it so that they cannot go to the square they just came from to prevent the animals from waddling in one place.
 * -No space on the board: If there are more species to be spawned than places on the board, instead of running infinitely trying to find an empty square, my program will recognize that and just fill up the board compeltely and will not spawn any more species.
 */

import java.util.Scanner;
class EcoSimulator {
  
  //Initlialize all global variables
  static int rows;
  static int columns;
  static Scanner input;
  
  /**
   * main
   * This method gets the input, calls the other methods to move the animals and display results, and ends the simulation
   * @param String [] args which is the code you type inside it.
   */
  public static void main (String [] args) {
    int plants;
    int plantHealth;
    int plantSpawn;
    int sheep;
    int sheepHealth;
    int wolves;
    int wolfHealth;
    int delay;
    int pauseInterval;
    boolean terminateSimulation = false;
    
    //Greeting message
    input = new Scanner(System.in);
    System.out.println("Welcome to my Ecosystem Simulator!");
    System.out.println("Project created by Andy Li");
    
    /*
     Reccomanded case for maximum sustainablilty and acurracy (can copy all the input numbers in one go except for the last 2 and paste it when running the program to save time).
     <Select and copy starting from the next line>
     50
     50
     200
     5
     25
     50
     30
     5
     30
     <Select until here>
   */
    
    //Obtains the user input
    System.out.println("\nHow many rows does the map have? (Reccomended: 50)");
    rows = input.nextInt();
    System.out.println("\nHow many columns does the map have? (Reccomended: 50)");
    columns = input.nextInt();
    System.out.println("\nHow many plants does the map have? (Reccomended: 200)");
    plants = input.nextInt();
    System.out.println("\nWhat is the health of each plant? (Reccomended: 5)");
    plantHealth = input.nextInt();
    System.out.println("\nHow many plants will spawn after every turn? (Reccomended: 25)");
    plantSpawn = input.nextInt();
    System.out.println("\nHow many sheep does the map have? (Reccomended: 50)");
    sheep = input.nextInt();
    System.out.println("\nWhat is the health of each sheep? (Reccomended: 30)");
    sheepHealth = input.nextInt();
    System.out.println("\nHow many wolves does the map have? (Reccomended: 5)");
    wolves = input.nextInt();
    System.out.println("\nWhat is the health of each wolf? (Reccomended: 30)");
    wolfHealth = input.nextInt();
    System.out.println("\nWhat is the delay between each time the screen is updated (in milliseconds)? (Reccomended: 100 for fast simulation over long period of time, 500 for slower simulation and ease of keeping track of animal behaviours)");
    System.out.println("\nNote that the program will take much longer if running a large map than a small map so adjust the sleep times accordingly when the map size is changed.");
    delay = input.nextInt();
    System.out.println("\nAfter how many turns (every certain amount of turns) would you like the simulation to pause and let you view the current statistics of the simulation?");
    pauseInterval = input.nextInt();
    
    //To prevent next line blues from later input
    input.nextLine();
    
    //Creates an instance of an object with class World, which contains the map and all the animals and their information/values
    World sim = new World(rows,columns,plants,sheep,wolves,plantHealth,sheepHealth,wolfHealth);
    
    //Displays the initial setup of the map before any animal moves
    String[][] numMap = convertToDisplay(sim.getMap());
    DisplayGrid d = new DisplayGrid(numMap);
    d.update(numMap,sim);
    d.refresh();
    try{ 
      Thread.sleep(2000);
    } catch(Exception e) {
    }
    
    //A while loop to continually call the methods to move the animals, spawn the plants, and update the display until the simulation is terminated
    while(!terminateSimulation) {
      
      //Moves the animals
       sim.moveAnimals();
       
       //Converts the object map of the world to an array of strings which can be used to update the screen
       numMap = convertToDisplay(sim.getMap());
       d.update(numMap,sim);
       d.refresh();
       
       //Updates the status of all plants and animals
       sim.updateStatus();
       
       //If the simulation needs to be terminated, the while loop will be exited
       if(sim.hasExtinction()) {
         terminateSimulation = endSimulation();
       }
       if((sim.getTurnCount() % pauseInterval) == 0) {
         terminateSimulation = pauseSimulation();
       }
       
       //Delay after the display and then spawns the plants for the next turn
       try{ 
         Thread.sleep(delay);
       } catch(Exception e) {
       }
       sim.createPlants(plantSpawn);
        
    }
    
    //Simulation ending message and then the program terminates and closes the simulator
    System.out.println("\nSimulation will terminate shortly.\nThank you for trying out my ecosystem simulator!\nProject created by Andy Li");
    try{ 
      Thread.sleep(2000);
    } catch(Exception e) {
    }
    System.exit(0);
  }
  
  /**
   * convertToDisplay
   * This method accepts a Life array (world map) as a parameter and will convert it
   * into a 2D array of strings in order to be used by the DisplayGrid class.
   * @param A 2D object array of class Life, representing the map of the world and its inhabitants.
   * @return A 2D array of strings containing numbers from 0 to 3 in order to be used by the DisplayGrid class
   * to update the screen display.
   */
  public static String[][] convertToDisplay(Life[][] objectMap) {
    String[][] numMap = new String[rows][columns];
    for(int i=0; i<rows; i++) {
      for(int j=0; j<columns; j++) {
        if(objectMap[i][j] instanceof Plant) {
          numMap[i][j] = 1+"";
        } else if(objectMap[i][j] instanceof Sheep) {
          numMap[i][j] = 2+"";
        } else if(objectMap[i][j] instanceof Wolf) {
          numMap[i][j] = 3+"";
        } else {
          numMap[i][j] = 0+"";
        }
      }
    }
    return numMap;
  }
  
  /**
   * endSimulation
   * This method is called when a species has become extinct, and allows the user to review the
   * statistics of the simluation from the beginning to the end until they want to close the simulation.
   * @return A boolean that if it is true, it will exit the while loop in the main method that keeps
   * the simulation running which will lead to the 3 lines of code that closes the simulation.
   */
  public static boolean endSimulation() {
    System.out.println("\nThe simualtion has ended due to extinction of a species. Take your time to view the simulation statistics and type \"end\" to exit the simulaion.");
    String end = input.nextLine();
    while(!end.toLowerCase().equals("end")) {
      System.out.println("Please type \"end\" when you want to close the simulation.");
      end = input.nextLine();
    }
    return true;
  }
  
  /**
   * pauseSimulation
   * This method is called periodically to give the user a chance to review the current statistics 
   * of the simulation and gives them the opportunity to end the simulation.
   * @return A boolean that if it is true, it will exit the while loop in the main method that keeps
   * the simulation running which will lead to the 3 lines of code that closes the simulation.
   */
  public static boolean pauseSimulation() {
    System.out.println("\nThe simulation has been paused. Take your time to view the simulation statistics and type \"run\" to continue the simulation or \"stop\" to exit the simulaion.");
    String userAction = input.nextLine();
    if(userAction.toLowerCase().equals("stop")) {
      return true;
    }
    return false;
  }

}