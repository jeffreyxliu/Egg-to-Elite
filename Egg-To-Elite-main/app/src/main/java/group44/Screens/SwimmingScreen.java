package group44.Screens;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import group44.App;
import group44.Pet;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

/**
 * A swimming-based minigame that extends TrainingScreen.
 * The player controls a pet on the water surface, jumping or diving
 * past obstacles while collecting coins.
 */
public class SwimmingScreen extends TrainingScreen {

    /** Controls the gravitational force pulling the pet back to the water surface while jumping. */
    private static final double GRAVITY = 0.3;
    /** Controls the gravitational force pulling the pet upward while diving. */
    private static final double DIVE_GRAVITY = -0.3;
    /** The jump force applied when the pet jumps above the surface. */
    private static final double UP_JUMP_FORCE = -10.5;
    /** The jump force applied when the pet dives below the surface. */
    private static final double DOWN_JUMP_FORCE = 12.0;
    /** The width of standard (green) obstacles. */
    private static final int OBSTACLE_WIDTH = 50;
    /** The vertical gap in standard obstacles. */
    private static final int OBSTACLE_GAP = 100;
    /** The initial horizontal speed of obstacles drifting left. */
    private static final int INITIAL_OBSTACLE_SPEED = 3;

    /** The width of rising (red) obstacles. */
    private static final int RISING_OBSTACLE_WIDTH = 50;
    /** The height of rising (red) obstacles. */
    private static final int RISING_OBSTACLE_HEIGHT = 80;
    /** The vertical speed for rising obstacles. */
    private static final double RISING_OBSTACLE_VERTICAL_SPEED = 1.0;

    /** Speed at which background clouds drift from right to left. */
    private static final double CLOUD_SPEED = 0.5;
    /** Number of clouds displayed in the background. */
    private static final int NUM_CLOUDS = 5;

    /** Indicates if the pet is currently jumping (true) or not (false). */
    private boolean jumping = false;
    /**
     * The jump mode: 
     * -1 for jumping up (above the surface), 
     * +1 for diving down (below the surface).
     */
    private int jumpMode = 0;

    /** The pet's vertical velocity while jumping or diving. */
    private double swimVelocity;
    /** The current speed of obstacles moving left, which can increase over time. */
    private double obstacleSpeed;

    /** A list of standard, green obstacles. */
    private List<Obstacle> obstacles;
    /** A list of rising, red obstacles. */
    private List<RisingObstacle> risingObstacles;
    /** A list of background clouds. */
    private List<Cloud> clouds;

    /** Distance between consecutive obstacles. Larger for tutorials, smaller for the actual game. */
    private int distanceBetweenObstacles;

    /**
     * Constructor for SwimmingScreen.
     * Sets up visuals, controls, and starts the minigame.
     *
     * @param pet The pet being trained.
     * @param isTutorialScreen If true, no rewards or skill gains are given.
     */
    public SwimmingScreen(Pet pet, boolean isTutorialScreen) {
        super(pet, isTutorialScreen);

        // The pet's size in the game
        this.petWidth = 30;
        this.petHeight = 30;

        // Decide spacing based on whether it’s a tutorial
        distanceBetweenObstacles = isTutorialScreen ? 600 : 300;

        // Setup everything inherited from TrainingScreen
        setupUI();
        setupHUD("Press SPACE/SHIFT or LEFT CLICK/RIGHT CLICK to jump/dive");
        setupBottomButtons();
        setupGameVariables();
        setupControls();
        setupGameLoop();

        // Start the game immediately
        startGame();

        ScreenClass.playMusic("swimming.mp3");
    }

    /**
     * Initializes variables such as obstacle lists, cloud lists,
     * and obstacle speeds. Also populates the background with a set of clouds.
     */
    @Override
    protected void setupGameVariables() {
        super.setupGameVariables(); // sets up coins list, score, etc.

        random = new Random();
        obstacles = new ArrayList<>();
        risingObstacles = new ArrayList<>();
        clouds = new ArrayList<>();

        obstacleSpeed = INITIAL_OBSTACLE_SPEED;
        
        // Create some clouds at random positions in the top half
        for (int i = 0; i < NUM_CLOUDS; i++) {
            double x = random.nextInt((int) canvasWidth);
            double y = random.nextInt((int) (canvasHeight / 2.0));
            double w = 60 + random.nextInt(40);  // random cloud width
            double h = 30 + random.nextInt(20);  // random cloud height
            clouds.add(new Cloud(x, y, w, h));
        }
    }

