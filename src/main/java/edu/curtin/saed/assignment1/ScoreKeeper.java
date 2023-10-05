package edu.curtin.saed.assignment1;

import java.util.concurrent.*;
import javafx.application.Platform;
import javafx.scene.control.*;

public class ScoreKeeper implements Runnable // Consumer thread
{
    // Queue is size 11 because per second, score can be increased 1x by 10 and 10x by at most 10 walls being broken at once
    private BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(11);
    private static final Integer POISON = Integer.valueOf(-1); // Score can never be -1
    private int score;
    private Label label;

    public ScoreKeeper(Label label)
    {
        score = 0;
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
                Integer addedScore = queue.take();

                if(addedScore == POISON)
                {
                    running = false; // Ends current thread
                }
                else
                {
                    score += addedScore.intValue();
                    printScore(); // Update score to GUI
                }
            }
        }
        catch(InterruptedException e)
        {
            System.out.println("[ScoreKeeper] run(): " + e.getMessage());
        }
    }

    public void printScore()
    {
        Platform.runLater(() -> 
        {
            label.setText("Score: " + score); // Prints score to GUI
        });
    }

    public void addScore(int num) throws InterruptedException
    {
        queue.put(Integer.valueOf(num));
    }

    public void addPoison() throws InterruptedException
    {
        queue.put(POISON);
    }
}
