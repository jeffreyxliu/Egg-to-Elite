package group44.Screens;

import group44.Screens.TrainingScreen;
import group44.App;
import group44.Pet;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A tutorial screen to teach the user how to do the "running" minigame,
 * modeled after a dinosaur-style endless runner. Press SPACE to jump over obstacles.
 * Extends TrainingScreen for tutorial logic.
 */
public class RunningTutorialScreen extends TrainingScreen {

    /** The gravitational pull applying to the pet. */
    private final double gravity;

    /** The upward force applied to the pet when jumping. */
    private final double jumpForce;

    /** The y-coordinate for the ground level (the pet stands/jumps above it). */
    private final double groundLevel;

    /** Width of each spawned obstacle in the scene. */
    private final double obstacleWidth;

    /** Height of each spawned obstacle in the scene. */
    private final double obstacleHeight;

    /** The initial speed at which obstacles move toward the pet. */
    private final double initialObstacleSpeed;

    /** The gap distance (in pixels) between consecutive obstacles. */
    private final double obstacleGapDistance;

    /** The vertical velocity of the pet. Positive values move downward. */
    private double petVelocity;

    /** A list to store and manage active obstacles. */
    private List<Obstacle> obstacles;

    /** The current obstacle speed, which can increase as the game progresses. */
    private double obstacleSpeed;

    /** The image representing the pet while running. */
    private Image runningImage;

    /**
     * Constructs a new RunningTutorialScreen, initializing game parameters
     * such as canvas size, positions, gravity, and obstacle sizes.
     *
     * @param pet              The Pet object associated with this game (for sprite usage).
     * @param isTutorialScreen Indicates if this is a tutorial screen (behavior may differ from actual game).
     */
    public RunningTutorialScreen(Pet pet, boolean isTutorialScreen) {
        super(pet, isTutorialScreen);

        // Apply “ratio” to both UI and game constants
        canvasWidth = 800 * ratio;
        canvasHeight = 600 * ratio;

        this.petX = 100 * ratio;
        this.petWidth = 60 * ratio;
        this.petHeight = 45 * ratio;
        this.gravity = 0.18 * ratio;
        this.jumpForce = -7.0 * ratio;
        this.groundLevel = canvasHeight - (65 * ratio);

        this.obstacleWidth = 40 * ratio;
        this.obstacleHeight = 60 * ratio;
        this.initialObstacleSpeed = 3.0 * ratio;
        this.obstacleGapDistance = 300 * ratio;

        setupUI();
        loadResources();
        setupGameVariables();
        setupControls();
        setupGameLoop();
        startGame();
    }

    /**
     * Sets up the basic user interface, including heads-up display (HUD) and bottom buttons.
     * Requests focus on the game canvas so it can receive keyboard input.
     */
    @Override
    protected void setupUI() {
        super.setupUI();
        setupHUD("Press SPACE to jump over obstacles!");
        setupBottomButtons();
        gameCanvas.requestFocus();
    }

    /**
     * Loads any external resources needed for this running tutorial,
     * such as the pet's running sprite.
     */
    private void loadResources() {
        try {
            runningImage = pet.getPetImage();
        } catch (Exception e) {
            System.err.println("Error loading running image: " + e.getMessage());
            runningImage = null;
        }
    }

    /**
     * Sets up the needed variables for the running tutorial game, such as the
     * obstacle list and obstacle speed, recalculating if necessary.
     */
    @Override
    protected void setupGameVariables() {
        super.setupGameVariables();
        obstacles = new ArrayList<>();
        obstacleSpeed = initialObstacleSpeed;
    }

