package edu.curtin.saed.assignment1;

import javafx.scene.canvas.*;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import java.util.*;

/**
 * A JavaFX GUI element that displays a grid on which you can draw images, text and lines.
 */
public class JFXArena extends Pane
{
    private Image robotImage;
    private Image wallImage;
    private Image brokenWallImage;
    private Image citadelImage;
 
    private int gridWidth = 9;
    private int gridHeight = 9;

    private double gridSquareSize; // Auto-calculated
    private Canvas canvas; // Used to provide a 'drawing surface'.
    private TextArea logger;

    private List<ArenaListener> listeners = null;
    private List<Robot> robotsList = new LinkedList<>(); // Shared resource
    private List<Wall> wallList = new LinkedList<>(); // Shared resrouce

    /**
     * Creates a new arena object, loading the robot image and initialising a drawing surface.
     */
    public JFXArena(TextArea logger)
    {
        ImageLoader imageLoader = new ImageLoader();
        robotImage = imageLoader.getRobot();
        wallImage = imageLoader.getWall();
        brokenWallImage = imageLoader.getBrokenWall();
        citadelImage = imageLoader.getCitadel();

        this.logger = logger;
        canvas = new Canvas();
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        getChildren().add(canvas);
    }
    
    public void addRobot(Robot newRobot)
    {
        robotsList.add(newRobot);
        requestLayout();
        logger.appendText("Robot " + newRobot.getId() + " spawned at [" + newRobot.getX() + "," + newRobot.getY() + "]\n");
    }

    public void addWall(Wall newWall)
    {
        if(wallList.size() < 10) // Maximum of 10 walls can be built
        {
            wallList.add(newWall);
            requestLayout();
            logger.appendText("Wall built at [" + newWall.getX() + "," + newWall.getY() + "]\n");
        }
    }

    public boolean checkForRobot(int x, int y)
    {
        for(Robot robot : robotsList)
        {
            if((robot.getX() == x && robot.getY() == y) || (robot.getNextX() == x && robot.getNextY() == y))
            {
                return true; // Return true if robot exists at [x,y]
            }
        }
        return false; // Return false if there is no robot at [x,y]
    }

   /** 
    * Checks for walls. Can be called by robot or player. 
    * isCheckForRobot = true is for robots
    * isCheckForRobot = false is for player trying to place a wall
    */
    public boolean checkForWall(int x, int y, boolean isCheckForRobot)
    {
        for(Wall wall : wallList)
        {
            if(wall.getX() == x && wall.getY() == y)
            {
                if(isCheckForRobot == true) // If checkForWall() is called by a robot
                {
                    if(wall.getState().equals("Wall"))
                    {
                        wall.setState("Weak");
                        logger.appendText("Wall at [" + wall.getX() + "," + wall.getY() + "] has been weakened\n");
                    }
                    else if(wall.getState().equals("Weak"))
                    {
                        wallList.remove(wall);
                        logger.appendText("Wall at [" + wall.getX() + "," + wall.getY() + "] has been destroyed\n");
                    }
                }
                return true; // Return true if wall exists at [x,y]
            }
        }
        return false; // Return false if there is no wall at [x,y]
    }

    public boolean removeRobot(Robot robot)
    {
        return robotsList.remove(robot);
    }
   
