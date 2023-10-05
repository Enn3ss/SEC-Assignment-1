package edu.curtin.saed.assignment1;

import java.util.concurrent.*;
import javafx.application.Platform;

public class GameManager implements Runnable
{
    private ExecutorService es = Executors.newCachedThreadPool(); // Unlimited threads
    private JFXArena arena;
    private ScoreKeeper scoreKeeper;
    private Object mutex = new Object();
    private boolean gameOver = false;   

    public GameManager(JFXArena arena, ScoreKeeper scoreKeeper)
    {
    	this.arena = arena;
        this.scoreKeeper = scoreKeeper;
    }

    @Override
    public void run()
    {
        try
        {
            while(!gameOver)
            {
                spawnRobot(); 
                Thread.sleep(1500); // Attemps to spawn a new robot every 1500 ms
            }
        }
        catch(InterruptedException e)
        {
            gameOver = true;
            System.out.println("[GameManager] run(): " + e.getMessage());
        }
        es.shutdown(); // Shutdown ExecutorService
    }

    public void spawnRobot() // Submits a task to spawn a robot and instruct it to move
    {        
        es.submit(() ->
        {
            boolean isAlive;
            Robot newRobot = makeRobot(getRandomStartXY(), getRandomStartXY());

            if(newRobot != null)
            {
                isAlive = true;
                while(isAlive && !gameOver) // While robot is alive and not game over
                {
                    isAlive = moveRobot(newRobot);
                }
            }
        });
    }

    public Robot makeRobot(int x, int y)
    {
        synchronized(mutex) // Prevents robots from spawning on the same corner
        {
            if(!arena.checkForRobot(x, y))
            {
                Robot newRobot = new Robot(x, y);
                arena.addRobot(newRobot); // If grid square is unoccupied, add to list of robots in JFXArena
                return newRobot;
            }
        }
        return null;
    }

    public boolean moveRobot(Robot newRobot)
    {
        int random = ThreadLocalRandom.current().nextInt(1, 5);
        boolean isAlive = true;

        try
        {
            Thread.sleep(newRobot.getDelay()); // Robots can only attempt a move every 'd' ms (delay)
            switch(random) // Robots have an equal chance of moving up, down, left, or right regardless of position on the map
            {
                case 1:
                isAlive = moveUp(newRobot);
                break;
                
                case 2:
                isAlive = moveLeft(newRobot);
                break;

                case 3:
                isAlive = moveRight(newRobot);
                break;

                case 4:
                isAlive = moveDown(newRobot);
                break;

                default:
                break;
            }
        }
        catch(InterruptedException e)
        {
            System.out.println("[GameManager] moveRobot(): " + e.getMessage());
        }

        return isAlive;
    }

    public boolean moveUp(Robot newRobot) // Move robot up
    {
        int x = newRobot.getX();
        int y = newRobot.getY();
        boolean isAlive = true;

        try
        {
            if(!doesRobotExist(newRobot, x, y - 1) && y > 0) // Check if move is possible
            {
                for(int i = 0; i < 10; i++)
                {
                    newRobot.decreaseYPos(); // Decrease robot's 'y' value by 0.1
                    Platform.runLater(() ->
                    {
                        arena.requestLayout(); // Update GUI
                    });
                    Thread.sleep(40);
                }
                newRobot.setY(y - 1); // Update robot's y coordinate
                gameOverCheck(x, y - 1); // Check if robot reached citadel

                if(doesWallExist(newRobot, x, y - 1)) // Check if robot moved into a wall
                {
                    scoreKeeper.addScore(100);
                    isAlive = false;
                }
            }
        }
        catch(InterruptedException e)
        {
            System.out.println("[GameManager] moveUp(): " + e.getMessage());
        }

        return isAlive;
    }

    public boolean moveLeft(Robot newRobot) // Move robot left
    {
        int x = newRobot.getX();
        int y = newRobot.getY();
        boolean isAlive = true;

        try
        {
            if(!doesRobotExist(newRobot, x - 1, y) && x > 0) // Check if move is possible
            {
                for(int i = 0; i < 10; i++)
                {
                    newRobot.decreaseXPos(); // Decrease robot's 'x' value by 0.1
                    Platform.runLater(() ->
                    {
                        arena.requestLayout(); // Update GUI
                    });
                    Thread.sleep(40);
                }
                newRobot.setX(x - 1); // Update robot's x coordinate
                gameOverCheck(x - 1, y); // Check if robot reached citadel

                if(doesWallExist(newRobot, x - 1, y)) // Check if robot moved into a wall
                {
                    scoreKeeper.addScore(100);
                    isAlive = false;
                }
            }
        }
        catch(InterruptedException e)
        {
            System.out.println("[GameManager] moveLeft(): " + e.getMessage());
        }

        return isAlive;
    }