    /**
     * Configures user input controls for jumping. SPACE resets if the game is over,
     * or makes the pet jump if the game is running.
     */
    private void setupControls() {
        gameCanvas.setFocusTraversable(true);
        gameCanvas.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                if (isGameOver) {
                    restartGame();
                } else if (isGameRunning) {
                    // Jump if on (or near) the ground
                    if (petY >= groundLevel - petHeight / 2 - 1) {
                        petVelocity = jumpForce;
                    }
                }
            }
        });
    }

    /**
     * Creates and starts an animation loop. The handle() method is called up to 60 times 
     * per second, updating and rendering the game until it is over.
     */
    @Override
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
     * Initializes or restarts the game state, placing the pet near the ground,
     * clearing existing obstacles, and starting the animation loop.
     */
    @Override
    protected void startGame() {
        petY = groundLevel - petHeight / 2;
        petVelocity = 0;
        obstacles.clear();
        score = 0;
        obstacleSpeed = initialObstacleSpeed;
        isGameOver = false;
        scoreLabel.setText("0");

        // Add two obstacles right away
        addObstacle(canvasWidth + 200 * ratio);
        addObstacle(canvasWidth + 200 * ratio + obstacleGapDistance);

        isGameRunning = true;
        gameLoop.start();
        gameCanvas.requestFocus();
    }

    /**
     * Updates game logic such as pet movement and obstacle positions,
     * checking for collisions and increasing the score when obstacles are passed.
     */
    @Override
    protected void updateGame() {
        petVelocity += gravity;
        petY += petVelocity;

        // Keep pet from sinking below ground
        if (petY > groundLevel - petHeight / 2) {
            petY = groundLevel - petHeight / 2;
            petVelocity = 0;
        }

        Iterator<Obstacle> it = obstacles.iterator();
        while (it.hasNext()) {
            Obstacle obstacle = it.next();
            obstacle.x -= obstacleSpeed;

            // Score if pet passes center of obstacle
            if (!obstacle.isPassed && obstacle.x + (obstacleWidth / 2.0) < petX) {
                obstacle.isPassed = true;
                score++;
                scoreLabel.setText(String.valueOf(score));
            }

            // Remove obstacles off-screen
            if (obstacle.x + obstacleWidth < 0) {
                it.remove();
            }
        }

        // Add new obstacle if needed
        if (obstacles.isEmpty() 
         || obstacles.get(obstacles.size() - 1).x < canvasWidth - 300 * ratio) {
            addObstacle(canvasWidth);
        }

        checkCollisions();
    }

    /**
     * Renders all game elements such as background, ground, obstacles, and the pet sprite.
     * Also displays a game over overlay if the game has ended.
     */
    @Override
    protected void renderGame() {
        setupBackground();

        // Ground
        gc.setFill(Color.rgb(83, 54, 10));
        gc.fillRect(0, groundLevel, canvasWidth, 50 * ratio);
        gc.setFill(Color.rgb(124, 252, 0));
        gc.fillRect(0, groundLevel, canvasWidth, 10 * ratio);

        // Obstacles
        gc.setFill(Color.rgb(50, 50, 50));
        for (Obstacle obstacle : obstacles) {
            gc.fillRect(obstacle.x, obstacle.y, obstacleWidth, obstacleHeight);
        }

        // Pet
        if (runningImage != null) {
            double desiredHeight = 45;
            double imageWidth = runningImage.getWidth();
            double imageHeight = runningImage.getHeight();
            double aspectRatio = imageWidth / imageHeight;
            double scaledWidth = desiredHeight * aspectRatio;

            // Then draw the sprite preserving aspect ratio
            gc.drawImage(
                runningImage,
                petX - scaledWidth / 2,
                petY - desiredHeight / 2,
                scaledWidth,
                desiredHeight
            );
        } else {
            gc.setFill(Color.RED);
            gc.fillRect(petX - petWidth / 2,
                        petY - petHeight / 2,
                        petWidth,
                        petHeight);
        }

        // Game Over overlay
        if (isGameOver) {
            drawGameoverOverlay();
        }
    }

    /**
     * Spawns a new obstacle at the specified x-position, placing
     * it at ground level minus the obstacle height.
     *
     * @param startX The x-coordinate where the new obstacle appears.
     */
    private void addObstacle(double startX) {
        obstacles.add(new Obstacle(startX));
    }

    /**
     * Checks if the pet intersects with any obstacle, and ends the game if a collision is detected.
     */
    private void checkCollisions() {
        double petLeft = petX - petWidth / 2;
        double petRight = petX + petWidth / 2;
        double petTop = petY - petHeight / 2;
        double petBottom = petY + petHeight / 2;

        for (Obstacle obstacle : obstacles) {
            double obsLeft = obstacle.x;
            double obsRight = obstacle.x + obstacleWidth;
            double obsTop = obstacle.y;
            double obsBottom = obstacle.y + obstacleHeight;

            if (petRight > obsLeft && petLeft < obsRight
                && petBottom > obsTop && petTop < obsBottom) {
                endGame();
                break;
            }
        }
    }

    /**
     * Signals that the game has ended due to a collision or other reason.
     * The gameOver flag is set and rendering will stop the main logic updates.
     */
    private void endGame() {
        isGameOver = true;
    }

    /**
     * Restarts the game if it has ended, resetting all parameters
     * as if the game just started.
     */
    @Override
    protected void restartGame() {
        if (isGameOver) {
            startGame();
        }
    }

    /**
     * Stops the animation loop and transitions the user back to the main GameScreen.
     * Typically called if the user wants to exit this tutorial.
     */
    @Override
    protected void returnToGame() {
        gameLoop.stop();
        App.setScreen(new GameScreen(pet));
    }

    /**
     * Represents a simple obstacle for the pet to jump over.
     * Holds x/y position, and a flag indicating if it's already been counted for score.
     */
    private class Obstacle {
        double x;
        double y;
        boolean isPassed;

        /**
         * Creates a new obstacle at the specified x-coordinate just above the ground.
         *
         * @param startX The initial horizontal position of the obstacle.
         */
        Obstacle(double startX) {
            this.x = startX;
            this.y = groundLevel - obstacleHeight;
            this.isPassed = false;
        }
    }
}