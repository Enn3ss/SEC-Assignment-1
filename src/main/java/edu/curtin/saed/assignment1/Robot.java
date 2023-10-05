package edu.curtin.saed.assignment1;

public class Robot
{
    private static int total = 0; // Used to keep track of unique Id
    private int uniqueId;
    private int delay;
    private int x;
    private int y;
    private int nextX;
    private int nextY;
    private double xPos; // Used to print robot's 'x' changes to GUI
    private double yPos; // Used to print robot's 'y' changes to GUI

    public Robot(int x, int y)
    {
        total++;
        this.uniqueId = total;
        this.delay = (int)Math.floor(Math.random() * (2000 - 500 + 1) + 500);
        this.x = x;
        this.y = y;
        nextX = x;
        nextY = y;
        xPos = x;
        yPos = y;
   }

    public int getId() { return uniqueId; }
    public int getDelay() { return delay; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getNextX() { return nextX; }
    public int getNextY() { return nextY; }
    public double getXPos() { return xPos; }
    public double getYPos() { return yPos; }
    
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setNextX(int nextX) { this.nextX = nextX; }
    public void setNextY(int nextY) { this.nextY = nextY; }

    public void increaseXPos() { xPos = xPos + 0.1; }
    public void increaseYPos() { yPos = yPos + 0.1; }
    
    public void decreaseXPos() { xPos = xPos - 0.1; }
    public void decreaseYPos() { yPos = yPos - 0.1; }

    public void setXPos(double xPos) { this.xPos = xPos; }
    public void setYPos(double yPos) { this.yPos = yPos; }
}
