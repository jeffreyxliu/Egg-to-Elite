package group44.Screens;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import group44.Pet;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

/**
 * A tutorial screen for the swimming minigame, extending {@link TrainingScreen}.
 * Lets the player practice jumping or diving over obstacles without gaining
 * in-game rewards if it's marked as a tutorial.
 */
public class SwimmingTutorialScreen extends TrainingScreen {

    /** Gravitational force pulling the pet back down when jumping. */
    private static final double GRAVITY = 0.3;
    /** Gravitational force pulling the pet upward if diving. */
    private static final double DIVE_GRAVITY = -0.3;
    /** The upward jump force applied when pressing UP. */
    private static final double UP_JUMP_FORCE = -10.5;
    /** The downward dive force applied when pressing DOWN. */
    private static final double DOWN_JUMP_FORCE = 12.0;

    /** The width of each obstacle drawn on the screen. */
    private static final int OBSTACLE_WIDTH = 50;
    /** The vertical gap between the obstacle top and the water surface. */
    private static final int OBSTACLE_GAP = 100;
    /** The initial speed at which obstacles move left across the screen. */
    private static final int INITIAL_OBSTACLE_SPEED = 3;

    /** Flag indicating whether the pet is currently jumping/diving. */
    private boolean jumping = false;
    /**
     * Jump mode:
     * -1 means jumping up,
     * +1 means diving downward.
     */
    private int jumpMode = 0;

    /** The pet's vertical velocity while jumping or diving. */
    private double swimVelocity;
    /** List of green obstacles to dodge. */
    private List<Obstacle> obstacles;
    /** Current obstacle speed, which can vary over time. */
    private double obstacleSpeed;

    /** An Image reference for rendering the pet. */
    private Image petImage;
    /**
     * The horizontal spacing between consecutive obstacles,
     * typically larger for tutorials.
     */
    private int distanceBetweenObstacles;
    /** Random number generator for obstacle positioning. */
    private Random random;

    /**
     * Constructor for SwimmingTutorialScreen.
     *
     * @param pet The pet being trained or practiced with.
     * @param isTutorialScreen True if this screen does not grant rewards/XP.
     */
    public SwimmingTutorialScreen(Pet pet, boolean isTutorialScreen) {
        super(pet, isTutorialScreen);
        petWidth = 30;
        petHeight = 30;

        distanceBetweenObstacles = isTutorialScreen ? 600 : 300;
        random = new Random();

        setupUI();
        loadResources();
        setupGameVariables();
        setupControls();
        setupGameLoop();

        startGame();
    }

    /**
     * Sets up the user interface for the tutorial screen, including HUD text and bottom buttons.
     */
    @Override
    protected void setupUI() {
        super.setupUI();
        setupHUD("Press UP or DOWN to jump or dive!");
        setupBottomButtons();
    }

    /**
     * Loads character or pet-related resources, such as images or audio.
     */
    private void loadResources() {
        petImage = pet.getPetImage();
    }

    /**
     * Initializes game variables including obstacle list and speed.
     */
    @Override
    protected void setupGameVariables() {
        super.setupGameVariables();
        obstacles = new ArrayList<>();
        obstacleSpeed = INITIAL_OBSTACLE_SPEED;
    }

    /**
     * Sets up keypress controls for jumping (UP) or diving (DOWN),
     * and restarts the game if the ENTER key is pressed on game over.
     */
    private void setupControls() {
        gameCanvas.setFocusTraversable(true);

        gameCanvas.setOnKeyPressed(e -> {
            if (!jumping && isGameRunning && !isGameOver && (petY + petHeight >= canvasHeight / 2)) {
                if (e.getCode() == KeyCode.UP) {
                    jumping = true;
                    jumpMode = -1;
                    swimVelocity = UP_JUMP_FORCE;
                } else if (e.getCode() == KeyCode.DOWN) {
                    jumping = true;
                    jumpMode = 1;
                    swimVelocity = DOWN_JUMP_FORCE;
                }
            } else if (e.getCode() == KeyCode.ENTER && isGameOver) {
                restartGame();
            }
        });
    }

    /**
     * Begins the tutorial run, placing initial obstacles and resetting stats.
     */
    @Override
    protected void startGame() {
        petX = canvasWidth / 3.0; 
        petY = canvasHeight / 2 - petHeight;
        swimVelocity = 0;
        obstacles.clear();
        coins.clear();
        score = 0;
        coinsCollected = 0;
        coinCounter = 0;
        obstacleSpeed = INITIAL_OBSTACLE_SPEED;
        isGameOver = false;

        addObstacle(canvasWidth);
        addObstacle(canvasWidth + distanceBetweenObstacles);
        addObstacle(canvasWidth + distanceBetweenObstacles * 2);

        scoreLabel.setText("0");
        coinCountLabel.setText("0");

        isGameRunning = true;
        gameLoop.start();
        gameCanvas.requestFocus();
    }

