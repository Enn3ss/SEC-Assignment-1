package edu.curtin.saed.assignment1;

import java.io.*;
import javafx.scene.image.Image;

@SuppressWarnings("PMD.UnusedAssignment")
public class ImageLoader
{
    private static final String ROBOT_PNG = "1554047213.png";
    //private static final String DROID_PNG = "droid2.png";
    private static final String WALL_PNG = "181478.png";
    private static final String BROKEN_WALL_PNG = "181479.png";
    private static final String CROSS_PNG = "cross.png";
    private static final String CITADEL_PNG = "rg1024-isometric-tower.png";
    
    private Image robot = null;
    private Image wall = null;
    private Image brokenWall = null;
    private Image cross = null;
    private Image citadel = null;

    public Image loadImage(String imageStr)
    {
        Image image = null;

    	try(InputStream is = getClass().getClassLoader().getResourceAsStream(imageStr))
    	{
    		if(is == null)
    		{
    			throw new AssertionError("Cannot find image file " + imageStr);
            }
            image = new Image(is); // Suppressing warning; [image] value is used
        }
        catch(IOException e)
        {
            throw new AssertionError("Cannot load image file " + imageStr, e);
        }

        return image;
    }

    public Image getRobot()
    {
        if(robot == null)
        {
            robot = loadImage(ROBOT_PNG);
        }
        return robot;
    }

    public Image getWall()
    {
        if(wall == null)
        {
            wall = loadImage(WALL_PNG);
        }
        return wall;
    }

    public Image getBrokenWall()
    {
        if(brokenWall == null)
        {
            brokenWall = loadImage(BROKEN_WALL_PNG);
        }
        return brokenWall;
    }

    public Image getCross()
    {
        if(cross == null)
        {
            cross = loadImage(CROSS_PNG);
        }
        return cross;
    }

    public Image getCitadel()
    {
        if(citadel == null)
        {
            citadel = loadImage(CITADEL_PNG);       
        }
        return citadel;
    }
}        
