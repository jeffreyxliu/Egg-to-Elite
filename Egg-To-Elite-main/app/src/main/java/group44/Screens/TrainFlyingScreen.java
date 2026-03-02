package group44.Screens;

import group44.App;
import group44.Screens.GameScreen;
import group44.Screens.TrainingScreen;
import group44.Pet;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Training screen for the flying minigame where the pet must navigate through pipes.
 * Flappy Bird-inspired gameplay that improves the pet's flying skill.
 */
public class TrainFlyingScreen extends TrainingScreen {

    /**
     * The constant force of gravity that pulls the pet downward each frame.
     */
    private static final double GRAVITY = 0.1;
    
    /**
     * The upward force applied when the player jumps/flaps.
     * Negative value indicates upward movement in the coordinate system.
     */
    private static final double JUMP_FORCE = -3.0;
    
    /**
     * The width of pipe obstacles in pixels.
     */
    private static final int PIPE_WIDTH = 80;
    
    /**
     * The vertical gap between top and bottom pipes in pixels.
     * Determines the space available for the pet to fly through.
     */
    private static final int PIPE_GAP = 180;
    
    /**
     * The initial horizontal speed of pipes moving from right to left.
     * Measured in pixels per frame.
     */
    private static final int INITIAL_PIPE_SPEED = 3;
    
    /**
     * The current vertical velocity of the pet (bird). Positive means downward movement.
     */
    private double birdVelocity;
    
    /**
     * Collection of all active pipe obstacles in the game.
     */
    private List<Pipe> pipes;
    
    /**
     * The current horizontal speed of pipe movement.
     */
    private double pipeSpeed;
    
    /**
     * The image used to render the pet in flying mode.
     */
    private Image birdImage;
    
    /**
     * Describes the distance between pipes in pixels. 
     * This value may differ if the screen is a tutorial.
     */
    private int distanceBetweenPipes;

    /**
     * Constructor for the flying training screen.
     *
     * @param pet             The pet that will be trained
     * @param isTutorialScreen If true, this is a tutorial with different settings
     */
    public TrainFlyingScreen(Pet pet, boolean isTutorialScreen) {
        super(pet, isTutorialScreen);
        petWidth = 60;
        petHeight = 45;

        if (isTutorialScreen) {
            distanceBetweenPipes = 600;
        } else {
            distanceBetweenPipes = 300;
        }

        // Initialize the game
        setupUI();
        loadResources();
        setupGameVariables();
        setupControls();
        setupGameLoop();
    
        // Start the game
        startGame();

        ScreenClass.playMusic("flying.mp3");
    }
    
    /**
     * Sets up the UI components for the flying training screen.
     * Creates and configures the game canvas, HUD, and navigation buttons.
     */
    @Override
    protected void setupUI() {
        super.setupUI();
        setupHUD("Press SPACE or CLICK to flap wings!");
        setupBottomButtons();
    }
            
    /**
     * Loads the pet's flying image from the pet object.
     */
    private void loadResources() {
        birdImage = pet.getPetFlyingImage();            
    }
    
    /**
     * Sets up the game variables specific to the flying minigame.
     * Initializes the pipes collection and pipe movement speed.
     */
    @Override
    protected void setupGameVariables() {
        super.setupGameVariables();
        pipes = new ArrayList<>();
        pipeSpeed = INITIAL_PIPE_SPEED;
    }
    
