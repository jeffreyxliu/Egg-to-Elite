package group44.Screens;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import group44.App;
import group44.Food;
import group44.Pet;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * The GameScreen class represents the main gameplay screen where the pet's
 * current state, skills, and actions are displayed and updated in real time.
 * This class extends ScreenClass and handles UI updates, timers, pet interactions,
 * and saving/loading of game state.
 */
public class GameScreen extends ScreenClass {

    // Static timer references used to periodically update pet stats
    private static Timeline hungerTimer;
    private static Timeline sleepTimer;
    private static Timeline happinessTimer;
    public static boolean timersInitialized = false;
    
    // Instance variables for UI components and pet state tracking
    private Label petNameLabel;
    private ProgressBar healthBar, sleepBar, happinessBar, fullnessBar;
    private Label coinCountLabel;
    private ProgressBar runningExpBar, swimmingExpBar, flyingExpBar;
    private Label runningLevelLabel, swimmingLevelLabel, flyingLevelLabel;
    
    private boolean isPetSleeping = false;
    private boolean isPetDead = false;

    private ImageView petImageView;
    private Timeline spriteFlipTimer;
    private boolean isSpriteFlipped = false;
    private Label statusLabel; // Status label for displaying messages

    private boolean timerStarted = false;

    Label scoreLabel;
    Timeline scoreTimer;

    private int currentSaveSlot = 0; // 0 indicates that the game has not been saved yet

    /**
     * Constructs a new GameScreen with the specified pet.
     *
     * @param pet the pet whose state will be managed and displayed on this screen
     */
    public GameScreen(Pet pet) {
        this.pet = pet;

        // Store the pet name for use by RunningTutorialScreen
        App.setGameScreenPetName(pet.getName());
        
        root.setPadding(new Insets(20));
        // Load the background image
        Image backgroundImage = new Image("sunny1.jpg"); // Replace with your image file path
    
        // Create a BackgroundImage object
        BackgroundImage background = new BackgroundImage(
            backgroundImage,
            BackgroundRepeat.NO_REPEAT, // Do not repeat the image
            BackgroundRepeat.NO_REPEAT, // Do not repeat the image
            BackgroundPosition.CENTER,  // Center the image
            new BackgroundSize(
                1.0, 1.0,                 // Scale width and height to 100% of the screen
                true, true,               // Use percentages for width and height
                false, false              // Do not preserve aspect ratio; stretch to fit
            )
        );
    
        // Set the background to the root layout
        root.setBackground(new Background(background));

        // Left section: Current state AND skills (combined)
        VBox leftSideBox = new VBox(20); // Spacing between sections
        VBox currentStateBox = createCurrentStateBox();
        VBox skillsBox = createSkillsBox();
        leftSideBox.getChildren().addAll(currentStateBox, skillsBox);
        root.setLeft(leftSideBox);

        // Center section: Pet display and customization
        VBox petDisplayBox = createPetDisplayBox(pet.getName(), pet.getPetImage());
        root.setCenter(petDisplayBox);

        // Right section: Actions only (skills moved to left)
        VBox actionsBox = createActionsBox();
        root.setRight(actionsBox);
        
        // Create and add the status label to the right panel
        statusLabel = new Label("");
        statusLabel.setTextFill(Color.RED);
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        actionsBox.getChildren().add(statusLabel);
        
        // Score panel setup
        VBox scoreBox = createActionsBox();
        root.setRight(scoreBox);
        scoreLabel = new Label("Score: " + pet.getScore());
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        scoreLabel.setTextFill(Color.BLACK);
        scoreBox.getChildren().add(scoreLabel);    
        
        if (!timerStarted) {
            initializeScoreTimer();
        }

        // Bottom section: Control and training buttons layout
        BorderPane bottomPane = new BorderPane();
        
        // Create control buttons and place them at the right of the bottom pane
        HBox controlButtonsBox = createControlButtonsBox();
        bottomPane.setRight(controlButtonsBox);

        // Create the training buttons and center them in the bottom pane
        HBox trainingBox = createTrainingBox(pet.getPetImage());
        bottomPane.setCenter(trainingBox);
                
        // Set the bottom pane as the bottom of the main layout
        root.setBottom(bottomPane);
        
        // Update all UI elements with pet's current stats
        updateStats();
        
        // Initialize and start timers only once
        initializeTimers();
        
        ScreenClass.playMusic("game_screen.mp3");
    }