    public boolean moveRight(Robot newRobot) // Move robot right
    {
        int x = newRobot.getX();
        int y = newRobot.getY();
        boolean isAlive = true;

        try
        {
            if(!doesRobotExist(newRobot, x + 1, y) && x < 8) // Check if move is possible
            {
                for(int i = 0; i < 10; i++)
                {
                    newRobot.increaseXPos(); // Increase robot's 'x' value by 0.1
                    Platform.runLater(() ->
                    {
                        arena.requestLayout(); // Update GUI
                    });
                    Thread.sleep(40);
                }
                newRobot.setX(x + 1); // Update robot's x coordinate
                gameOverCheck(x + 1, y); // Check if robot reached citadel

                if(doesWallExist(newRobot, x + 1, y)) // Check if robot moved into a wall
                {
                    scoreKeeper.addScore(100);                    
                    isAlive = false;
                }
            }
        }
        catch(InterruptedException e)
        {
            System.out.println("[GameManager] moveRight(): " + e.getMessage());
        }

        return isAlive;
    }

    public boolean moveDown(Robot newRobot) // Move robot down
    {
        int x = newRobot.getX();
        int y = newRobot.getY();
        boolean isAlive = true;

        try
        {
            if(!doesRobotExist(newRobot, x, y + 1) && y < 8) // Check if move is possible
            {
                for(int i = 0; i < 10; i++)
                {
                    newRobot.increaseYPos(); // Increase robot's 'y' value by 0.1
                    Platform.runLater(() ->
                    {
                        arena.requestLayout(); // Update GUI
                    });
                    Thread.sleep(40);
                }
                newRobot.setY(y + 1); // Update robot's 'y' coordinate
                gameOverCheck(x, y + 1); // Check if robot reached citadel

                if(doesWallExist(newRobot, x, y + 1)) // Check if robot moved into a wall
                {
                    scoreKeeper.addScore(100);
                    isAlive = false;
                }
            }
        }
        catch(InterruptedException e)
        {
            System.out.println("[GameManager] moveDown(): " + e.getMessage());
        }

        return isAlive;
    }

    public boolean doesRobotExist(Robot robot, int x, int y)
    {
        synchronized(mutex) // Prevents robots from moving into the same grid square
        {
            if(arena.checkForRobot(x, y) == false)
            {
                robot.setNextX(x);
                robot.setNextY(y);
                return false;
            }
            else
            {
                return true;
            }
        }
    } 

    public boolean doesWallExist(Robot robot, int x, int y)
    {
        if(arena.checkForWall(x, y, true))
        {
            return arena.removeRobot(robot); // Return true if wall exists and the robot runs into it (gets removed from list)
        }
        return false;
    }

    public boolean isWallPlaceable(Wall newWall)
    {
        synchronized(mutex) // Prevents walls being placed in an occupied grid square (i.e. occupied by another wall or robot)
        {
            if((arena.checkForRobot(newWall.getX(), newWall.getY()) == false) && (arena.checkForWall(newWall.getX(), newWall.getY(), false) == false))
            {
                arena.addWall(newWall); // If unoccupied, add to list of walls in JFXArena
                return true;
            }
        }
        return false;
    }

    public void gameOverCheck(int x, int y) throws InterruptedException
    {
        if(x == 4 && y == 4) // Citadel coordinates
        {
            gameOver = true;
            scoreKeeper.addPoison(); // Ends ScoreKeeper thread
            System.out.println("Game Over Nerd");
        }
    }

    public boolean isGameOver()
    {
        return gameOver;
    }

    public int getRandomStartXY()
    {
        int random = ThreadLocalRandom.current().nextInt(1, 3);
        int xy = 0;

        switch(random)
        {
            case 1:
            xy = 0;
            break;

            case 2:
            xy = 8;
            break;

            default:
            break;
        }

        return xy;
    }
}
