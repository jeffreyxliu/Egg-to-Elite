package group44.Screens;

import group44.App;
import group44.Pet;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A running minigame similar to the "dinosaur runner" where the pet must jump over obstacles.
 * Optionally, coins can be collected along the way. When {@code isTutorialScreen} is false,
 * the Pet's running skill and coins are updated on game over.
 */
public class TrainRunningScreen extends TrainingScreen {

    // Gameplay constants
    private static final double GRAVITY = 0.18;
    private static final double JUMP_FORCE = -7.0;
    private static final double GROUND_OFFSET = 65;
    private static final double OBSTACLE_WIDTH = 40;
    private static final double OBSTACLE_HEIGHT = 60;
    private static final double INITIAL_OBSTACLE_SPEED = 3.0;
    private static final double OBSTACLE_GAP_DISTANCE = 300;

    // Pet parameters
    private final double petX = 100;
    private final double petWidth = 60;
    private final double petHeight = 45;

    // Game variables
    private double petY;
    private double petVelocity;
    private double obstacleSpeed;
    private int coinCounter;
    
    // Obstacles
    private List<Obstacle> obstacles;
    
    // Images
    private Image runningImage;

    /**
     * Constructor for the TrainRunningScreen.
     * Initializes UI elements, loads resources, sets up game variables, controls, and starts the game.
     *
     * @param pet the Pet object used in the minigame.
     * @param isTutorialScreen true if this screen is a tutorial (no rewards), false otherwise.
     */
    public TrainRunningScreen(Pet pet, boolean isTutorialScreen) {
        super(pet, isTutorialScreen);

        // Canvas dimensions
        this.canvasWidth = 800;
        this.canvasHeight = 600;

        // Initialize the game
        setupUI();
        loadResources();
        setupGameVariables();
        setupControls();
        setupGameLoop();
        
        // Start the game
        startGame();

        ScreenClass.playMusic("running.mp3");
    }
    
    /**
     * Sets up the user interface by calling the parent method and configuring the HUD and bottom buttons.
     */
    @Override
    protected void setupUI() {
        super.setupUI();
        
        // Set up the HUD and bottom buttons
        setupHUD("Press SPACE or CLICK to jump over obstacles!");
        setupBottomButtons();
    }
    
    /**
     * Loads required resources such as the pet's running image.
     */
    private void loadResources() {
        try {
            runningImage = pet.getPetImage();
            System.out.println("Running image loaded");
        } catch (Exception e) {
            System.err.println("Error loading running image: " + e.getMessage());
            runningImage = null;
        }
    }

    /**
     * Sets up game variables specific to the running minigame.
     * Initializes the obstacles list, obstacle speed, and resets the coin counter.
     */
    @Override
    protected void setupGameVariables() {
        super.setupGameVariables();
        obstacles = new ArrayList<>();
        obstacleSpeed = INITIAL_OBSTACLE_SPEED;
        coinCounter = 0;
    }