    /**
     * Updates the pet's happiness by decrementing its value and applying effects
     * if the pet becomes too unhappy.
     *
     * @param decrement the amount to reduce the pet's happiness by
     */
    private void updateHappiness(int decrement) {
        // Ensure that the current screen is an instance of GameScreen
        if (App.getCurrentScreen() instanceof GameScreen) {
            GameScreen currentGameScreen = (GameScreen) App.getCurrentScreen();
            Pet currentPet = currentGameScreen.pet;
            
            currentPet.setHappiness(Math.max(0, currentPet.getHappiness() - decrement));
            
            // Check for critical happiness
            if (currentPet.getHappiness() < 25) {
                if (currentPet.getHappiness() <= 0) {
                    // Disable commands that do not increase pet happiness
                    disableInteractionButtons(true, false, true);

                    // Change to sad sprite
                    Image sadImage = new Image(pet.getSpriteFileNameBase() + "_sad.png");
                    petImageView.setImage(sadImage);
                    
                    // Update UI to show pet is sad
                    petNameLabel.setText(pet.getName() + " (Sad)");
                    petNameLabel.setTextFill(Color.DARKBLUE);
                }
            } else {
                // Clear status message if pet is happy enough
                currentGameScreen.statusLabel.setText("");
            }
        }
    }
    
    /**
     * Updates the pet's hunger by decrementing its fullness and applying related effects.
     *
     * @param decrement the amount to reduce the pet's fullness by
     */
    private void updateHunger(int decrement) {
        if (App.getCurrentScreen() instanceof GameScreen) {
            GameScreen currentGameScreen = (GameScreen) App.getCurrentScreen();
            Pet currentPet = currentGameScreen.pet;
            
            currentPet.setFullness(Math.max(0, currentPet.getFullness() - decrement));
            
            // Check for critical hunger
            if (currentPet.getFullness() < 25) {
                // Visual indicator that pet is hungry
                currentGameScreen.petNameLabel.setTextFill(Color.RED);
                
                // Change to hungry sprite
                Image hungryImage = new Image(pet.getSpriteFileNameBase() + "_hungry.png");
                petImageView.setImage(hungryImage);

                // Update UI to show pet is hungry
                petNameLabel.setText(pet.getName() + " (Hungry)");
                petNameLabel.setTextFill(Color.DARKBLUE);

                if (currentPet.getFullness() < 10) {
                    // Pet loses stamina when severely hungry
                    currentPet.setStamina(Math.max(0, currentPet.getStamina() - 1));
                }
            } else {
                currentGameScreen.petNameLabel.setTextFill(Color.rgb(30, 100, 200));
            }
            if (currentPet.getFullness() <= 0) {
                currentPet.setHappiness(Math.max(0, currentPet.getHappiness() - 1));
                currentPet.setHealth(Math.max(0, currentPet.getHealth() - decrement));
            }
        }
    }

    /**
     * Updates the pet's sleepiness by decrementing or incrementing its sleepiness based on its state.
     *
     * @param decrement the amount to decrement sleepiness by when the pet is awake, or apply health penalty when necessary
     */
    private void updateSleepiness(int decrement) {
        if (App.getCurrentScreen() instanceof GameScreen) {
            GameScreen currentGameScreen = (GameScreen) App.getCurrentScreen();
            Pet currentPet = currentGameScreen.pet;
            
            if (!currentGameScreen.isPetSleeping) {
                // Decrease sleepiness when the pet is awake
                currentPet.setSleepiness(Math.max(0, currentPet.getSleepiness() - decrement));
                
                // Check if pet should fall asleep due to exhaustion
                if (currentPet.getSleepiness() <= 0) {
                    currentPet.setHealth(Math.max(0, currentPet.getHealth() - decrement));
                    currentGameScreen.makePetSleep();
                }
            } else {
                // Increase sleep value when pet is sleeping
                currentPet.setSleepiness(Math.min(100, currentPet.getSleepiness() + 3));
                
                // Wake the pet when fully rested
                if (currentPet.getSleepiness() >= 100) {
                    currentGameScreen.wakePet();
                }
            }
        }
    }

    /**
     * Initializes the static timers for hunger, sleepiness, and happiness updates.
     * This method ensures that the timers are only created once.
     */
    private void initializeTimers() {
        if (!timersInitialized) {
            // Initialize hunger timer
            hungerTimer = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> {
                    Platform.runLater(() -> {
                        updateHunger(1);
                        if (App.getCurrentScreen() instanceof GameScreen) {
                            ((GameScreen) App.getCurrentScreen()).updateStats();
                        }
                    });
                })
            );
            
