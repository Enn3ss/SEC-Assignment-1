package edu.curtin.saed.assignment1;

public class Wall
{ 
    private String state; // Wall or Weak
    private int x;
    private int y;

    public Wall(int x, int y)
    {
        state = "Wall";
        this.x = x;
        this.y = y;
    }

    public String getState() { return state; }
    public int getX() { return x; }
    public int getY() { return y; }

    public void setState(String state) { this.state = state; }
}