    /**
     * Configures keyboard and mouse controls.
     * SPACE key or mouse click triggers a jump if near the ground; if the game is over, triggers a restart.
     */
    private void setupControls() {
        // Make canvas focusable for keyboard input
        gameCanvas.setFocusTraversable(true);
        
        // Space key to jump
        gameCanvas.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                if (isGameOver) {
                    restartGame();
                } else if (isGameRunning) {
                    // Jump if near the ground
                    if (petY >= (canvasHeight - GROUND_OFFSET) - petHeight / 2 - 1) {
                        petVelocity = JUMP_FORCE;
                    }
                }
            }
        });

        // Mouse click to jump
        gameCanvas.setOnMouseClicked(e -> {
            if (isGameOver) {
                restartGame();
            } else if (isGameRunning) {
                if (petY >= (canvasHeight - GROUND_OFFSET) - petHeight / 2 - 1) {
                    petVelocity = JUMP_FORCE;
                }
            }
        });
    }

    /**
     * Starts the running game by resetting game state variables, clearing previous obstacles and coins,
     * and adding initial obstacles to the scene.
     */
    @Override
    protected void startGame() {
        // Reset game state
        petY = (canvasHeight - GROUND_OFFSET) - petHeight / 2;
        petVelocity = 0;
        obstacles.clear();
        coins.clear();
        score = 0;
        coinsCollected = 0;
        coinCounter = 0;
        obstacleSpeed = INITIAL_OBSTACLE_SPEED;
        isGameOver = false;
        
        // Reset HUD labels
        scoreLabel.setText("0");
        coinCountLabel.setText("0");
        
        // Add initial obstacles
        addObstacle(canvasWidth + 200);
        addObstacle(canvasWidth + 200 + OBSTACLE_GAP_DISTANCE);
        
        // Start animations and focus canvas for events
        isGameRunning = true;
        gameLoop.start();
        gameCanvas.requestFocus();
    }

    /**
     * Updates the game state each frame:
     * - Applies gravity to the pet.
     * - Moves obstacles and coins.
     * - Checks scoring conditions and collisions.
     */
    @Override
    protected void updateGame() {
        // Apply gravity to pet
        petVelocity += GRAVITY;
        petY += petVelocity;
        
        // Reset pet position if it hits the ground
        if (petY > (canvasHeight - GROUND_OFFSET) - petHeight / 2) {
            petY = (canvasHeight - GROUND_OFFSET) - petHeight / 2;
            petVelocity = 0;
        }
                
        // Move obstacles and update score
        Iterator<Obstacle> obsIterator = obstacles.iterator();
        while (obsIterator.hasNext()) {
            Obstacle obstacle = obsIterator.next();
            obstacle.x -= obstacleSpeed;
            
            // Check if pet passed obstacle for scoring
            if (!obstacle.passed && obstacle.x + OBSTACLE_WIDTH / 2 < petX) {
                obstacle.passed = true;
                score++;
                scoreLabel.setText(String.valueOf(score));
            }
            
            // Remove obstacles off-screen
            if (obstacle.x + OBSTACLE_WIDTH < 0) {
                obsIterator.remove();
            }
        }
        
        // Move coins and check for collection
        Iterator<Coin> coinIterator = coins.iterator();
        while (coinIterator.hasNext()) {
            Coin coin = coinIterator.next();
            coin.x -= obstacleSpeed;
            
            // Check if pet collects coin
            if (!coin.collected && checkCoinCollision(coin)) {
                coin.collected = true;
                coinsCollected++;
                coinCountLabel.setText(String.valueOf(coinsCollected));
                ScreenClass.playSound("coin.mp3");
            }
            
            // Remove coins that are off-screen or collected
            if (coin.x + coinSize < 0 || coin.collected) {
                coinIterator.remove();
            }
        }

        // Add new obstacle when needed
        if (obstacles.isEmpty() || obstacles.get(obstacles.size() - 1).x < canvasWidth - OBSTACLE_GAP_DISTANCE) {
            addObstacle(canvasWidth);
        }
        
        // Check for collisions
        checkCollisions();
    }

    /**
     * Renders the game by drawing the background, obstacles, coins, pet, and game over overlay as needed.
     */
    @Override
    protected void renderGame() {
        setupBackground();
        
        // Draw ground
        gc.setFill(Color.rgb(83, 54, 10));
        gc.fillRect(0, canvasHeight - GROUND_OFFSET, canvasWidth, 50);
        
        // Draw grass
        gc.setFill(Color.rgb(124, 252, 0));
        gc.fillRect(0, canvasHeight - GROUND_OFFSET, canvasWidth, 10);
        
        // Draw obstacles
        gc.setFill(Color.DARKGRAY);
        for (Obstacle obstacle : obstacles) {
            gc.fillRect(obstacle.x, obstacle.y, OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
        }

        // Draw coins using parent's drawCoins method
        drawCoins();
        
        // Draw pet image, preserving aspect ratio
        if (runningImage != null) {
            double desiredHeight = 45;
            double imageWidth = runningImage.getWidth();
            double imageHeight = runningImage.getHeight();
            double aspectRatio = imageWidth / imageHeight;
            double scaledWidth = desiredHeight * aspectRatio;

            gc.drawImage(
                runningImage,
                petX - scaledWidth / 2,
                petY - desiredHeight / 2,
                scaledWidth,
                desiredHeight
            );
        } else {
            // Fallback if image is missing
            gc.setFill(Color.RED);
            gc.fillRect(petX - petWidth / 2,
                        petY - petHeight / 2,
                        petWidth,
                        petHeight);
        }
        
        // Draw game over overlay if game has ended
        if (isGameOver) {
            drawGameoverOverlay();
        }
    }
    
    /**
     * Adds a new obstacle at the specified x-coordinate.
     * Also, based on a coin counter, occasionally adds a coin near the jump peak.
     *
     * @param x the x-coordinate where the obstacle is added.
     */
    private void addObstacle(double x) {
        // Create a new obstacle at the appropriate y-coordinate
        double obsY = (canvasHeight - GROUND_OFFSET) - OBSTACLE_HEIGHT;
        obstacles.add(new Obstacle(x, obsY));
        
        // Add a coin every few obstacles
        coinCounter++;
        if (coinCounter >= 2 + random.nextInt(3)) { // 2-4 obstacles
            coinCounter = 0;
            
            // Position coins higher to match the pet's jump arc peak
            double groundY = canvasHeight - GROUND_OFFSET;
            double jumpPeakHeight = 150; // Approximate jump peak height
            double coinY = groundY - jumpPeakHeight + random.nextInt(30); 
            
            // Add the coin centered horizontally relative to the obstacle
            coins.add(new Coin(x + OBSTACLE_WIDTH / 2 - coinSize / 2, coinY));
        }
    }

    /**
     * Checks if the pet's bounding box collides with the given coin.
     * The collision detection uses a simple rectangular overlap test.
     *
     * @param coin the coin to check for collision.
     * @return true if a collision is detected, false otherwise.
     */
    @Override
    protected boolean checkCoinCollision(Coin coin) {
        double petLeft = petX - petWidth / 2 + 5;
        double petRight = petX + petWidth / 2 - 5;
        double petTop = petY + 5;
        double petBottom = petY + petHeight - 5;
        
        double coinLeft = coin.x;
        double coinRight = coin.x + coinSize;
        double coinTop = coin.y;
        double coinBottom = coin.y + coinSize;
        
        return !(coinRight < petLeft || coinLeft > petRight ||
                 coinBottom < petTop || coinTop > petBottom);
    }
    
    /**
     * Checks for collisions between the pet and any obstacles.
     * If a collision is detected, the game ends.
     */
    private void checkCollisions() {
        double petLeft = petX - petWidth / 2 + 5;
        double petRight = petX + petWidth / 2 - 5;
        double petTop = petY + 5;
        double petBottom = petY + petHeight - 5;
        
        for (Obstacle obstacle : obstacles) {
            if (petRight > obstacle.x && petLeft < obstacle.x + OBSTACLE_WIDTH) {
                if (petBottom > obstacle.y && petTop < obstacle.y + OBSTACLE_HEIGHT) {
                    endGame();
                    return;
                }
            }
        }
    }
        
    /**
     * Ends the game, calculates the skill gain based on the score, and updates the pet's running experience and coins.
     * In tutorial mode, no rewards are given.
     */
    private void endGame() {
        isGameOver = true;
        
        // Calculate skill gain based on score
        skillGain = Math.max(0, score * 2);

        if (!isTutorialScreen) {
            pet.addRunExperience(skillGain);
            pet.setCoins(pet.getCoins() + coinsCollected);
        }
    }
    
    /**
     * Restarts the game if it is over by calling {@link #startGame()}.
     */
    @Override
    protected void restartGame() {
        if (isGameOver) {
            startGame();
        }
    }

    /**
     * Stops the game loop and returns control to the main game screen,
     * updating the game statistics before transitioning.
     */
    @Override
    protected void returnToGame() {
        gameLoop.stop();
        GameScreen gameScreen = new GameScreen(pet);
        gameScreen.updateStats();  // Updates level/exp bars with new stats
        App.setScreen(gameScreen);
    }
        
    /**
     * Inner class representing an obstacle in the running minigame.
     * Each obstacle has an x and y position and tracks whether the pet has passed it.
     */
    private static class Obstacle {
        double x;         // x position
        double y;         // y position
        boolean passed;   // whether the pet has passed this obstacle
        
        /**
         * Constructs a new Obstacle.
         *
         * @param x the x-coordinate of the obstacle.
         * @param y the y-coordinate of the obstacle.
         */
        Obstacle(double x, double y) {
            this.x = x;
            this.y = y;
            this.passed = false;
        }
    }
}