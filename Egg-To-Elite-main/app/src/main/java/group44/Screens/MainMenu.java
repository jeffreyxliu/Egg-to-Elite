package group44.Screens;

import group44.Screens.ScreenClass;
import group44.Screens.PetSelectionScreen;
import group44.App;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The MainMenu class represents the primary entry screen for the "Egg to Elite" game.
 * It displays buttons for starting a new game, loading an existing game, viewing instructions,
 * accessing parental controls, and quitting the application.
 */
public class MainMenu extends ScreenClass {

    /**
     * A label that displays a warning message when a user is not allowed to play,
     * or if all save slots are already used.
     */
    private Label saveSlotsFullMessageLabel;

    /**
     * Constructs the MainMenu, sets up the background, creates the title, buttons, and
     * any accompanying stylistic effects or labels such as credits and warnings.
     */
    public MainMenu() {
        // Set padding and style for the root layout
        root.setPadding(new Insets(30));

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

        
         // Create a spacer to replace the icon image
         Region topSpacer = new Region();
         topSpacer.setPrefHeight(240); // Set the height to match the previous icon height
 
        // Stylized title (larger)
        Label titleLabel = new Label("Egg to Elite");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 64)); // Increased from 48 to 64
        
        // Create gradient text fill
        Stop[] stops = new Stop[] { 
            new Stop(0, Color.rgb(30, 100, 200)),
            new Stop(0.5, Color.rgb(60, 150, 255)),
            new Stop(1, Color.rgb(30, 100, 200))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        titleLabel.setTextFill(gradient);
        
        // Add text shadow effect (no reflection)
        DropShadow textShadow = new DropShadow();
        textShadow.setRadius(5.0);
        textShadow.setOffsetX(3.0);
        textShadow.setOffsetY(3.0);
        textShadow.setColor(Color.color(0.1, 0.1, 0.1, 0.7));
        
        titleLabel.setEffect(textShadow);
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setPadding(new Insets(15, 0, 30, 0));

        // Create button shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setRadius(8.0);
        shadow.setOffsetX(4.0);
        shadow.setOffsetY(4.0);
        shadow.setColor(Color.color(0.4, 0.4, 0.4, 0.5));

        // Buttons
        Button newGameButton = createStyledButton("New Game");
        Button loadGameButton = createStyledButton("Load Game");
        Button instructionsButton = createStyledButton("How to Play"); // New button
        Button parentControlsButton = createStyledButton("Parent Controls");
        Button quitButton = createStyledButton("Quit");
        
        // Set effects
        newGameButton.setEffect(shadow);
        loadGameButton.setEffect(shadow);
        instructionsButton.setEffect(shadow); // Add shadow to new button
        parentControlsButton.setEffect(shadow);
        quitButton.setEffect(shadow);

        // Button actions
        newGameButton.setOnAction(e -> selectPet());
        loadGameButton.setOnAction(e -> loadGame());
        instructionsButton.setOnAction(e -> showInstructions()); // New action
        parentControlsButton.setOnAction(e -> openParentControls());
        quitButton.setOnAction(e -> Platform.exit());

        // Create horizontal layout for buttons with spacing
        HBox buttonBox = new HBox(25); // Increased spacing from 15 to 25
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(newGameButton, loadGameButton, instructionsButton, parentControlsButton, quitButton);
        buttonBox.setPadding(new Insets(30, 0, 0, 0)); // Increased top padding


        // Create a label for the save slots full message (initially hidden)
        saveSlotsFullMessageLabel = new Label();
        saveSlotsFullMessageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        saveSlotsFullMessageLabel.setTextFill(Color.RED); // Set the text color to red
        saveSlotsFullMessageLabel.setVisible(false); // Initially hidden
        saveSlotsFullMessageLabel.setWrapText(true);
        saveSlotsFullMessageLabel.setTextAlignment(TextAlignment.CENTER);

    
        // Add title to top and buttons to center
        VBox topBox = new VBox(15); // Increased spacing from 10 to 15
        topBox.setAlignment(Pos.CENTER);
        topBox.getChildren().addAll(topSpacer, titleLabel, saveSlotsFullMessageLabel);
        root.setTop(topBox);
        root.setCenter(buttonBox);  

        // Setup the credits section at the bottom left
        VBox creditsBox = new VBox(3); // Small spacing between credit lines
        creditsBox.setAlignment(Pos.BOTTOM_LEFT);
        
        // Create credit labels with smaller font
        Label developersLabel = new Label("Developers: Nicolas Seglenieks, Shawn Chen, Alex Minski, Aaron Yi, Jeffery Liu");
        Label teamLabel = new Label("Team Number: 44");
        Label termLabel = new Label("Term: Winter, 2025");
        Label courseLabel = new Label("Game created for CS 2212 @ UWO");
        
        // Style the labels
        Font creditFont = Font.font("Arial", FontWeight.NORMAL, 12);
        Color creditColor = Color.rgb(0, 0, 0, 0.8); // Slightly transparent dark gray
        
        for (Label label : new Label[] {developersLabel, teamLabel, termLabel, courseLabel}) {
            label.setFont(creditFont);
            label.setTextFill(creditColor);
        }
        
        // Add labels to the credits box
        creditsBox.getChildren().addAll(developersLabel, teamLabel, termLabel, courseLabel);
        creditsBox.setPadding(new Insets(0, 0, 10, 10)); // Add some padding at the bottom and left
        
        // Add the credits box to the bottom of the root
        root.setBottom(creditsBox);
        
        // This will also play the main menu theme across the parental controls screen, so we do not need to
        // make this call in its constructor
        ScreenClass.playMusic("main.mp3");
    }

    /**
     * Checks whether parental controls allow the user to play, verifies save slot availability,
     * and either proceeds to pet selection or updates a message label if a slot is unavailable.
     */
    private void selectPet() {
        // Check if all three save slots are occupied
        ParentControlsScreen parentControls = new ParentControlsScreen();
        if (!parentControls.getCurrentlyAllowedToPlay()) {
            saveSlotsFullMessageLabel.setText("You are not allowed to play at this time. Please try again later.");
            saveSlotsFullMessageLabel.setVisible(true);
            return; // Prevent the game from starting
    }
        boolean allSlotsOccupied = true;
        for (int i = 1; i <= 3; i++) {
            String filePath = "GameSaves/LoadGameSlot" + i + ".csv";
            if (!new java.io.File(filePath).exists()) {
                allSlotsOccupied = false;
                break;
            }
        }
    
        if (allSlotsOccupied) {
            // Update and display the message label
            saveSlotsFullMessageLabel.setText("All save slots are full. Please load a game or delete a save to create a new pet.");
            saveSlotsFullMessageLabel.setVisible(true);
            return;
        }

        // Hide the message label if it was previously visible
        saveSlotsFullMessageLabel.setVisible(false);

        // Create Pet Selection Screen and change the app's displayed screen
        PetSelectionScreen petSelectionScreen = new PetSelectionScreen();
        App.setScreen(petSelectionScreen);
    }

    /**
     * Opens a dialog requiring the user to enter a password before displaying
     * the ParentControlsScreen. Users who fail the check will see an error message.
     */
    private void openParentControls() {
        // Create a VBox for the password prompt
        VBox passwordBox = new VBox();
        passwordBox.setPadding(new Insets(10));
        passwordBox.setSpacing(10);
        passwordBox.setAlignment(Pos.CENTER);

        // Create a label for the password prompt
        Label passwordLabel = new Label("Enter Password:");
        passwordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        // Create a PasswordField for password input
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // Create a label for feedback messages
        Label feedbackLabel = new Label();
        feedbackLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        feedbackLabel.setTextFill(Color.RED); // Default color for error messages
        feedbackLabel.setVisible(false); // Initially hidden

        // Declare the passwordStage variable
        Stage passwordStage = new Stage();
        passwordStage.setTitle("Password Required");
        passwordStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows

        // Create a button to submit the password
        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #4286f4; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10;");
        submitButton.setOnAction(e -> {
            if (passwordField.getText().equals("hello")) { 
                // If the password is correct, close the password dialog
                passwordStage.close();

                // Open the Parental Controls screen
                ParentControlsScreen parentControlsScreen = new ParentControlsScreen();
                App.setScreen(parentControlsScreen);
            } else {
                // If the password is incorrect, show an error message
                feedbackLabel.setText("Incorrect password. Please try again.");
                feedbackLabel.setTextFill(Color.RED);
                feedbackLabel.setVisible(true);
            }
            passwordField.clear(); // Clear the password field after submission
        });

        // Add all elements to the VBox
        passwordBox.getChildren().addAll(passwordLabel, passwordField, submitButton, feedbackLabel);

        // Set the scene for the passwordStage
        passwordStage.setScene(new Scene(passwordBox, 300, 200));
        passwordStage.showAndWait();
    }

    /**
     * Loads an existing game by switching to the LoadGameScreen, provided the user
     * is allowed to play under parental controls. If not allowed, displays an error message.
     */
    private void loadGame() 
    {
        // Check if the user is allowed to play
        ParentControlsScreen parentControls = new ParentControlsScreen();
        if (!parentControls.getCurrentlyAllowedToPlay()) {
            // Update and display the restriction message
            saveSlotsFullMessageLabel.setText("You are not allowed to load a game at this time. Please try again later.");
            saveSlotsFullMessageLabel.setVisible(true);
            return; // Prevent the game from loading
        }

        // Hide the message label if it was previously visible
        saveSlotsFullMessageLabel.setVisible(false);
        
        // Create Load Game Screen and change the app's displayed screen
        LoadGameScreen loadGameScreen = new LoadGameScreen();
        App.setScreen(loadGameScreen);
    }

    /**
     * Opens the instructions screen for the user, hiding any previously visible error messages.
     */
    private void showInstructions() {
        // Hide any error messages
        saveSlotsFullMessageLabel.setVisible(false);
        
        // Show the instructions screen
        InstructionsScreen instructionsScreen = new InstructionsScreen();
        App.setScreen(instructionsScreen);
    }
}
