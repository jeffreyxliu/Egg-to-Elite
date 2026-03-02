package group44.Screens;

import group44.App;
import group44.PetInfo;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * The PetSelectionScreen class presents a list of available pets for the player to choose from.
 * It features navigation buttons to cycle through pets, a brief description of each pet, and
 * controls to confirm the selection or return to the main menu.
 */
public class PetSelectionScreen extends ScreenClass {

    /**
     * Holds the index of the currently displayed PetInfo in the availablePets list.
     */
    private int currentPetIndex = 0;

    /**
     * A list containing all PetInfo objects that can be selected.
     */
    private final List<PetInfo> availablePets = new ArrayList<>();

    /**
     * The ImageView for displaying the currently selected pet's image.
     */
    private ImageView petImageView;

    /**
     * A TextFlow component to display detailed text information about the currently selected pet.
     */
    private TextFlow petInfoText;

    /**
     * The label used for displaying the name of the currently selected pet.
     */
    private Label petNameLabel;

    /**
     * Constructs the PetSelectionScreen, initializes pet data, configures
     * the user interface, and shows the first pet by default.
     */
    public PetSelectionScreen() {
        // Initialize pet data (replace with actual pet images and descriptions)
        initializePets();

        // Set up the screen layout
        root.setPadding(new Insets(30 * ratio));

        // Load the background image
        Image backgroundImage = new Image("sunny.jpg"); // Replace with your image file path

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

        // Set up the pet info area in the top left
        setupPetInfoArea();

        // Set up the pet selection area in the center
        setupPetSelectionArea();

        // Set up bottom buttons
        setupBottomButtons();

        // Display the first pet
        updatePetDisplay();
    }

    /**
     * Creates a styled container in the top area to display the current pet's name
     * and description. It includes a scrollable text flow for informative pet details.
     */
    private void setupPetInfoArea() {
        VBox infoContainer = new VBox(15 * ratio);
        infoContainer.setPadding(new Insets(20 * ratio));
        infoContainer.setMaxWidth(400 * ratio);
        infoContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-background-radius: 15;");

        DropShadow shadow = new DropShadow();
        shadow.setRadius(10.0 * ratio);
        shadow.setOffsetX(5.0 * ratio);
        shadow.setOffsetY(5.0 * ratio);
        shadow.setColor(Color.color(0.2, 0.2, 0.2, 0.5));
        infoContainer.setEffect(shadow);

        petNameLabel = new Label("Select Your Pet");
        petNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        petNameLabel.setTextFill(Color.rgb(30, 100, 200));

        petInfoText = new TextFlow();
        petInfoText.setPrefWidth(360 * ratio);
        petInfoText.setLineSpacing(5 * ratio);

        Text defaultText = new Text("Browse through the available pets and choose your favorite! Each pet has unique traits and abilities.");
        defaultText.setFont(Font.font("Arial", 16));
        petInfoText.getChildren().add(defaultText);

        ScrollPane scrollPane = new ScrollPane(petInfoText);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(200);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        infoContainer.getChildren().addAll(petNameLabel, scrollPane);
        root.setTop(infoContainer);
    }

    /**
     * Arranges the UI elements for navigating pets (via left and right arrows)
     * and displaying a large image of the currently selected pet in the center.
     */
    private void setupPetSelectionArea() {
        HBox selectionArea = new HBox(20 * ratio);
        selectionArea.setAlignment(Pos.CENTER);

        Button leftArrow = createArrowButton(true);
        leftArrow.setOnAction(e -> navigatePets(-1));

        StackPane petImageContainer = new StackPane();

        Rectangle clip = new Rectangle(400 * ratio, 400 * ratio);
        clip.setArcWidth(30 * ratio);
        clip.setArcHeight(30 * ratio);

        petImageView = new ImageView();
        petImageView.setFitWidth(400 * ratio);
        petImageView.setFitHeight(400 * ratio);
        petImageView.setPreserveRatio(true);
        petImageView.setClip(clip);

        petImageContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #4286f4;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 15;" +
            "-fx-background-radius: 15;" +
            "-fx-padding: 10;"
        );

        DropShadow imageShadow = new DropShadow();
        imageShadow.setRadius(15.0 * ratio);
        imageShadow.setOffsetX(7.0 * ratio);
        imageShadow.setOffsetY(7.0 * ratio);
        imageShadow.setColor(Color.color(0.2, 0.2, 0.2, 0.6));
        petImageContainer.setEffect(imageShadow);

