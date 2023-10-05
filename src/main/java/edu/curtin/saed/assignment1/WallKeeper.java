package edu.curtin.saed.assignment1;

import java.util.concurrent.*;
import javafx.application.Platform;
import javafx.scene.control.*;

public class WallKeeper implements Runnable // Consumer thread
{
    private BlockingQueue<Wall> queue = new ArrayBlockingQueue<>(10); // 10 walls max
    private static final Wall POISON = new Wall(-1, -1); // Placeable wall will never be [-1,-1]
    private GameManager gameManager;
    private Label label;

    public WallKeeper(GameManager gameManager, Label label)
    {
        this.gameManager = gameManager;
        this.label = label;
    }

    @Override
    public void run()
    {
        boolean running = true;
        try
        {
            while(running)
            {
                Wall newWall = queue.take();

                if(newWall == POISON)
                {
                    running = false;
                }
                else if(gameManager.isWallPlaceable(newWall))
                {
                    printWallsQueued();
                    Thread.sleep(2000);
                }
            }
        }
        catch(InterruptedException e)
        {
            System.out.println("[WallKeeper] run(): " + e.getMessage());
        }
    }

    public void addWall(Wall newWall) throws InterruptedException
    {
        queue.put(newWall);
        printWallsQueued();
    }

    public void addPoison() throws InterruptedException
    {
        queue.put(POISON);
    }

    public void printWallsQueued()
    {
        Platform.runLater(() -> 
        {
            label.setText("Walls Queued: " + queue.size());
        });
    }
}
