package group44.Screens;

import group44.App;
import group44.Pet;
import group44.Screens.ScreenClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * VetScreen allows the user to visit a veterinary clinic to heal their pet.
 * <p>
 * The screen displays a background, a floor design, a cross symbol indicative of a clinic,
 * the pet's image, and health information including a health bar. Additionally, users can
 * heal their pet given a cooldown period and navigate back to the main game screen.
 * </p>
 */
public class VetScreen extends ScreenClass {

    /** The cooldown period in seconds between consecutive pet heals. */
    private static final int HEAL_COOLDOWN_SECONDS = 5;
    
    /**
     * Stores the last time (in seconds) when the pet was healed.
     * Initially set to -HEAL_COOLDOWN_SECONDS to allow immediate healing.
     */
    private int lastHealTime = -HEAL_COOLDOWN_SECONDS;

    /**
     * Constructs a VetScreen with the specified pet.
     *
     * @param pet the Pet instance to be healed
     */
    public VetScreen(Pet pet) {
        this.pet = pet;
        setupUI();
    }

    /**
     * Sets up the user interface of the VetScreen.
     * <p>
     * This method configures the background, floor, cross symbol image, pet image,
     * heal button with its functionality, health bar, status label, and the navigation 
     * button to return to the game screen.
     * </p>
     */
    private void setupUI() {
        // Set background color
        root.setStyle("-fx-background-color: rgb(196,164,132);"); // Light brown background

        // Create the floor rectangle and set its style
        Rectangle floor = new Rectangle(0, screenHeight - (400 * ratio), screenWidth, 400 * ratio);
        floor.setFill(Color.rgb(62, 33, 3));

        // Cross symbol at the top
        Image crossSymbol = new Image("cross_symbol.png");
        ImageView crossImage = new ImageView(crossSymbol);
        crossImage.setFitWidth(200 * ratio); // Set the width of the image
        crossImage.setFitHeight(200 * ratio); // Set the height of the image
        crossImage.setLayoutX(screenWidth / 2 - (100 * ratio));
        crossImage.setLayoutY(100 * ratio);

        // Display pet image at the clinic
        ImageView petImageView = new ImageView(pet.getPetImage());
        petImageView.setFitWidth(400 * ratio);
        petImageView.setFitHeight(400 * ratio);
        petImageView.setLayoutX(screenWidth / 2 - (200 * ratio));
        petImageView.setLayoutY(screenHeight - (700 * ratio));

        // Heal Pet Button and status label
        Label statusLabel = new Label("");
        statusLabel.setTextFill(Color.RED);
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20 * ratio));
        Button healButton = createStyledButton("Heal Pet");
        healButton.setOnAction(e -> {
            int currentTime = App.getTimerValue(); // Get the current time from App.java
            if (currentTime - lastHealTime >= HEAL_COOLDOWN_SECONDS) {
                // Heal the pet and update the last heal time
                pet.heal();
                lastHealTime = currentTime;
                setupUI(); // Refresh the UI to update health information
            } else {
                int remainingCooldown = HEAL_COOLDOWN_SECONDS - (currentTime - lastHealTime);
                statusLabel.setText("Heal on cooldown! Wait " + remainingCooldown + " seconds.");
            }
        });

        // Health bar displaying pet's current health percentage
        ProgressBar healthBar = new ProgressBar(pet.getHealth() / 100.0);
        healthBar.setPrefWidth(350 * ratio);
        if (pet.getHealth() <= 25) {
            healthBar.setStyle("-fx-accent: #FF0000;"); // Red for low health
        } else {
            healthBar.setStyle("-fx-accent: #4CAF50;"); // Green for good health
        }
        healthBar.setLayoutX(screenWidth / 2 - (75 * ratio));
        healthBar.setLayoutY(400 * ratio);
        healthBar.setVisible(true);

        // VBox to hold the heal button, status label, and health bar
        VBox buttonBox = new VBox(10 * ratio);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(0, 0, 540 * ratio, 0)); // Padding to move the button up
        buttonBox.getChildren().addAll(healButton, statusLabel, healthBar);

        // "Go Back" button to return to main game screen
        Button backButton = createStyledButton("Back to Game Screen");
        backButton.setOnAction(e -> {
            GameScreen gameScreen = new GameScreen(pet); // Create new GameScreen instance
            App.setScreen(gameScreen); // Navigate back to the main game screen
        });

        // VBox to hold the back button, positioned at the bottom-right
        VBox backBox = new VBox(0 * ratio);
        backBox.setAlignment(Pos.BOTTOM_RIGHT);
        backBox.setPadding(new Insets(20 * ratio, 20 * ratio, 20 * ratio, 20 * ratio));
        backBox.getChildren().add(backButton);

        // Add UI components to the root pane
        root.setTop(backBox);         // Back button at the top
        root.setCenter(buttonBox);      // Heal button and health bar centered
        root.getChildren().addAll(floor, crossImage, petImageView);
    }
}