        petImageContainer.getChildren().add(petImageView);

        Button rightArrow = createArrowButton(false);
        rightArrow.setOnAction(e -> navigatePets(1));

        selectionArea.getChildren().addAll(leftArrow, petImageContainer, rightArrow);
        root.setCenter(selectionArea);
    }

    /**
     * Creates the bottom button bar containing a "Back to Menu" button and a "Select This Pet" button.
     * Sets click actions to either return to the main menu or proceed with the selected pet name screen.
     */
    private void setupBottomButtons() {
        HBox buttonBox = new HBox(20 * ratio);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(30 * ratio, 0, 0, 0));

        Button selectButton = createStyledButton("Select This Pet");
        Button backButton = createStyledButton("Back to Menu");

        selectButton.setOnAction(e -> selectCurrentPet());
        backButton.setOnAction(e -> returnToMainMenu());

        buttonBox.getChildren().addAll(backButton, selectButton);
        root.setBottom(buttonBox);

        // Play the main menu music
        ScreenClass.playMusic("main.mp3");
    }

    /**
     * Changes the currentPetIndex by a specified direction and updates the pet display
     * to show the newly selected pet.
     *
     * @param direction The direction to move in the list: -1 for previous, +1 for next.
     */
    private void navigatePets(int direction) {
        currentPetIndex = (currentPetIndex + direction + availablePets.size()) % availablePets.size();
        updatePetDisplay();
    }

    /**
     * Updates the screen to display the currently selected pet, including its image, name,
     * and description text in the text flow.
     */
    private void updatePetDisplay() {
        if (!availablePets.isEmpty()) {
            PetInfo pet = availablePets.get(currentPetIndex);
            petImageView.setImage(pet.getImage());
            petNameLabel.setText(pet.getName());
            petInfoText.getChildren().clear();

            Text description = new Text(pet.getDescription());
            description.setFont(Font.font("Arial", 16));
            petInfoText.getChildren().add(description);
        }
    }

    /**
     * Called when the "Select This Pet" button is clicked. Retrieves the currently displayed pet
     * and transitions to the PetNamingScreen for setting a custom name.
     */
    private void selectCurrentPet() {
        if (!availablePets.isEmpty()) {
            PetInfo selectedPet = availablePets.get(currentPetIndex);
            System.out.println("Selected pet: " + selectedPet.getName());

            // Navigate to the naming screen with the selected pet info
            PetNamingScreen namingScreen = new PetNamingScreen(selectedPet);
            App.setScreen(namingScreen);
        }
    }

    /**
     * Returns user to the main menu screen.
     */
    private void returnToMainMenu() {
        MainMenu mainMenu = new MainMenu();
        App.setScreen(mainMenu);
    }

    /**
     * Initializes the list of available pets with placeholder data. 
     * Replace or adjust with real pet images, names, and descriptions as needed.
     */
    private void initializePets() {
        availablePets.add(new PetInfo(
            "Yellow Duck",
            new Image("yellow.png"),
            "The Yellow Duck is lighter and doesn't require feeding as often as the other ducks.",
            "yellow"
        ));

        availablePets.add(new PetInfo(
            "Green Duck",
            new Image("green.png"),
            "The Green Duck is a natural runner and will level up their running faster than other ducks.",
            "green"
        ));

        availablePets.add(new PetInfo(
            "Red Duck",
            new Image("red.png"),
            "The Red Duck is known for its flying capabilities. It will level up its flying faster than other ducks.",
            "red"
        ));

        availablePets.add(new PetInfo(
            "Teal Duck",
            new Image("gray.png"),
            "The Teal Duck is stoic and calm. It doesn't require as much to make it happy.",
            "gray"
        ));

        availablePets.add(new PetInfo(
            "Pink Duck",
            new Image("pink.png"),
            "The Pink Duck is a jack of all trades. It will level up all of its skills at a slightly faster rate.",
            "pink"
        ));

        availablePets.add(new PetInfo(
            "Blue Duck",
            new Image("blue.png"),
            "The Blue Duck is a friendly pet that loves to swim. They will level up their swimming faster than other ducks",
            "blue"
        ));
    }
}