    /**
     * Sets up controls for jumping/diving on SPACE/SHIFT or mouse clicks,
     * and handles game restart on ENTER if game is over.
     */
    private void setupControls() {
        // Ensure the canvas can receive key events
        gameCanvas.setFocusTraversable(true);

        gameCanvas.setOnKeyPressed(e -> {
            // Only jump/dive if conditions are met
            if (!jumping && isGameRunning && !isGameOver && (petY + petHeight >= canvasHeight / 2)) {
                if (e.getCode() == KeyCode.SPACE) {
                    jumping = true;
                    jumpMode = -1;
                    swimVelocity = UP_JUMP_FORCE;
                } else if (e.getCode() == KeyCode.SHIFT) {
                    jumping = true;
                    jumpMode = 1;
                    swimVelocity = DOWN_JUMP_FORCE;
                }
            }

            // Restart the game if ENTER is pressed and we're at game over
            if (e.getCode() == KeyCode.ENTER && isGameOver) {
                restartGame();
            }
        });

        gameCanvas.setOnMouseClicked(e -> {
            // Only respond to clicks if the game is running and not over
            if (isGameRunning && !isGameOver) {
                // Check if the pet is near the water's surface
                if (!jumping && (petY + petHeight >= canvasHeight / 2)) {
                    switch (e.getButton()) {
                        case PRIMARY:  // Left click - Jump up
                            jumping = true;
                            jumpMode = -1;
                            swimVelocity = UP_JUMP_FORCE;
                            break;
                            
                        case SECONDARY:  // Right click - Dive down
                            jumping = true;
                            jumpMode = 1;
                            swimVelocity = DOWN_JUMP_FORCE;
                            break;
                            
                        default:
                            break;
                    }
                }
            } else if (isGameOver) {
                // Restart on mouse click when game is over
                restartGame();
            }
        });
    }

    /**
     * Positions the pet at the surface, resets scores/obstacles,
     * and starts the main game loop.
     */
    @Override
    protected void startGame() {
        // Position pet horizontally around one-third of the canvas
        petX = canvasWidth / 3.0;
        // Position pet just at the water's surface
        petY = canvasHeight / 2.0 - petHeight;

        swimVelocity = 0;
        isGameOver = false;
        isGameRunning = true;

        obstacles.clear();
        risingObstacles.clear();
        coins.clear(); // from parent class
        score = 0;
        coinsCollected = 0;
        coinCounter = 0;
        obstacleSpeed = INITIAL_OBSTACLE_SPEED;

        // Add some initial obstacles so that they come in from the right
        addObstacle(canvasWidth);
        addObstacle(canvasWidth + distanceBetweenObstacles);
        addObstacle(canvasWidth + distanceBetweenObstacles * 2);

        // Add one initial rising obstacle
        addRisingObstacle(canvasWidth + distanceBetweenObstacles * 0.75);

        // Reset labels
        scoreLabel.setText("0");
        coinCountLabel.setText("0");

        // Start animation
        gameLoop.start();
        gameCanvas.requestFocus();
    }

    /**
     * Moves obstacles, rising obstacles, and clouds; checks for collisions;
     * spawns new obstacles and coins; updates the pet's position.
     */
    @Override
    protected void updateGame() {
        // Vertical movement (jump/dive) 
        if (jumping) {
            if (jumpMode == 1) { // diving
                swimVelocity += DIVE_GRAVITY;
                petY += swimVelocity;

                // If diving upward or reaching the top boundary of water
                if (swimVelocity < 0 && petY <= (canvasHeight / 2.0 - petHeight)) {
                    petY = canvasHeight / 2.0 - petHeight;
                    swimVelocity = 0;
                    jumping = false;
                }

                // If we go below the bottom, game ends
                if (petY > canvasHeight - petHeight) {
                    endGame();
                    return;
                }
            } else if (jumpMode == -1) { // jumping upward
                swimVelocity += GRAVITY;
                petY += swimVelocity;

                // If falling back down to water surface
                if (swimVelocity > 0 && petY >= (canvasHeight / 2.0 - petHeight)) {
                    petY = canvasHeight / 2.0 - petHeight;
                    swimVelocity = 0;
                    jumping = false;
                }
            }
        } else {
            // If not jumping, keep pet at water surface
            petY = canvasHeight / 2.0 - petHeight;
            swimVelocity = 0;
        }

        // Move obstacles
        Iterator<Obstacle> obsIterator = obstacles.iterator();
        while (obsIterator.hasNext()) {
            Obstacle obs = obsIterator.next();
            obs.x -= obstacleSpeed;

            // Increase score when pet passes an obstacle
            if (!obs.passed && obs.x + OBSTACLE_WIDTH < petX) {
                obs.passed = true;
                score++;
                scoreLabel.setText(String.valueOf(score));
            }

            // Remove off-screen obstacles
            if (obs.x + OBSTACLE_WIDTH < 0) {
                obsIterator.remove();
            }
        }

        // Move rising obstacles
        Iterator<RisingObstacle> risingIterator = risingObstacles.iterator();
        while (risingIterator.hasNext()) {
            RisingObstacle ro = risingIterator.next();
            ro.x -= obstacleSpeed;
            ro.y -= RISING_OBSTACLE_VERTICAL_SPEED;
            // Remove if fully off screen
            if (ro.x + RISING_OBSTACLE_WIDTH < 0 || ro.y + RISING_OBSTACLE_HEIGHT < 0) {
                risingIterator.remove();
            }
        }

        // Move coins, check collisions
        for (Iterator<Coin> coinIt = coins.iterator(); coinIt.hasNext();) {
            Coin coin = coinIt.next();
            coin.x -= obstacleSpeed;

            // If not collected, check collision
            if (!coin.collected && checkCoinCollision(coin)) {
                coin.collected = true;
                coinsCollected++;
                coinCountLabel.setText(String.valueOf(coinsCollected));
                ScreenClass.playSound("coin.mp3");
            }

            // Remove coins off screen or collected
            if (coin.x + coinSize < 0 || coin.collected) {
                coinIt.remove();
            }
        }

        // Spawn new obstacles if needed
        if (obstacles.isEmpty() 
            || obstacles.get(obstacles.size() - 1).x < canvasWidth - distanceBetweenObstacles) {
            addObstacle(canvasWidth);
        }

        // Spawn new rising obstacles if needed, with a 50% chance to prevent overcrowding
        if (risingObstacles.isEmpty() || 
            (obstacles.size() >= 2 && 
            risingObstacles.get(risingObstacles.size() - 1).x < obstacles.get(obstacles.size() - 2).x)) {
            
            if (random.nextDouble() < 0.5) {
                addRisingObstacle(canvasWidth);
            }
        }

        // Update cloud positions
        for (Cloud cloud : clouds) {
            cloud.x -= CLOUD_SPEED;
            if (cloud.x + cloud.width < 0) {
                // Recycle cloud to the right
                cloud.x = canvasWidth;
                cloud.y = random.nextInt((int) (canvasHeight / 2.0));
            }
        }

        // Check collisions
        checkCollisions();
        checkRisingObstacleCollisions();
    }

