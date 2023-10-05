package edu.curtin.saed.assignment1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application 
{
    public static void main(String[] args) 
    {
        launch();        
    }
    
    @Override
    public void start(Stage stage) 
    {
        stage.setTitle("Army of Robots");
        TextArea logger = new TextArea();
        JFXArena arena = new JFXArena(logger);

        ToolBar toolbar = new ToolBar();
        Label scoreLabel = new Label("Score: 0");
        Label wallLabel = new Label("Walls Queued: 0");
        toolbar.getItems().addAll(scoreLabel);
        toolbar.getItems().addAll(wallLabel);
        
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(arena, logger);
        arena.setMinWidth(300.0);
        
        BorderPane contentPane = new BorderPane();
        contentPane.setTop(toolbar);
        contentPane.setCenter(splitPane);
        
        Scene scene = new Scene(contentPane, 800, 800);
        stage.setScene(scene);
        stage.show();

        ScoreKeeper scoreKeeper = new ScoreKeeper(scoreLabel); 
        GameManager gameManager = new GameManager(arena, scoreKeeper);
        WallKeeper wallKeeper = new WallKeeper(gameManager, wallLabel);
        ScoreProducer scoreProd = new ScoreProducer(scoreKeeper, gameManager, wallKeeper);

        Thread scoreKeeperThread = new Thread(scoreKeeper, "Score Keeper Thread");
        scoreKeeperThread.start();

        Thread scoreProdThread = new Thread(scoreProd, "Score Producer Thread");
        scoreProdThread.start();

        Thread gameManagerThread = new Thread(gameManager, "Game Manager Thread");
        gameManagerThread.start();

        Thread wallKeeperThread = new Thread(wallKeeper, "Wall Keeper Thread");
        wallKeeperThread.start();

        arena.addListener((x, y) ->
        {
            try
            {
                if((x == 4 && y == 4) || (x == 0 && y == 0) || (x == 0 && y == 8) || (x == 8 && y == 0) || (x == 8 && y == 8))
                {
                    System.out.println("Cannot place wall at [" + x + "," + y + "]");
                }
                else
                {
                    wallKeeper.addWall(new Wall(x, y));
                    System.out.println("Trying to place wall at [" + x + "," + y + "]");
                }
            }
            catch(InterruptedException e)
            {
                System.out.println("[App] arena.addListener(): " + e.getMessage());
            }
        });

        // Ending threads prematurely upon GUI closure
        stage.setOnCloseRequest(event ->
        {
            scoreProdThread.interrupt();
            wallKeeperThread.interrupt();
            gameManagerThread.interrupt();
            scoreKeeperThread.interrupt();
        });
    }
}
