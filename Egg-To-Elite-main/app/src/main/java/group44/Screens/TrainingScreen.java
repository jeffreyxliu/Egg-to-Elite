package group44.Screens;

import group44.Pet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import group44.App;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * The TrainingScreen class serves as a base class for all mini-games 
 * that provide training for the {@link group44.Pet}. 
 * It sets up common UI elements (canvas, HUD, overlay) and 
 * methods needed by subclasses such as checking collisions with coins,
 * drawing coins, managing the game loop, etc.
 */
public class TrainingScreen extends ScreenClass {

    /** The default width of the game canvas. */
    protected double canvasWidth = 800;
    /** The default height of the game canvas. */
    protected double canvasHeight = 600;
    /** The size in pixels for rendered coins. */
    protected int coinSize = 25;

    /** A Label for displaying the current score. */
    protected Label scoreLabel;
    /** A Label for displaying how many coins have been collected. */
    protected Label coinCountLabel;

    /**
     * Flag indicating whether the training screen is a tutorial. If true,
     * skill or coin rewards are not applied, and game difficulty may be lowered.
     */
    protected boolean isTutorialScreen;

    /** A Canvas on which the mini-game is drawn. */
    protected Canvas gameCanvas;
    /** The GraphicsContext of {@link #gameCanvas}, used for rendering. */
    protected GraphicsContext gc;
    /** A counter controlling how often coins appear. */
    protected int coinCounter = 0;
    /** The total number of coins collected during the mini-game. */
    protected int coinsCollected = 0;
    /** The current score of the mini-game instance. */
    protected int score;
    /** The amount of skill the {@link group44.Pet} gains on this screen. */
    protected int skillGain;
    /** A list holding all uncollected {@link Coin} objects in the game. */
    protected List<Coin> coins;
    /** A shared random generator for spawn positions and events. */
    protected Random random;

    /** The Y-position of the pet on the canvas. */
    protected double petY;
    /** The X-position of the pet on the canvas. */
    protected double petX;
    /** The width of the pet's bounding box for collision. */
    protected double petWidth;
    /** The height of the pet's bounding box for collision. */
    protected double petHeight;

    /** The main animation loop driving the mini-game's updates and rendering. */
    protected AnimationTimer gameLoop;

    /** Whether the mini-game is actively running. */
    protected boolean isGameRunning;
    /** Whether the mini-game has ended. */
    protected boolean isGameOver;

    /**
     * Constructs a TrainingScreen.
     * 
     * @param pet             The {@link group44.Pet} associated with this screen.
     * @param isTutorialScreen True if no rewards or skill gains should be applied.
     */
    public TrainingScreen(Pet pet, boolean isTutorialScreen) {
        this.pet = pet;
        this.isTutorialScreen = isTutorialScreen;
        
        // Modify the pet's stats if this is not a tutorial screen
        if (!isTutorialScreen) {
            pet.setSleepiness(pet.getSleepiness() - 10);
            pet.setHunger(pet.getHunger() - 10);
            pet.setHealth(pet.getHealth() + 10);
        }
    }

    /**
     * Checks whether the given coin intersects with the pet's bounding box.
     * 
     * @param coin The coin to check collision for.
     * @return true if the pet's bounding box overlaps the coin, false otherwise.
     */
    protected boolean checkCoinCollision(Coin coin) {
        double petLeft = petX - petWidth / 2 + 5;
        double petRight = petX + petWidth / 2 - 5;
        double petTop = petY + 5;
        double petBottom = petY + petHeight - 5;

        double coinLeft = coin.x;
        double coinRight = coin.x + coinSize;
        double coinTop = coin.y;
        double coinBottom = coin.y + coinSize;

        return (petRight > coinLeft && petLeft < coinRight &&
                petBottom > coinTop && petTop < coinBottom);
    }

    /**
     * Ends the mini-game, stops the game loop, and returns control to the main game screen.
     */
    protected void returnToGame() {
        gameLoop.stop();
        GameScreen gameScreen = new GameScreen(pet);
        App.setScreen(gameScreen);
    }