    /**
     * Renders the game elements: clouds, water, obstacles, coins, and the pet.
     * If the game is over, draws an overlay.
     */
    @Override
    protected void renderGame() {
        // Clear the canvas and fill background from parent’s method
        setupBackground();

        // 1) Draw clouds in top half
        for (Cloud cloud : clouds) {
            gc.setFill(Color.LIGHTGRAY);
            gc.fillOval(cloud.x, cloud.y, cloud.width, cloud.height);
        }

        // 2) Draw water
        gc.setFill(Color.rgb(0, 105, 148));
        gc.fillRect(0, canvasHeight / 2.0, canvasWidth, canvasHeight / 2.0);

        // 3) Draw obstacles
        gc.setFill(Color.FORESTGREEN);
        for (Obstacle obs : obstacles) {
            double obstacleHeight = canvasHeight - obs.gapY;
            gc.fillRect(obs.x, obs.gapY, OBSTACLE_WIDTH, obstacleHeight);
        }

        // 4) Draw rising obstacles
        gc.setFill(Color.DARKRED);
        for (RisingObstacle ro : risingObstacles) {
            gc.fillRect(ro.x, ro.y, RISING_OBSTACLE_WIDTH, RISING_OBSTACLE_HEIGHT);
        }

        // 5) Draw coins (inherited method from TrainingScreen)
        drawCoins();

        // 6) Draw pet
        if (pet.getPetImage() != null) {
            // Tilt the pet based on swim velocity
            double rotation = Math.min(Math.max(swimVelocity * 5, -20), 20);
            gc.save();
            gc.translate(petX, petY + petHeight / 2.0);
            gc.rotate(rotation);
            gc.drawImage(pet.getPetImage(), -petWidth / 2.0, -petHeight / 2.0, petWidth, petHeight);
            gc.restore();
        } else {
            // fallback if no image
            gc.setFill(Color.PINK);
            gc.fillRect(petX - petWidth / 2.0, petY, petWidth, petHeight);
        }

        // 7) If game is over, draw overlay
        if (isGameOver) {
            drawGameoverOverlay();
        }
    }