            // Initialize sleep timer
            sleepTimer = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> {
                    Platform.runLater(() -> {
                        updateSleepiness(1);
                        if (App.getCurrentScreen() instanceof GameScreen) {
                            ((GameScreen) App.getCurrentScreen()).updateStats();
                        }
                    });
                })
            );

            // Initialize happiness timer
            happinessTimer = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> {
                    Platform.runLater(() -> {
                        updateHappiness(1);
                        if (App.getCurrentScreen() instanceof GameScreen) {
                            ((GameScreen) App.getCurrentScreen()).updateStats();
                        }
                    });
                })
            );
            
            // Set timers to run indefinitely
            hungerTimer.setCycleCount(Timeline.INDEFINITE);
            sleepTimer.setCycleCount(Timeline.INDEFINITE);
            happinessTimer.setCycleCount(Timeline.INDEFINITE);
            
            hungerTimer.play();
            sleepTimer.play();
            happinessTimer.play();
            
            timersInitialized = true;
        }
    }
    
    /**
     * Stops all static timers for hunger, sleepiness, and happiness.
     * This method should be called when exiting the game or when timers are no longer needed.
     */
    public static void stopAllTimers() {
        if (hungerTimer != null) {
            hungerTimer.stop();
        }
        if (sleepTimer != null) {
            sleepTimer.stop();
        }
        if (happinessTimer != null) {
            happinessTimer.stop();
        }
    }

    /**
     * Creates a VBox containing the current state of the pet including health, sleep, happiness, and fullness.
     *
     * @return a VBox with UI elements displaying the pet's current state
     */
    private VBox createCurrentStateBox() {
        VBox currentStateBox = new VBox(15);
        currentStateBox.setPadding(new Insets(20));
        currentStateBox.setAlignment(Pos.TOP_LEFT);

        Label currentStateLabel = new Label("Current State");
        currentStateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // Health box with progress bar
        VBox healthBox = new VBox(5);
        Label healthLabel = new Label("Health: " + pet.getStamina());
        healthBar = new ProgressBar(pet.getStamina() / 100.0);
        healthBar.setPrefWidth(150);
        healthBar.setStyle("-fx-accent: #4CAF50;");
        healthBox.getChildren().addAll(healthLabel, healthBar);
        
        // Sleep box with progress bar
        VBox sleepBox = new VBox(5);
        Label sleepLabel = new Label("Sleep: " + pet.getSleepiness());
        sleepBar = new ProgressBar(pet.getSleepiness() / 100.0);
        sleepBar.setPrefWidth(150);
        sleepBar.setStyle("-fx-accent: #4CAF50;");
        sleepBox.getChildren().addAll(sleepLabel, sleepBar);
        
        // Happiness box with progress bar
        VBox happinessBox = new VBox(5);
        Label happinessLabel = new Label("Happiness: " + pet.getHappiness());
        happinessBar = new ProgressBar(pet.getHappiness() / 100.0);
        happinessBar.setPrefWidth(150);
        happinessBar.setStyle("-fx-accent: #4CAF50;");
        happinessBox.getChildren().addAll(happinessLabel, happinessBar);
        
        // Fullness box with progress bar
        VBox fullnessBox = new VBox(5);
        Label fullnessLabel = new Label("Fullness: " + pet.getFullness());
        fullnessBar = new ProgressBar(pet.getFullness() / 100.0);
        fullnessBar.setPrefWidth(150);
        fullnessBar.setStyle("-fx-accent: #4CAF50;");
        fullnessBox.getChildren().addAll(fullnessLabel, fullnessBar);

        currentStateBox.getChildren().addAll(
            currentStateLabel, healthBox, sleepBox, happinessBox, fullnessBox
        );

        return currentStateBox;
    }

    /**
     * Creates a VBox to display the pet's name and image.
     *
     * @param petName the name of the pet to display
     * @param petImage the image representing the pet
     * @return a VBox containing the pet display elements
     */
    private VBox createPetDisplayBox(String petName, Image petImage) {
        VBox petDisplayBox = new VBox(15);
        petDisplayBox.setAlignment(Pos.CENTER);

        petNameLabel = new Label(petName);
        petNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        petNameLabel.setTextFill(Color.rgb(30, 100, 200));

        petImageView = new ImageView(petImage);
        petImageView.setFitWidth(200);
        petImageView.setFitHeight(200);
        petImageView.setPreserveRatio(true);

        petDisplayBox.getChildren().addAll(petNameLabel, petImageView);
        
        // Start sprite flipping animation for idle effect
        startSpriteFlipTimer();
        
        return petDisplayBox;
    }

    /**
     * Creates a timer that flips the pet sprite horizontally every 5 seconds
     * for a simple idle animation effect.
     */
    private void startSpriteFlipTimer() {
        spriteFlipTimer = new Timeline(
            new KeyFrame(Duration.seconds(5), e -> {
                // Only flip sprite if the pet is awake
                if (!isPetSleeping) {
                    isSpriteFlipped = !isSpriteFlipped;
                    if (isSpriteFlipped) {
                        petImageView.setScaleX(-1); // Flip horizontally
                    } else {
                        petImageView.setScaleX(1);  // Normal orientation
                    }
                }
            })
        );
        
        spriteFlipTimer.setCycleCount(Timeline.INDEFINITE);
        spriteFlipTimer.play();
    }

    /**
     * Creates a VBox containing interaction buttons such as "Take to Vet", "Sleep",
     * "Shop", and "Play", as well as a coin count label.
     *
     * @return a VBox with UI elements for pet interactions
     */
    private VBox createActionsBox() {
        VBox actionsBox = new VBox(15);
        actionsBox.setPadding(new Insets(20));
        actionsBox.setAlignment(Pos.TOP_RIGHT);

        Button vetButton = createStyledButton("Take to Vet");
        VetScreen vetScreen = new VetScreen(pet);
        vetButton.setOnAction(e -> App.setScreen(vetScreen));

        Button sleepButton = createStyledButton("Sleep");
        sleepButton.setOnAction(e -> putPetToSleep());

        Button shopButton = createStyledButton("Shop");
        shopButton.setOnAction(e -> App.setScreen(new ShopScreen(pet)));
        
        Button playButton = createStyledButton("Play");
        playButton.setOnAction(e -> petPlay());

        coinCountLabel = new Label("Coins: " + pet.getCoins());
        coinCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        actionsBox.getChildren().addAll(
            vetButton, 
            sleepButton, 
            coinCountLabel, 
            shopButton,
            playButton
        );

        return actionsBox;
    }

    /**
     * Creates a VBox displaying the pet's skills and corresponding progress bars.
     *
     * @return a VBox with UI elements for displaying pet skills
     */
    private VBox createSkillsBox() {
        VBox skillsBox = new VBox(15);
        skillsBox.setPadding(new Insets(20));
        skillsBox.setAlignment(Pos.TOP_LEFT);
        
        Label skillsTitle = new Label("Skills");
        skillsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        // Running skill box
        VBox runningBox = new VBox(5);
        runningLevelLabel = new Label("Running: Lvl " + pet.getRunLevel());
        runningExpBar = new ProgressBar((float)pet.getRunExperience() / (pet.getRunLevel() * pet.getRunLevel()));
        runningExpBar.setPrefWidth(150);
        runningExpBar.setStyle("-fx-accent: #4CAF50;");
        runningBox.getChildren().addAll(runningLevelLabel, runningExpBar);
        
        // Swimming skill box
        VBox swimmingBox = new VBox(5);
        swimmingLevelLabel = new Label("Swimming: Lvl " + pet.getSwimLevel());
        swimmingExpBar = new ProgressBar((float)pet.getSwimExperience() / (pet.getSwimLevel() * pet.getSwimLevel()));
        swimmingExpBar.setPrefWidth(150);
        swimmingExpBar.setStyle("-fx-accent: #2196F3;");
        swimmingBox.getChildren().addAll(swimmingLevelLabel, swimmingExpBar);
        
        // Flying skill box
        VBox flyingBox = new VBox(5);
        flyingLevelLabel = new Label("Flying: Lvl " + pet.getFlyLevel());
        flyingExpBar = new ProgressBar((float)pet.getFlyExperience() / (pet.getFlyLevel() * pet.getFlyLevel()));
        flyingExpBar.setPrefWidth(150);
        flyingExpBar.setStyle("-fx-accent: #FF9800;");
        flyingBox.getChildren().addAll(flyingLevelLabel, flyingExpBar);
        
        // Tooltip label encouraging training
        Label tooltipLabel = new Label("Train to level up skills!");
        tooltipLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        tooltipLabel.setTextFill(Color.BLACK);
        tooltipLabel.setAlignment(Pos.CENTER);
        
        skillsBox.getChildren().addAll(
            skillsTitle, runningBox, swimmingBox, flyingBox, tooltipLabel
        );
        
        return skillsBox;
    }
    
    /**
     * Creates an HBox containing training buttons for running, swimming, and flying,
     * as well as tutorial buttons for each respective skill.
     *
     * @param petImage the pet image used for display during training
     * @return an HBox with UI elements for training actions
     */
    private HBox createTrainingBox(Image petImage) {
        HBox trainingBox = new HBox(15);
        trainingBox.setPadding(new Insets(20));
        trainingBox.setAlignment(Pos.CENTER);

        Button trainRunningButton = createStyledButton("Train Running");
        trainRunningButton.setOnAction(e -> trainRunning());

        Button trainSwimmingButton = createStyledButton("Train Swimming");
        trainSwimmingButton.setOnAction(e -> trainSwimming());

        Button trainFlyingButton = createStyledButton("Train Flying");
        trainFlyingButton.setOnAction(e -> trainFlying());

        Button runningTutorialButton = createStyledButton("Running Tutorial");
        runningTutorialButton.setOnAction(e -> App.setScreen(new RunningTutorialScreen(pet, true)));

        Button swimmingTutorialButton = createStyledButton("Swimming Tutorial");
        swimmingTutorialButton.setOnAction(e -> showSwimmingTutorial());

        Button flyingTutorialButton = createStyledButton("Flying Tutorial");
        flyingTutorialButton.setOnAction(e -> showFlyingTutorial());

        trainingBox.getChildren().addAll(
            trainRunningButton, trainSwimmingButton, trainFlyingButton,
            runningTutorialButton, swimmingTutorialButton, flyingTutorialButton
        );

        return trainingBox;
    }

    /**
     * Creates an HBox containing control buttons for saving the game and quitting to the menu.
     *
     * @return an HBox with UI elements for game control actions
     */
    private HBox createControlButtonsBox() {
        HBox controlButtonsBox = new HBox(10);
        controlButtonsBox.setPadding(new Insets(20, 20, 20, 0));
        controlButtonsBox.setAlignment(Pos.CENTER_RIGHT);

        // Label to display the save confirmation message
        Label saveMessageLabel = new Label("");
        saveMessageLabel.setTextFill(Color.BLACK);
        saveMessageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        saveMessageLabel.setVisible(false);

        // Save Game Button
        Button saveButton = createStyledButton("Save Game");
        saveButton.setOnAction(e -> {
            saveGame();
            saveMessageLabel.setText("Game saved successfully!");
            saveMessageLabel.setVisible(true);
            Timeline hideMessageTimeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
                saveMessageLabel.setVisible(false);
            }));
            hideMessageTimeline.setCycleCount(1);
            hideMessageTimeline.play();
        });

        // Quit to Menu Button
        Button quitButton = createStyledButton("Quit to Menu");
        quitButton.setOnAction(e -> {
            if (spriteFlipTimer != null) {
                spriteFlipTimer.stop();
            }
            App.setScreen(new MainMenu());
        });

        VBox saveBox = new VBox(5);
        saveBox.setAlignment(Pos.CENTER_RIGHT);
        saveBox.getChildren().addAll(saveMessageLabel, saveButton, quitButton);

        controlButtonsBox.getChildren().add(saveBox);

        return controlButtonsBox;
    }

    /**
     * Creates a styled button with predefined CSS styling for the GameScreen.
     *
     * @param text the text to display on the button
     * @return a styled Button instance
     */
    @Override
    protected Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: #4286f4; -fx-text-fill: white; -fx-font-size: 14px; " +
            "-fx-background-radius: 10; -fx-padding: 10;"
        );
        return button;
    }

    /**
     * Saves the current game state to a CSV file based on the available save slot.
     */
    private void saveGame() {
        int nextSaveSlot = getNextAvailableSaveSlot();

        if (isLoadedGame) {
            nextSaveSlot = loadedSlot;
        } else if (currentSaveSlot == 0) {
            currentSaveSlot = nextSaveSlot;
        } else if (currentSaveSlot != 0) {
            nextSaveSlot = currentSaveSlot;
        } else if (nextSaveSlot == 0) {
            return;
        }
        
        String slotName = "LoadGameSlot" + nextSaveSlot;
        String filePath = "GameSaves/" + slotName + ".csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(
                pet.getSpriteFileNameBase() + "," +
                pet.getName() + "," +
                pet.getSleepiness() + "," +
                pet.getHappiness() + "," +
                pet.getFullness() + "," +
                pet.getHealth() + "," +
                pet.getStamina() + "," +
                pet.getScore() + "," +
                pet.getRunLevel() + "," +
                pet.getRunExperience() + "," +
                pet.getSwimLevel() + "," +
                pet.getSwimExperience() + "," +
                pet.getFlyLevel() + "," +
                pet.getFlyExperience() + "," +
                pet.getState() + "," +
                pet.getCoins()
            );
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    /**
     * Determines the next available save slot by checking for non-existent save files.
     *
     * @return the number of the next available save slot, or 0 if none are available
     */
    private int getNextAvailableSaveSlot() {
        for (int i = 1; i < 4; i++) {
            String filePath = "GameSaves/LoadGameSlot" + i + ".csv";
            if (!new java.io.File(filePath).exists()) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Puts the pet to sleep manually, increases sleepiness, and updates UI accordingly.
     */
    private void putPetToSleep() {
        if (isPetSleeping) {
            return;
        }
        
        ScreenClass.playSound("click.mp3");
        pet.setSleepiness(Math.min(100, pet.getSleepiness() + 20));
        updateStats();
        makePetSleep();
    }

    /**
     * Increases the pet's happiness when the pet plays and updates the UI.
     */
    private void petPlay() {
        ScreenClass.playSound("click.mp3");
        pet.setHappiness(Math.min(100, pet.getHappiness() + 20));
        updateStats();
    }

    /**
     * Navigates to the TrainRunningScreen for training the running skill.
     */
    private void trainRunning() {
        TrainRunningScreen trainRunningScreen = new TrainRunningScreen(pet, false);
        App.setScreen(trainRunningScreen);
    }

    /**
     * Navigates to the SwimmingScreen for training the swimming skill.
     */
    private void trainSwimming() {
        SwimmingScreen trainSwimmingScreen = new SwimmingScreen(pet, false);
        App.setScreen(trainSwimmingScreen);
    }

    /**
     * Navigates to the TrainFlyingScreen for training the flying skill.
     */
    private void trainFlying() {
        TrainFlyingScreen trainFlyingScreen = new TrainFlyingScreen(pet, false);
        App.setScreen(trainFlyingScreen);
    }

    /**
     * Navigates to the running tutorial screen.
     */
    private void showRunningTutorial() {
        TrainRunningScreen runningTutorialScreen = new TrainRunningScreen(pet, true);
        App.setScreen(runningTutorialScreen);
    }

    /**
     * Navigates to the swimming tutorial screen.
     */
    private void showSwimmingTutorial() {
        SwimmingTutorialScreen trainSwimmingScreenTutorial = new SwimmingTutorialScreen(pet, true);
        App.setScreen(trainSwimmingScreenTutorial);
    }

    /**
     * Navigates to the flying tutorial screen.
     */
    private void showFlyingTutorial() {
        TrainFlyingScreen trainFlyingScreenTutorial = new TrainFlyingScreen(pet, true);
        App.setScreen(trainFlyingScreenTutorial);
    }
    
    /**
     * Updates the pet's vital statistics and skill progress, and handles pet death.
     */
    public void updateStats() {
        updateHunger(0);
        updateHappiness(0);
        updateSleepiness(0);

        if (pet.getHealth() <= 0) {
            killPet();
        }

        // Update health progress bar and label
        healthBar.setProgress(pet.getHealth() / 100.0);
        ((Label)((VBox)healthBar.getParent()).getChildren().get(0)).setText("Health: " + pet.getHealth());
        if (pet.getHealth() <= 25) {
            healthBar.setStyle("-fx-accent: #FF0000;");
        } else {
            healthBar.setStyle("-fx-accent: #4CAF50;");
        }

        // Update sleep progress bar and label
        sleepBar.setProgress(pet.getSleepiness() / 100.0);
        ((Label)((VBox)sleepBar.getParent()).getChildren().get(0)).setText("Sleep: " + pet.getSleepiness());
        if (pet.getSleepiness() <= 25) {
            sleepBar.setStyle("-fx-accent: #FF0000;");
        } else {
            sleepBar.setStyle("-fx-accent: #4CAF50;");
        }

        // Update happiness progress bar and label
        happinessBar.setProgress(pet.getHappiness() / 100.0);
        ((Label)((VBox)happinessBar.getParent()).getChildren().get(0)).setText("Happiness: " + pet.getHappiness());
        if (pet.getHappiness() <= 25) {
            happinessBar.setStyle("-fx-accent: #FF0000;");
        } else {
            happinessBar.setStyle("-fx-accent: #4CAF50;");
        }

        // Update fullness progress bar and label
        fullnessBar.setProgress(pet.getFullness() / 100.0);
        ((Label)((VBox)fullnessBar.getParent()).getChildren().get(0)).setText("Fullness: " + pet.getFullness());
        if (pet.getFullness() <= 25) {
            fullnessBar.setStyle("-fx-accent: #FF0000;");
        } else {
            fullnessBar.setStyle("-fx-accent: #4CAF50;");
        }
        
        // Update skill levels and experience progress bars
        runningLevelLabel.setText("Running: Lvl " + pet.getRunLevel());
        runningExpBar.setProgress((float)pet.getRunExperience() / (pet.getRunLevel() * pet.getRunLevel()));
        
        swimmingLevelLabel.setText("Swimming: Lvl " + pet.getSwimLevel());
        swimmingExpBar.setProgress((float)pet.getSwimExperience() / (pet.getSwimLevel() * pet.getSwimLevel()));
        
        flyingLevelLabel.setText("Flying: Lvl " + pet.getFlyLevel());
        flyingExpBar.setProgress((float)pet.getFlyExperience() / (pet.getFlyLevel() * pet.getFlyLevel()));
    }

    /**
     * Feeds the pet with a basic food item if the pet has enough coins,
     * and updates the pet's fullness and coin count.
     */
    private void feedPet() {
        Food food = new Food("basic food", 10, 5, 5, "path/to/icon.png");
        if (pet.getCoins() >= food.getCost()) {
            pet.setFullness(Math.min(100, pet.getFullness() + food.getNutrition()));
            pet.setCoins(pet.getCoins() - food.getCost());
            statusLabel.setText("");
            updateStats();
            coinCountLabel.setText("Coins: " + pet.getCoins());
        } else {
            statusLabel.setText("Not enough coins!");
        }
    }

    /**
     * Puts the pet to sleep by changing its state, updating the sprite,
     * and disabling interaction buttons.
     */
    private void makePetSleep() {
        if (isPetSleeping) {
            return;
        }
        
        isPetSleeping = true;
        
        // Change to sleeping sprite
        Image sleepingImage = new Image(pet.getSpriteFileNameBase() + "_sleeping.png");
        petImageView.setImage(sleepingImage);
        
        // Update UI to indicate pet is sleeping
        petNameLabel.setText(pet.getName() + " (Sleeping)");
        petNameLabel.setTextFill(Color.DARKBLUE);
                
        // Disable interaction buttons while sleeping
        disableInteractionButtons(true, true, false);
    }

    /**
     * Wakes the pet up by restoring its normal state, updating its sprite,
     * and re-enabling interaction buttons.
     */
    private void wakePet() {
        if (!isPetSleeping) {
            return;
        }
        
        isPetSleeping = false;
        
        // Restore normal sprite
        Image normalImage = pet.getPetImage();
        petImageView.setImage(normalImage);
        
        // Update UI to indicate pet is awake
        petNameLabel.setText(pet.getName());
        petNameLabel.setTextFill(Color.rgb(30, 100, 200));
                
        // Re-enable interaction buttons
        disableInteractionButtons(false, true, false);
    }

    /**
     * Enables or disables interaction and training buttons based on the pet's state.
     *
     * @param disable if true, disables the buttons; if false, enables them
     * @param allowSleep if true, allows the sleep button to remain active
     * @param allowShop if true, allows the shop button to remain active
     */
    private void disableInteractionButtons(boolean disable, boolean allowSleep, boolean allowShop) {
        // Disable or enable interaction buttons in the right panel
        VBox actionsBox = (VBox) root.getRight();
        for (int i = 0; i < actionsBox.getChildren().size(); i++) {
            if (actionsBox.getChildren().get(i) instanceof Button) {
                Button button = (Button) actionsBox.getChildren().get(i);
                if (button.getText().equals("Sleep") && !isPetSleeping && allowSleep) {
                    continue;
                } else if (button.getText().equals("Shop") && !isPetSleeping && allowShop) {
                    continue;
                }
                button.setDisable(disable);
            }
        }
        
        // Disable or enable training buttons in the bottom center panel
        HBox trainingBox = (HBox) ((BorderPane) root.getBottom()).getCenter();
        for (int i = 0; i < trainingBox.getChildren().size(); i++) {
            if (trainingBox.getChildren().get(i) instanceof Button) {
                Button button = (Button) trainingBox.getChildren().get(i);
                button.setDisable(disable);
            }
        }
    }

    /**
     * Initializes a timer that increments the pet's score every second
     * and updates the score label accordingly.
     */
    private void initializeScoreTimer() {
        scoreTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            pet.incrementScore(1);
            scoreLabel.setText("Score: " + pet.getScore());
        }));
        scoreTimer.setCycleCount(Timeline.INDEFINITE);
        scoreTimer.play();
        timerStarted = true;
    }

    /**
     * Handles the pet's death by updating the pet's state, sprite, playing sounds,
     * disabling interactions, and showing the game over dialog.
     */
    private void killPet() {
        if (isPetDead) {
            return;
        }
        
        isPetDead = true;
        pet.setScore(App.getTimerValue());
        
        if (spriteFlipTimer != null) {
            spriteFlipTimer.stop();
        }
        
        // Change to death sprite
        Image deadImage = new Image(pet.getSpriteFileNameBase() + "_dead.png");
        petImageView.setImage(deadImage);
        
        // Update UI to indicate pet is deceased
        petNameLabel.setText(pet.getName() + " (Deceased)");
        petNameLabel.setTextFill(Color.BLACK);
        
        ScreenClass.playMusic("sad_music.mp3");
        ScreenClass.playSound("death.mp3");
        disableInteractionButtons(true, false, false);
        
        Platform.runLater(this::showGameOverDialog);
        
        saveGame();
    }

    /**
     * Displays a game over dialog overlay that provides options to start a new game,
     * load a game, or quit to the main menu.
     */
    private void showGameOverDialog() {
        StackPane gameOverPane = new StackPane();
        gameOverPane.setStyle("-fx-background-color: transparent;");
        gameOverPane.prefWidthProperty().bind(root.widthProperty());
        gameOverPane.prefHeightProperty().bind(root.heightProperty());
        
        VBox gameOverBox = new VBox(20);
        gameOverBox.setAlignment(Pos.CENTER);
        gameOverBox.setPadding(new Insets(30));
        gameOverBox.setMaxWidth(400);
        gameOverBox.setMaxHeight(300);
        gameOverBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 10, 0.5, 0.0, 0.0);"
        );
        
        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        gameOverLabel.setTextFill(Color.RED);
        
        Label deathMessageLabel = new Label(pet.getName() + " has died due to neglect.");
        deathMessageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        deathMessageLabel.setWrapText(true);
        deathMessageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        Button newGameButton = new Button("New Game");
        newGameButton.setStyle(
            "-fx-background-color: #4286f4; -fx-text-fill: white; -fx-font-size: 16px; " +
            "-fx-background-radius: 5; -fx-padding: 10 20;"
        );
        newGameButton.setPrefWidth(200);
        newGameButton.setOnAction(e -> App.setScreen(new PetSelectionScreen()));
        
        Button loadGameButton = new Button("Load Game");
        loadGameButton.setStyle(
            "-fx-background-color: #4286f4; -fx-text-fill: white; -fx-font-size: 16px; " +
            "-fx-background-radius: 5; -fx-padding: 10 20;"
        );
        loadGameButton.setPrefWidth(200);
        loadGameButton.setOnAction(e -> App.setScreen(new LoadGameScreen()));
        
        Button quitButton = new Button("Quit Game");
        quitButton.setStyle(
            "-fx-background-color: #ff4c4c; -fx-text-fill: white; -fx-font-size: 16px; " +
            "-fx-background-radius: 5; -fx-padding: 10 20;"
        );
        quitButton.setPrefWidth(200);
        quitButton.setOnAction(e -> {
            if (spriteFlipTimer != null) {
                spriteFlipTimer.stop();
            }
            App.setScreen(new MainMenu());
        });
        
        gameOverBox.getChildren().addAll(
            gameOverLabel,
            deathMessageLabel,
            new Label(""), // Spacer
            newGameButton,
            loadGameButton,
            quitButton
        );
        
        gameOverPane.getChildren().add(gameOverBox);
        
        BorderPane borderPane = (BorderPane) root;
        StackPane centerPane = new StackPane();
        Node originalCenter = borderPane.getCenter();
        centerPane.getChildren().add(originalCenter);
        centerPane.getChildren().add(gameOverPane);
        borderPane.setCenter(centerPane);
        gameOverPane.toFront();
    }
}