    /**
     * A static inner class representing a coin placed on the game canvas.
     * The coin can be collected if the pet collides with it.
     */
    protected static class Coin {
        /** The coin's X-position. */
        double x;
        /** The coin's Y-position. */
        double y;
        /** Whether the coin has been collected by the pet. */
        boolean collected;

        /**
         * Creates a Coin with the specified coordinates.
         *
         * @param x The x-coordinate of the coin.
         * @param y The y-coordinate of the coin.
         */
        Coin(double x, double y) {
            this.x = x;
            this.y = y;
            this.collected = false;
        }
    }

    /**
     * Sets up the top HUD, including instructions and stats such as score and coins.
     *
     * @param userControls A brief text displaying which keys or actions control the game.
     */
    protected void setupHUD(String userControls) {
        VBox topContainer = new VBox(10 * ratio);
        topContainer.setAlignment(Pos.CENTER);

        Label instructionsLabel = new Label(userControls);
        instructionsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16 * ratio));
        instructionsLabel.setTextFill(Color.DARKBLUE);

        HBox statsBox = new HBox(40 * ratio);
        statsBox.setAlignment(Pos.CENTER);

        scoreLabel = new Label("0");
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24 * ratio));
        scoreLabel.setTextFill(Color.DARKBLUE);

        Label scoreTitle = new Label("SCORE");
        scoreTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14 * ratio));
        scoreTitle.setTextFill(Color.DARKBLUE);

        VBox scoreContainer = new VBox(5 * ratio, scoreTitle, scoreLabel);
        scoreContainer.setAlignment(Pos.CENTER);

        coinCountLabel = new Label("0");
        coinCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24 * ratio));
        coinCountLabel.setTextFill(Color.DARKBLUE);

        Label coinTitle = new Label("COINS");
        coinTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14 * ratio));
        coinTitle.setTextFill(Color.DARKBLUE);

        VBox coinContainer = new VBox(5 * ratio, coinTitle, coinCountLabel);
        coinContainer.setAlignment(Pos.CENTER);

        statsBox.getChildren().addAll(scoreContainer, coinContainer);
        topContainer.getChildren().addAll(instructionsLabel, statsBox);

        root.setTop(topContainer);
    }

    /**
     * Adds a bottom row of buttons, including a restart button and a button 
     * to return to the main game screen.
     */
    protected void setupBottomButtons() {
        Button restartButton = createStyledButton("Restart");
        Button backButton = createStyledButton("Back to Game");

        restartButton.setFocusTraversable(false);
        backButton.setFocusTraversable(false);

        restartButton.setOnAction(e -> restartGame());
        backButton.setOnAction(e -> returnToGame());

        HBox buttonBox = new HBox(20 * ratio);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15 * ratio, 0, 0, 0));
        buttonBox.getChildren().addAll(backButton, restartButton);

        root.setBottom(buttonBox);
    }

    /**
     * Restarts the mini-game from a game-over state by calling {@link #startGame()}.
     */
    protected void restartGame() {
        if (isGameOver) {
            startGame();
        }
    }

    /**
     * A placeholder method to be overridden by subclasses. 
     * Begins or restarts the mini-game's initial state.
     */
    protected void startGame() {
        // Overridden by subclass implementations
    }

    /**
     * Prepares the backdrop of the canvas with a default clear and fill color.
     * Subclasses can further draw backgrounds or decorations on top.
     */
    protected void setupBackground() {
        gc.clearRect(0, 0, canvasWidth, canvasHeight);
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, canvasWidth, canvasHeight);
    }

    /**
     * Draws any uncollected coins on the canvas as yellow/gold circles.
     */
    protected void drawCoins() {
        for (Coin coin : coins) {
            if (!coin.collected) {
                gc.setFill(Color.GOLD);
                gc.fillOval(coin.x, coin.y, coinSize, coinSize);
                gc.setStroke(Color.ORANGE);
                gc.setLineWidth(2);
                gc.strokeOval(coin.x, coin.y, coinSize, coinSize);
            }
        }
    }

    /**
     * Draws a game-over overlay, including game stats, skill gained, 
     * and a tutorial notice if {@link #isTutorialScreen} is true.
     */
    protected void drawGameoverOverlay() {
        gc.setFill(new Color(0, 0, 0, 0.7));
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        gc.fillText("GAME OVER", canvasWidth / 2 - 140, canvasHeight / 2 - 50);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        String scoreText = "Score: " + score;
        double scoreWidth = gc.getFont().getSize() * scoreText.length() * 0.6;
        gc.fillText(scoreText, canvasWidth / 2 - scoreWidth / 2, canvasHeight / 2 + 20);

        String coinsText = "Coins: " + coinsCollected;
        double coinsWidth = gc.getFont().getSize() * coinsText.length() * 0.6;
        gc.fillText(coinsText, canvasWidth / 2 - coinsWidth / 2, canvasHeight / 2 + 70);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        String expText = "Experience gain: " + skillGain;
        double expWidth = gc.getFont().getSize() * expText.length() * 0.5;
        gc.fillText(expText, canvasWidth / 2 - expWidth / 2, canvasHeight / 2 + 120);

        if (isTutorialScreen) {
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            gc.setFill(Color.YELLOW);
            String tutorialText = "Experience and coins not gained because this minigame was a tutorial";
            double tutorialWidth = gc.getFont().getSize() * tutorialText.length() * 0.5;
            gc.fillText(tutorialText, canvasWidth / 2 - tutorialWidth / 2, canvasHeight / 2 + 200);
        }

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        String restartText = "Click or press ENTER to restart";
        double restartWidth = gc.getFont().getSize() * restartText.length() * 0.5;
        gc.fillText(restartText, canvasWidth / 2 - restartWidth / 2, canvasHeight / 2 + 170);
    }

    /**
     * Sets up the main {@link #gameLoop} that performs game logic updates
     * and rendering calls during the minigame.
     */
    protected void setupGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isGameOver) {
                    updateGame();
                }
                renderGame();
            }
        };
    }

    /**
     * A placeholder update method to be overridden by minigames.
     * Contains the game logic for each frame (movement, collisions, etc.).
     */
    protected void updateGame() {
        // Overridden by subclass implementations
    }

    /**
     * A placeholder render method to be overridden by minigames.
     * Contains canvas drawing logic for each frame.
     */
    protected void renderGame() {
        // Overridden by subclass implementations
    }

    /**
     * Sets up common game variables (e.g., coins list, random generator, etc.). 
     * Subclass screens typically extend this method for specialized game variables.
     */
    protected void setupGameVariables() {
        random = new Random();
        coins = new ArrayList<>();
        score = 0;
        skillGain = 0;
        coinCounter = 0;
        coinsCollected = 0;
        isGameOver = false;
    }

    /**
     * Initializes general UI structure for minigames, including a canvas 
     * with a shadow and a centered layout. Subclass screens typically 
     * add specific HUD, controls, or layout changes.
     */
    protected void setupUI() {
        root.setPadding(new Insets(20 * ratio));
        root.setStyle("-fx-background-color: #d0ecf8;");

        gameCanvas = new Canvas(canvasWidth, canvasHeight);
        gc = gameCanvas.getGraphicsContext2D();

        DropShadow canvasShadow = new DropShadow();
        canvasShadow.setRadius(10.0 * ratio);
        canvasShadow.setOffsetX(5.0 * ratio);
        canvasShadow.setOffsetY(5.0 * ratio);
        canvasShadow.setColor(Color.color(0, 0, 0, 0.4));
        gameCanvas.setEffect(canvasShadow);

        StackPane canvasContainer = new StackPane();
        canvasContainer.getChildren().add(gameCanvas);
        canvasContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        canvasContainer.setPadding(new Insets(10 * ratio));

        root.setCenter(canvasContainer);
    }
}