    /**
     * Spawns a standard green obstacle at a given x-coordinate along the bottom half,
     * leaving a gap near the water surface. Also spawns coins periodically.
     *
     * @param x The x-position where the obstacle will first appear on screen.
     */
    private void addObstacle(double x) {
        double maxJumpHeight = (Math.pow(-UP_JUMP_FORCE, 2)) / (2 * GRAVITY);
        int maxObstacleTop = (int) (canvasHeight / 2.0 - maxJumpHeight + petHeight);

        int minGapY = Math.max((int) (canvasHeight / 2.0 + 20), maxObstacleTop);
        int maxGapY = (int) (canvasHeight / 2.0 + OBSTACLE_GAP);

        // Raise obstacle slightly
        int gapY = random.nextInt(maxGapY - minGapY + 1) + minGapY - 40;
        int obstacleTopY = gapY - OBSTACLE_GAP;

        Obstacle newObs = new Obstacle(x, obstacleTopY);
        obstacles.add(newObs);

        // Increase coin frequency
        coinCounter++;
        if (coinCounter >= 1) {
            coinCounter = 0;
            
            // The midpoint between this obstacle and the next one
            double nextObstacleX = x + distanceBetweenObstacles;
            double midpointX = x + (nextObstacleX - x) / 2;
            double coinX = midpointX + random.nextInt(60) - 30; 
            int coinY = (int) (canvasHeight / 2.0) + random.nextInt((int) (canvasHeight / 2.0 - coinSize));

            Coin c = new Coin(coinX, coinY);
            coins.add(c);
        }
    }

    /**
     * Spawns a red RisingObstacle starting off-screen, which slowly rises upward.
     * The position may be placed relative to other obstacles for staggered difficulty.
     *
     * @param x The approximate x-coordinate for spawning this obstacle.
     */
    private void addRisingObstacle(double x) {
        double risingObstacleX = Math.max(canvasWidth + 20, x);
        
        if (!obstacles.isEmpty()) {
            // find the rightmost obstacle
            double rightmostX = -1;
            for (Obstacle obs : obstacles) {
                if (obs.x > rightmostX) {
                    rightmostX = obs.x;
                }
            }
            
            if (rightmostX > canvasWidth) {
                risingObstacleX = rightmostX + distanceBetweenObstacles / 2;
                risingObstacleX += random.nextInt(40) - 20;
            }
        }
        
        int initialY = random.nextInt((int)(canvasHeight - RISING_OBSTACLE_HEIGHT));
        risingObstacles.add(new RisingObstacle(risingObstacleX, initialY));
    }

    /**
     * Checks collisions between the pet and any green obstacles.
     * Ends the game if a collision is detected.
     */
    private void checkCollisions() {
        double petLeft = petX - petWidth / 2.0 + 5;
        double petRight = petX + petWidth / 2.0 - 5;
        double petBottom = petY + petHeight - 5;

        for (Obstacle obs : obstacles) {
            if (petRight > obs.x && petLeft < obs.x + OBSTACLE_WIDTH) {
                if (petBottom > obs.gapY) {
                    endGame();
                    return;
                }
            }
        }
    }

    /**
     * Checks collisions between the pet and any red rising obstacles.
     * Ends the game if a collision is detected.
     */
    private void checkRisingObstacleCollisions() {
        double petLeft = petX - petWidth / 2.0 + 5;
        double petRight = petX + petWidth / 2.0 - 5;
        double petTop = petY + 5;
        double petBottom = petY + petHeight - 5;

        for (RisingObstacle ro : risingObstacles) {
            boolean overlapX = (petRight > ro.x) && (petLeft < ro.x + RISING_OBSTACLE_WIDTH);
            boolean overlapY = (petBottom > ro.y) && (petTop < ro.y + RISING_OBSTACLE_HEIGHT);
            if (overlapX && overlapY) {
                endGame();
                return;
            }
        }
    }

    /**
     * Ends the game, applying experience/coin gains if it's not a tutorial.
     * The pet gains swim experience and collects coins earned in the run.
     */
    private void endGame() {
        isGameOver = true;
        // Example skill gain formula
        skillGain = Math.max(0, score * 2);

        // If not tutorial, award pet
        if (!isTutorialScreen) {
            pet.addSwimExperience(skillGain);
            pet.setCoins(pet.getCoins() + coinsCollected);
        }
    }

    /**
     * Returns to the main game screen, stopping the game loop and enabling UI interactions.
     */
    @Override
    protected void returnToGame() {
        // Stop the game loop
        gameLoop.stop();
        // Make the game canvas ignore clicks so the bottom buttons can be used
        gameCanvas.setMouseTransparent(true);
        App.setScreen(new GameScreen(pet));
    }

    // ------------------- INNER CLASSES -------------------

    /**
     * Represents a single standard green obstacle on the swimming surface,
     * with a gap that the pet must jump or dive around.
     */
    private static class Obstacle {
        double x;    // left x‐coordinate
        int gapY;    // top of the gap
        boolean passed;

        Obstacle(double x, int gapY) {
            this.x = x;
            this.gapY = gapY;
            this.passed = false;
        }
    }

    /**
     * Represents a red, rising obstacle that moves both to the left and upward.
     */
    private static class RisingObstacle {
        double x;
        double y;

        RisingObstacle(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Represents a cloud used for background decoration, drifting left over time.
     */
    private static class Cloud {
        double x, y, width, height;

        Cloud(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