    /**
     * Adds a callback for when the user clicks on a grid square within the arena. The callback 
     * (of type ArenaListener) receives the grid (x,y) coordinates as parameters to the 
     * 'squareClicked()' method.
     */
    public void addListener(ArenaListener newListener)
    {
        if(listeners == null)
        {
            listeners = new LinkedList<>();
            setOnMouseClicked(event ->
            {
                int gridX = (int)(event.getX() / gridSquareSize);
                int gridY = (int)(event.getY() / gridSquareSize);
                
                if(gridX < gridWidth && gridY < gridHeight)
                {
                    for(ArenaListener listener : listeners)
                    {   
                        listener.squareClicked(gridX, gridY);
                    }
                }
            });
        }
        listeners.add(newListener);
    }
        
        
    /**
     * This method is called in order to redraw the screen, either because the user is manipulating 
     * the window, OR because you've called 'requestLayout()'.
     *
     * You will need to modify the last part of this method; specifically the sequence of calls to
     * the other 'draw...()' methods. You shouldn't need to modify anything else about it.
     */
    @Override
    public  void layoutChildren()
    {
        super.layoutChildren(); 
        GraphicsContext gfx = canvas.getGraphicsContext2D();
        gfx.clearRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
        
        // First, calculate how big each grid cell should be, in pixels. (We do need to do this
        // every time we repaint the arena, because the size can change.)
        gridSquareSize = Math.min(
            getWidth() / (double) gridWidth,
            getHeight() / (double) gridHeight);
            
        double arenaPixelWidth = gridWidth * gridSquareSize;
        double arenaPixelHeight = gridHeight * gridSquareSize;
            
            
        // Draw the arena grid lines. This may help for debugging purposes, and just generally
        // to see what's going on.
        gfx.setStroke(Color.DARKGREY);
        gfx.strokeRect(0.0, 0.0, arenaPixelWidth - 1.0, arenaPixelHeight - 1.0); // Outer edge

        for(int gridX = 1; gridX < gridWidth; gridX++) // Internal vertical grid lines
        {
            double x = (double) gridX * gridSquareSize;
            gfx.strokeLine(x, 0.0, x, arenaPixelHeight);
        }
        
        for(int gridY = 1; gridY < gridHeight; gridY++) // Internal horizontal grid lines
        {
            double y = (double) gridY * gridSquareSize;
            gfx.strokeLine(0.0, y, arenaPixelWidth, y);
        }

        // Invoke helper methods to draw things at the current location.
        // ** You will need to adapt this to the requirements of your application. **
        drawImage(gfx, citadelImage, 4, 4);
        drawLabel(gfx, "Citadel", 4, 4);

        for(Robot robot : robotsList)
        {
            drawImage(gfx, robotImage, robot.getXPos(), robot.getYPos());
            drawLabel(gfx, String.valueOf(robot.getId()), robot.getXPos(), robot.getYPos());
        };

        for(Wall wall : wallList)
        {
            if(wall.getState().equals("Wall"))
            {
                drawImage(gfx, wallImage, wall.getX(), wall.getY());
                drawLabel(gfx, wall.getState(), wall.getX(), wall.getY());
            }
            else if(wall.getState().equals("Weak"))
            {
                drawImage(gfx, brokenWallImage, wall.getX(), wall.getY());
                drawLabel(gfx, wall.getState(), wall.getX(), wall.getY());
            }
        }
    }
    
    
    /** 
     * Draw an image in a specific grid location. *Only* call this from within layoutChildren(). 
     *
     * Note that the grid location can be fractional, so that (for instance), you can draw an image 
     * at location (3.5,4), and it will appear on the boundary between grid cells (3,4) and (4,4).
     *     
     * You shouldn't need to modify this method.
     */
    private void drawImage(GraphicsContext gfx, Image image, double gridX, double gridY)
    {
        // Get the pixel coordinates representing the centre of where the image is to be drawn. 
        double x = (gridX + 0.5) * gridSquareSize;
        double y = (gridY + 0.5) * gridSquareSize;
        
        // We also need to know how "big" to make the image. The image file has a natural width 
        // and height, but that's not necessarily the size we want to draw it on the screen. We 
        // do, however, want to preserve its aspect ratio.
        double fullSizePixelWidth = robotImage.getWidth();
        double fullSizePixelHeight = robotImage.getHeight();
        
        double displayedPixelWidth, displayedPixelHeight;
        if(fullSizePixelWidth > fullSizePixelHeight)
        {
            // Here, the image is wider than it is high, so we'll display it such that it's as 
            // wide as a full grid cell, and the height will be set to preserve the aspect 
            // ratio.
            displayedPixelWidth = gridSquareSize;
            displayedPixelHeight = gridSquareSize * fullSizePixelHeight / fullSizePixelWidth;
        }
        else
        {
            // Otherwise, it's the other way around -- full height, and width is set to 
            // preserve the aspect ratio.
            displayedPixelHeight = gridSquareSize;
            displayedPixelWidth = gridSquareSize * fullSizePixelWidth / fullSizePixelHeight;
        }

        // Actually put the image on the screen.
        gfx.drawImage(image,
            x - displayedPixelWidth / 2.0,  // Top-left pixel coordinates.
            y - displayedPixelHeight / 2.0, 
            displayedPixelWidth,              // Size of displayed image.
            displayedPixelHeight);
    }
    
    
    /**
     * Displays a string of text underneath a specific grid location. *Only* call this from within 
     * layoutChildren(). 
     *     
     * You shouldn't need to modify this method.
     */
    private void drawLabel(GraphicsContext gfx, String label, double gridX, double gridY)
    {
        gfx.setTextAlign(TextAlignment.CENTER);
        gfx.setTextBaseline(VPos.TOP);
        gfx.setStroke(Color.BLUE);
        gfx.strokeText(label, (gridX + 0.5) * gridSquareSize, (gridY + 1.0) * gridSquareSize);
    }
}