    /**
     * Updates game logic, including the pet's jumping or diving mechanics,
     * obstacle movement, coin collection, and collision checks.
     */
    @Override
    protected void updateGame() {
        if (jumping) {
            if (jumpMode == 1) { // dive
                swimVelocity += DIVE_GRAVITY;
                petY += swimVelocity;

                if (swimVelocity < 0 && petY <= (canvasHeight / 2 - petHeight)) {
                    petY = canvasHeight / 2 - petHeight;
                    swimVelocity = 0;
                    jumping = false;
                }

                if (petY > canvasHeight - petHeight) {
                    endGame();
                    return;
                }
            } else if (jumpMode == -1) { // up jump
                swimVelocity += GRAVITY;
                petY += swimVelocity;

                if (swimVelocity > 0 && petY >= (canvasHeight / 2 - petHeight)) {
                    petY = canvasHeight / 2 - petHeight;
                    swimVelocity = 0;
                    jumping = false;
                }
            }
        } else {
            petY = canvasHeight / 2 - petHeight;
            swimVelocity = 0;
        }

        Iterator<Obstacle> obsIterator = obstacles.iterator();
        while (obsIterator.hasNext()) {
            Obstacle obs = obsIterator.next();
            obs.x -= obstacleSpeed;

            if (!obs.passed && obs.x + OBSTACLE_WIDTH < (canvasWidth / 3)) {
                obs.passed = true;
                score++;
                scoreLabel.setText(String.valueOf(score));
            }

            if (obs.x + OBSTACLE_WIDTH < 0) {
                obsIterator.remove();
            }
        }

        Iterator<Coin> coinIterator = coins.iterator();
        while (coinIterator.hasNext()) {
            Coin coin = coinIterator.next();
            coin.x -= obstacleSpeed;

            if (!coin.collected && checkCoinCollision(coin)) {
                coin.collected = true;
                coinsCollected++;
                coinCountLabel.setText(String.valueOf(coinsCollected));
                ScreenClass.playSound("coin.mp3");
            }

            if (coin.x + coinSize < 0 || coin.collected) {
                coinIterator.remove();
            }
        }

        if (obstacles.isEmpty() || obstacles.get(obstacles.size() - 1).x < canvasWidth - distanceBetweenObstacles) {
            addObstacle(canvasWidth);
        }

        checkCollisions();
    }

    /**
     * Renders the tutorial screen, including your pet, obstacles, coins, 
     * and water background. Also draws an overlay if the game is over.
     */
    @Override
    protected void renderGame() {
        setupBackground();

        // Draw water
        gc.setFill(Color.rgb(0, 105, 148));
        gc.fillRect(0, canvasHeight / 2, canvasWidth, canvasHeight / 2);

        // Draw obstacles
        for (Obstacle obs : obstacles) {
            gc.setFill(Color.FORESTGREEN);
            double obstacleHeight = canvasHeight - obs.gapY;
            gc.fillRect(obs.x, obs.gapY, OBSTACLE_WIDTH, obstacleHeight);
        }

        // Draw coins (inherited from TrainingScreen)
        drawCoins();

        // Draw pet image with slight rotation
        if (petImage != null) {
            double rotation = Math.min(Math.max(swimVelocity * 5, -20), 20);
            gc.save();
            gc.translate(canvasWidth / 3, petY + petHeight / 2);
            gc.rotate(rotation);
            gc.drawImage(petImage, -petWidth / 2, -petHeight / 2, petWidth, petHeight);
            gc.restore();
        }

        if (isGameOver) {
            drawGameoverOverlay();
        }
    }

    /**
     * Creates and positions a new obstacle based on random values for its vertical gap,
     * occasionally adding a coin behind it.
     *
     * @param x The x-position where this obstacle appears.
     */
    private void addObstacle(double x) {
        double maxJumpHeight = (Math.pow(-UP_JUMP_FORCE, 2)) / (2 * GRAVITY); // ~183
        int maxObstacleTop = (int) (canvasHeight / 2 - maxJumpHeight + petHeight);

        int minGapY = Math.max((int) (canvasHeight / 2 + 20), maxObstacleTop);
        int maxGapY = (int) (canvasHeight / 2 + OBSTACLE_GAP);
        int gapY = random.nextInt(maxGapY - minGapY + 1) + minGapY;

        int obstacleTopY = gapY - OBSTACLE_GAP;
        obstacles.add(new Obstacle(x, obstacleTopY));

        coinCounter++;
        if (coinCounter >= 3 + random.nextInt(3)) {
            coinCounter = 0;
            int coinY = gapY - coinSize / 2 + random.nextInt(60) - 30;
            coins.add(new Coin(x + OBSTACLE_WIDTH + 50 + random.nextInt(100), coinY));
        }
    }

    /**
     * Checks for collisions between the pet and any standard obstacles, ending
     * the tutorial if one occurs.
     */
    private void checkCollisions() {
        double petLeft = canvasWidth / 3 - petWidth / 2 + 5;
        double petRight = canvasWidth / 3 + petWidth / 2 - 5;
        double petTop = petY + 5;
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
     * Ends the tutorial run, awarding no rewards if {@code isTutorialScreen} 
     * is true, or awarding coins and experience for normal runs.
     */
    private void endGame() {
        isGameOver = true;
        skillGain = Math.max(0, score * 2);
        if (!isTutorialScreen) {
            pet.addSwimExperience(skillGain);
            pet.setCoins(pet.getCoins() + coinsCollected);
        }
    }

    // --------------------- INNER CLASSES ---------------------
    /**
     * A static inner class representing a single green obstacle in the tutorial.
     */
    private static class Obstacle {
        double x;
        int gapY;
        boolean passed;

        /**
         * Constructs an Obstacle with a given x-position and gap starting point.
         *
         * @param x The left edge of the obstacle.
         * @param gapY The vertical position of the gap, measured from the top.
         */
        Obstacle(double x, int gapY) {
            this.x = x;
            this.gapY = gapY;
            this.passed = false;
        }
    }
}
