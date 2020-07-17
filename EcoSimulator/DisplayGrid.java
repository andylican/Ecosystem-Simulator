/* [DisplayGrid.java]
 * Version 2 (Modified from original)
 * @author Mangat, edited by Andy Li
 * Devember 8, 2018
 * A Small program for Display a 2D String Array graphically
 */

// Graphics Imports
import javax.swing.*;
import java.awt.*;
import javax.imageio.*; 

class DisplayGrid { 

  private JFrame frame;
  private int maxX,maxY, GridToScreenRatio;
  private String[][] world;
  private World sim;
  DisplayGrid(String[][] w) { 
    this.world = w;
    
    maxX = Toolkit.getDefaultToolkit().getScreenSize().width;
    maxY = Toolkit.getDefaultToolkit().getScreenSize().height;
    GridToScreenRatio = maxY / (world.length+1);  //ratio to fit in screen as square map
    
    System.out.println("Map size: "+world.length+" by "+world[0].length + "\nScreen size: "+ maxX +"x"+maxY+ " Ratio: " + GridToScreenRatio);
    
    this.frame = new JFrame("Map of World");
    
    GridAreaPanel worldPanel = new GridAreaPanel();
    
    frame.getContentPane().add(BorderLayout.CENTER, worldPanel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
    frame.setVisible(true);
  }
  
  
  public void refresh() { 
    frame.repaint();
  }
  
  /**
   * update
   * This method takes in a 2D array of strings representing the map of the simulation and makes the world variable of this class equal to it.
   * It also makes the simulation accessible in this class.
   * @param A 2D string array representing the world map in the simulation, and a World representing the simulation.
   */
  public void update(String [][] map, World sim) {
    this.world = map;
    this.sim = sim;
  }
  
  
  class GridAreaPanel extends JPanel {
    
    //Access 4 images from the folder of the project and allowes them to be used for display.
    Image sheep = Toolkit.getDefaultToolkit().getImage("sheep.png");
    Image wolf = Toolkit.getDefaultToolkit().getImage("wolf.png");
    Image grass = Toolkit.getDefaultToolkit().getImage("grass.jpg");
    Image ground = Toolkit.getDefaultToolkit().getImage("ground.jpg");
    public void paintComponent(Graphics g) {        
      
      setDoubleBuffered(true); 
      
      
      g.setColor(Color.WHITE);
      
      for(int i = 0; i<world.length;i=i+1)
      { 
        for(int j = 0; j<world[0].length;j=j+1) 
        { 
          
          //Draws the ground image in the current tile as the background
          g.drawImage(ground,j*GridToScreenRatio,i*GridToScreenRatio,GridToScreenRatio,GridToScreenRatio,this);
          
          //Draws the image based on the number in the world array, 1 representing grass, 2 representing a sheep, and 3 representing a wolf.
          if (world[i][j].equals("1"))    
            g.drawImage(grass,j*GridToScreenRatio,i*GridToScreenRatio,GridToScreenRatio,GridToScreenRatio,this);
          else if (world[i][j].equals("2"))
            g.drawImage(sheep,j*GridToScreenRatio,i*GridToScreenRatio,GridToScreenRatio,GridToScreenRatio,this);
          else if (world[i][j].equals("3"))
            g.drawImage(wolf,j*GridToScreenRatio,i*GridToScreenRatio,GridToScreenRatio,GridToScreenRatio,this);
          
          
          g.drawRect(j*GridToScreenRatio, i*GridToScreenRatio, GridToScreenRatio, GridToScreenRatio);
        }
      }
      
      //This displays the realtime status of the simulation, including the current turn number and the amount of each species.
      g.setColor(Color.BLACK);
      g.setFont(new Font("Arial",Font.PLAIN,30));
      g.drawString("Turn: "+sim.getTurnCount(),1300,100);
      g.drawString("# of plants: "+sim.getPlantCount(),1300,300);
      g.drawString("# of sheep: "+sim.getSheepCount(),1300,500);
      g.drawString("# of wolves: "+sim.getWolfCount(),1300,700);
    }
  }//end of GridAreaPanel
  
} //end of DisplayGrid

