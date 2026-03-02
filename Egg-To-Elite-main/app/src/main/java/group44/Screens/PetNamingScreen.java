package group44.Screens;

import group44.App;
import group44.Pet;
import group44.PetInfo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Screen for naming a newly selected pet before entering the game.
 * Displays a preview of the chosen pet, input for assigning a name,
 * and a button to confirm and start the adventure.
 */
public class PetNamingScreen extends ScreenClass {

    /**
     * The currently selected PetInfo object, containing sprite
     * and basic details about the chosen pet.
     */
    private PetInfo selectedPet;

    /**
     * The text field where the user can specify a custom pet name.
     */
    private TextField nameField;

    /**
     * A label that shows the typed name in real time as the user types.
     */
    private Label previewNameLabel;

    /**
     * Creates a new PetNamingScreen for the provided PetInfo object.
     *
     * @param selectedPet The pet that was selected in the previous screen.
     */
    public PetNamingScreen(PetInfo selectedPet) {
        this.selectedPet = selectedPet;

        // Set up the background and layout
        Image backgroundImage = new Image("sunny.jpg");
        BackgroundImage background = new BackgroundImage(
            backgroundImage,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(
                1.0, 1.0,
                true, true,
                false, false
            )
        );
        root.setBackground(new Background(background));
        root.setPadding(new Insets(40));

        VBox content = new VBox(30);
        content.setAlignment(Pos.CENTER);

        // Create title label
        Label titleLabel = new Label("Name Your New Pet");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.rgb(30, 100, 200));

        // Pet preview section
        VBox petPreview = createPetPreview();

        // Name input
        VBox nameInputArea = createNameInputArea();

        // Navigation buttons
        HBox buttonsArea = createButtonsArea();

        // Assemble UI
        content.getChildren().addAll(titleLabel, petPreview, nameInputArea, buttonsArea);
        root.setCenter(content);
    }

    /**
     * Creates a preview area displaying the pet's image, type label, description,
     * and a live preview of the name the user types in.
     *
     * @return A VBox containing the pet image, name preview, and description.
     */
    private VBox createPetPreview() {
        VBox previewContainer = new VBox(15);
        previewContainer.setAlignment(Pos.CENTER);
        previewContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 15; -fx-padding: 20;");

        DropShadow shadow = new DropShadow();
        shadow.setRadius(10.0);
        shadow.setOffsetX(5.0);
        shadow.setOffsetY(5.0);
        shadow.setColor(Color.color(0.2, 0.2, 0.2, 0.5));
        previewContainer.setEffect(shadow);

        Label petTypeLabel = new Label(selectedPet.getName());
        petTypeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        ImageView petImage = new ImageView(selectedPet.getImage());
        petImage.setFitHeight(200);
        petImage.setFitWidth(200);
        petImage.setPreserveRatio(true);

        TextFlow descFlow = new TextFlow();
        Text descText = new Text(selectedPet.getDescription());
        descText.setFont(Font.font("Arial", 14));
        descFlow.getChildren().add(descText);
        descFlow.setMaxWidth(400);
        descFlow.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        previewNameLabel = new Label(selectedPet.getName());
        previewNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        previewNameLabel.setTextFill(Color.rgb(30, 100, 200));

        previewContainer.getChildren().addAll(petTypeLabel, petImage, previewNameLabel, descFlow);
        return previewContainer;
    }

    /**
     * Creates a text field for the user to enter a custom name for the pet,
     * with a label showing instructions and live updating of the name preview.
     *
     * @return A VBox containing the name entry instructions and text field.
     */
    private VBox createNameInputArea() {
        VBox inputContainer = new VBox(15);
        inputContainer.setAlignment(Pos.CENTER);
        inputContainer.setMaxWidth(500);

        Label instructionsLabel = new Label("What would you like to call your new pet?");
        instructionsLabel.setFont(Font.font("Arial", 16));

        nameField = new TextField(selectedPet.getName());
        nameField.setFont(Font.font("Arial", 16));
        nameField.setMaxWidth(300);
        nameField.setPrefHeight(40);

        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                previewNameLabel.setText(newValue);
            } else {
                previewNameLabel.setText("[No Name]");
            }
            if (newValue != null && newValue.length() > 20) {
                nameField.setText(oldValue);
            }
        });

        javafx.application.Platform.runLater(() -> nameField.requestFocus());

        inputContainer.getChildren().addAll(instructionsLabel, nameField);
        return inputContainer;
    }

    /**
     * Creates the navigation buttons for going back to pet selection or
     * confirming the name and moving on to the game screen.
     *
     * @return An HBox containing the back and confirm buttons.
     */
    private HBox createButtonsArea() {
        HBox buttonsContainer = new HBox(20);
        buttonsContainer.setAlignment(Pos.CENTER);
        buttonsContainer.setPadding(new Insets(20, 0, 0, 0));

        Button backButton = createStyledButton("Back");
        backButton.setOnAction(e -> {
            PetSelectionScreen selectionScreen = new PetSelectionScreen();
            App.setScreen(selectionScreen);
        });

        Button confirmButton = createStyledButton("Start Adventure!");
        confirmButton.setOnAction(e -> {
            String petName = nameField.getText().trim();
            if (petName.isEmpty()) {
                petName = selectedPet.getName();
            }

            Pet pet = new Pet(
                selectedPet.getSpriteFileName(), // Sprite file name base
                petName,                         // Use the name provided by the user
                100,   // Sleepiness
                100,   // Happiness
                100,   // Fullness
                100,   // Health
                100,   // Stamina
                0,     // Score
                1,     // Run level
                0,     // Run experience
                1,     // Swim level
                0,     // Swim experience
                1,     // Fly level
                0,     // Fly experience
                0,     // State
                0      // Coins
            );

            GameScreen.timersInitialized = false;
            GameScreen gameScreen = new GameScreen(pet);
            App.setScreen(gameScreen);
        });

        buttonsContainer.getChildren().addAll(backButton, confirmButton);
        return buttonsContainer;
    }
}