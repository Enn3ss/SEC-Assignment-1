package edu.curtin.saed.assignment1;

public class ScoreProducer implements Runnable // Producer thread
{
    private ScoreKeeper scoreKeeper;
    private GameManager gameManager;
    private WallKeeper wallKeeper;

    public ScoreProducer(ScoreKeeper scoreKeeper, GameManager gameManager, WallKeeper wallKeeper)
    {
        this.scoreKeeper = scoreKeeper;
        this.gameManager = gameManager;
        this.wallKeeper = wallKeeper;
    }

    @Override
    public void run()
    {
        try
        {
            while(gameManager.isGameOver() == false) // While game hasn't ended
            {
                scoreKeeper.addScore(10);
                Thread.sleep(1000); // Increase score by 10 every 1000 ms
            }
            wallKeeper.addPoison(); // Ending WallKeeper thread
        }
        catch(InterruptedException e)
        {
            System.out.println("[ScoreProducer] run(): " + e.getMessage());
        }
    }
}