    /**
     * Sets up input controls for the minigame:
     * - SPACE key to flap
     * - Mouse click to flap
     * - ENTER key to restart if game is over
     */
    private void setupControls() {
        // Make canvas focusable for keyboard input
        gameCanvas.setFocusTraversable(true);
        
        // Space key to flap
        gameCanvas.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE && isGameRunning && !isGameOver) {
                birdVelocity = JUMP_FORCE;
            } else if (e.getCode() == KeyCode.ENTER && isGameOver) {
                restartGame();
            }
        });
        
        // Mouse click to flap
        gameCanvas.setOnMouseClicked(e -> {
            if (isGameRunning && !isGameOver) {
                birdVelocity = JUMP_FORCE;
            } else if (isGameOver) {
                restartGame();
            }
        });
    }
    
    /**
     * Starts or restarts the game with initial values.
     * Resets pet position, clears obstacles, and sets up initial game state.
     */
    @Override
    protected void startGame() {
        // Reset game state
        petY = canvasHeight / 2 - petHeight / 2;
        birdVelocity = 0;
        pipes.clear();
        coins.clear(); // Clear coins
        score = 0;
        coinsCollected = 0; // Reset coins collected
        coinCounter = 0;
        pipeSpeed = INITIAL_PIPE_SPEED;
        isGameOver = false;
        
        // Add initial pipes
        addPipe(canvasWidth);
        addPipe(canvasWidth + distanceBetweenPipes);
        addPipe(canvasWidth + distanceBetweenPipes * 2);
        
        // Update labels
        scoreLabel.setText("0");
        coinCountLabel.setText("0"); // Reset coin label
        
        // Start animations
        isGameRunning = true;
        gameLoop.start();
        
        // Focus canvas for key events
        gameCanvas.requestFocus();
    }
    
    /**
     * Updates the game state each frame.
     * Handles physics (gravity), collisions, obstacle movement, and score tracking.
     */
    @Override
    protected void updateGame() {
        // Apply gravity to bird
        birdVelocity += GRAVITY;
        petY += birdVelocity;
        
        // Check if bird hit the ceiling or ground
        if (petY <= 0) {
            petY = 0;
            birdVelocity = 0;
        } else if (petY >= canvasHeight - petHeight) {
            endGame(); // Hit the ground
            return;
        }
                
        // Move pipes, check pass, and remove off-screen
        Iterator<Pipe> pipeIterator = pipes.iterator();
        while (pipeIterator.hasNext()) {
            Pipe pipe = pipeIterator.next();
            pipe.x -= pipeSpeed;
            
            // Check if bird passed pipe
            if (!pipe.passed && pipe.x + PIPE_WIDTH < (canvasWidth / 3)) {
                pipe.passed = true;
                score++;
                scoreLabel.setText(String.valueOf(score));
            }
            
            // Remove pipes that are fully off-screen
            if (pipe.x + PIPE_WIDTH < 0) {
                pipeIterator.remove();
            }
        }
        
        // Move coins, check for collection
        Iterator<Coin> coinIterator = coins.iterator();
        while (coinIterator.hasNext()) {
            Coin coin = coinIterator.next();
            coin.x -= pipeSpeed;
            
            // Check if bird collected coin
            if (!coin.collected && checkCoinCollision(coin)) {
                coin.collected = true;
                coinsCollected++;
                coinCountLabel.setText(String.valueOf(coinsCollected));
                ScreenClass.playSound("coin.wav");
            }
            
            // Remove coins that are off-screen or collected
            if (coin.x + coinSize < 0 || coin.collected) {
                coinIterator.remove();
            }
        }

        // Add new pipe when needed
        if (pipes.isEmpty() || pipes.get(pipes.size() - 1).x < canvasWidth - distanceBetweenPipes) {
            addPipe(canvasWidth);
        }
        
        // Check for collisions with pipes
        checkCollisions();
    }
    
    /**
     * Renders the current game state to the canvas.
     * Draws the background, pipes, pet character, coins, etc.
     * Shows a game over overlay if relevant.
     */
    @Override
    protected void renderGame() {
        setupBackground();
        
        // Ground
        gc.setFill(Color.rgb(83, 54, 10));
        gc.fillRect(0, canvasHeight - 50, canvasWidth, 50);
            
        // Grass area
        gc.setFill(Color.rgb(124, 252, 0));
        gc.fillRect(0, canvasHeight - 50, canvasWidth, 10);
        
        // Draw pipes
        for (Pipe pipe : pipes) {
            // Top pipe
            gc.setFill(Color.rgb(0, 128, 0)); // green color for pipes
            gc.fillRect(pipe.x, 0, PIPE_WIDTH, pipe.gapY);
            
            // Bottom pipe
            gc.fillRect(pipe.x, pipe.gapY + PIPE_GAP, 
                        PIPE_WIDTH, canvasHeight - pipe.gapY - PIPE_GAP);
            
            // Pipe caps
            gc.setFill(Color.rgb(0, 150, 0)); // slightly different shade
            gc.fillRect(pipe.x - 5, pipe.gapY - 15, PIPE_WIDTH + 10, 15);
            gc.fillRect(pipe.x - 5, pipe.gapY + PIPE_GAP, PIPE_WIDTH + 10, 15);
        }

        // Draw coins (from TrainingScreen)
        drawCoins();
        
        // Draw bird (pet)
        if (birdImage != null) {
            // Calculate rotation based on velocity
            double rotation = Math.min(Math.max(birdVelocity * 7, -30), 45);
            
            gc.save();
            gc.translate(canvasWidth / 3, petY + petHeight / 2);
            gc.rotate(rotation);
            gc.drawImage(birdImage, -petWidth / 2, -petHeight / 2, petWidth, petHeight);
            gc.restore();
        }
        
        // Draw game over screen if needed
        if (isGameOver) {
            drawGameoverOverlay();
        }
    }
    
    /**
     * Adds a new pipe obstacle at the specified X position.
     * Randomly determines the gap position and occasionally adds a coin.
     *
     * @param x The x-coordinate where the pipe is placed
     */
    private void addPipe(double x) {
        // Random gap position
        int minGapY = 100;
        int maxGapY = (int)(canvasHeight - PIPE_GAP - 100);
        int gapY = random.nextInt(maxGapY - minGapY) + minGapY;
        
        pipes.add(new Pipe(x, gapY));
        
        // Add a coin every 3-5 pipes
        coinCounter++;
        if (coinCounter >= 3 + random.nextInt(3)) { // 3, 4, or 5
            coinCounter = 0;
            
            // Place coin in the center of the gap
            int coinY = gapY + PIPE_GAP / 2 - coinSize / 2;
            coinY += random.nextInt(60) - 30; // random vertical offset
            
            // Place the coin a bit after the pipe
            coins.add(new Coin(x + PIPE_WIDTH + 50 + random.nextInt(100), coinY));
        }
    }
    
    /**
     * Checks if the pet has collided with any pipes.
     * Uses rectangular collision detection for simplicity.
     */
    private void checkCollisions() {
        // Bird (pet) hitbox
        double birdLeft = canvasWidth / 3 - petWidth / 2 + 5;
        double birdRight = canvasWidth / 3 + petWidth / 2 - 5;
        double birdTop = petY + 5;
        double birdBottom = petY + petHeight - 5;
        
        // Collision with each pipe
        for (Pipe pipe : pipes) {
            if (birdRight > pipe.x && birdLeft < pipe.x + PIPE_WIDTH) {
                // Check top pipe
                if (birdTop < pipe.gapY) {
                    endGame();
                    return;
                }
                // Check bottom pipe
                if (birdBottom > pipe.gapY + PIPE_GAP) {
                    endGame();
                    return;
                }
            }
        }
    }
    
    /**
     * Ends the game and calculates skill gain.
     * If not a tutorial, updates pet stats and coins.
     */
    private void endGame() {
        isGameOver = true;
        
        // Calculate skill gain based on score
        skillGain = Math.max(0, score * 2);

        // If not tutorial, award skill experience and coins
        if (!isTutorialScreen) {
            pet.addFlyExperience(skillGain);
            pet.setCoins(pet.getCoins() + coinsCollected);
        }
    }
    
    /**
     * Checks for collision between the pet and a coin.
     * This overrides the parent class method to ensure 
     * the bird's bounding box is used for detection.
     *
     * @param coin The coin to check collision for
     * @return true if pet collides with the coin, false otherwise
     */
    @Override
    protected boolean checkCoinCollision(Coin coin) {
        double birdLeft = canvasWidth / 3 - petWidth / 2 + 5;
        double birdRight = canvasWidth / 3 + petWidth / 2 - 5;
        double birdTop = petY + 5;
        double birdBottom = petY + petHeight - 5;
        
        double coinLeft = coin.x;
        double coinRight = coin.x + coinSize;
        double coinTop = coin.y;
        double coinBottom = coin.y + coinSize;
        
        return (birdRight > coinLeft && birdLeft < coinRight &&
                birdBottom > coinTop && birdTop < coinBottom);
    }

    /**
     * Inner class representing a pipe obstacle.
     * Tracks position, gap placement, and whether 
     * the pet has already passed it.
     */
    private static class Pipe {
        double x;      // x position
        int gapY;      // y position of top of gap
        boolean passed; // whether bird has passed this pipe
        
        /**
         * Creates a new Pipe with a specified position and gap.
         *
         * @param x   The x position of the pipe
         * @param gapY The y position where the pipe gap starts
         */
        Pipe(double x, int gapY) {
            this.x = x;
            this.gapY = gapY;
            this.passed = false;
        }
    }
}
