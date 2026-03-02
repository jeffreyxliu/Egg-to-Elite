package group44.Screens;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import group44.App;
import group44.Pet;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * The LoadGameScreen class provides a UI for users to load saved pet data
 * within the Egg to Elite game. It shows available save slots, allowing
 * them to either load or delete a particular slot's data.
 */
public class LoadGameScreen extends ScreenClass {

    /**
     * Constructs the LoadGameScreen, setting a background image, top bar with title
     * and back button, and a center area displaying available save slots.
     */
    public LoadGameScreen() {
        // Set padding and style for the root layout
        root.setPadding(new Insets(30));

        // Load the background image
        Image backgroundImage = new Image("sunny.jpg");

        // Create a BackgroundImage object
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

        // Set the background to the root layout
        root.setBackground(new Background(background));

        // Set up the top and center sections
        setUpTop();
        setUpCenter();
    }

    /**
     * Creates and configures the top StackPane, including a title label
     * and a "Main Menu" button.
     */
    private void setUpTop() {
        // Create a StackPane to hold the title and back button
        StackPane topContainer = new StackPane();
        topContainer.setPadding(new Insets(15));

        // Create title label
        Label titleLabel = new Label("Load Game");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        StackPane.setAlignment(titleLabel, Pos.CENTER);

        // Create return to main menu button
        Button backToMainMenu = new Button("Main Menu");
        backToMainMenu.setStyle(
            "-fx-background-color: linear-gradient(to right,#4286f4, #1a56c4);\r\n" +
            "    -fx-text-fill: white;\r\n" +
            "    -fx-font-weight: bold;\r\n" +
            "    -fx-padding: 10 20 10 20;\r\n" +
            "    -fx-background-radius: 15;\r\n" +
            "    -fx-border-radius: 15;\r\n" +
            "    -fx-border-color: #1e90ff;\r\n" +
            "    -fx-border-width: 2;\r\n" +
            "    -fx-alignment: center-left;"
        );
        backToMainMenu.setOnAction(e -> returnToMainMenu());
        StackPane.setAlignment(backToMainMenu, Pos.CENTER_LEFT);

        // Add the button and title to the StackPane
        topContainer.getChildren().addAll(backToMainMenu, titleLabel);

        // Set the top container in the BorderPane
        root.setTop(topContainer);
    }

    /**
     * Creates and configures the center area containing three save slots,
     * each with its own load and delete button (if data exists).
     */
    private void setUpCenter() {
        // Create VBox for the save slots
        VBox saveSlot = new VBox(85);
        saveSlot.setPadding(new Insets(20));
        saveSlot.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.8);" +
            "-fx-border-color: #000000;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );
        saveSlot.setAlignment(Pos.CENTER);
        saveSlot.setPrefWidth(600);
        saveSlot.setPrefHeight(400);

        // Create HBox for each save slot
        HBox saveGameSlot1 = createSaveSlot("LoadGameSlot1");
        HBox saveGameSlot2 = createSaveSlot("LoadGameSlot2");
        HBox saveGameSlot3 = createSaveSlot("LoadGameSlot3");

        // Add save slots to VBox
        saveSlot.getChildren().addAll(saveGameSlot1, saveGameSlot2, saveGameSlot3);

        // Create a wrapper StackPane to center the saveSlot VBox
        StackPane wrapper = new StackPane(saveSlot);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(50));

        // Set the VBox in the center of the BorderPane
        root.setCenter(wrapper);
    }

    /**
     * Generates a single save slot element that either displays pet data and
     * provides Load/Delete buttons or indicates that no save is present.
     *
     * @param slotName The name of the save slot (e.g., "LoadGameSlot1").
     * @return An HBox containing the slot UI elements.
     */
    private HBox createSaveSlot(String slotName) {
        HBox saveSlot = new HBox(20);
        saveSlot.setPadding(new Insets(20));
        saveSlot.setStyle(
            "-fx-background-color: rgb(255, 255, 255);" +
            "-fx-border-color: #000000;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );
        saveSlot.setAlignment(Pos.CENTER_LEFT);

        // Define the file path for the save slot
        String filePath = "GameSaves/" + slotName + ".csv";
        java.io.File saveFile = new java.io.File(filePath);

        // Check if the save file exists
        if (saveFile.exists()) {
            Pet pet = loadPetFromFile(filePath);

            // Create label for the slot name
            Label slotLabel = new Label("Load " + pet.getName());
            slotLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

            // Extract the slot number from the string
            int slot = Character.getNumericValue(slotName.charAt(slotName.length() - 1));

            // Create load button
            Button loadButton = new Button("Load");
            styleButton(loadButton, 20, 10);
            loadButton.setOnAction(e -> loadGame(pet, slot));

            // Create delete button
            Button deleteButton = new Button("Delete");
            styleDeleteButton(deleteButton, 20, 10);
            deleteButton.setOnAction(e -> {
                if (saveFile.delete()) {
                    // Refresh the screen to reflect the deletion
                    App.setScreen(new LoadGameScreen());
                } else {
                    System.out.println("Failed to delete save file: " + filePath);
                }
            });

            // Add padding to the label
            slotLabel.setPadding(new Insets(0, 20, 0, 20));

            // Add an image to represent the pet
            if (!pet.getSpriteFileNameBase().isEmpty()) {
                String imagePath = pet.getSpriteFileNameBase() + ".png";
                Image petImage = new Image(imagePath);
                ImageView petImageView = new ImageView(petImage);
                petImageView.setFitHeight(50);
                petImageView.setPreserveRatio(true);
                saveSlot.getChildren().add(petImageView);
            }

            // Create four columns for pet stats
            VBox column1 = new VBox(5);
            VBox column2 = new VBox(5);
            VBox column3 = new VBox(5);
            VBox column4 = new VBox(5);
            column1.setAlignment(Pos.CENTER_LEFT);
            column2.setAlignment(Pos.CENTER_LEFT);
            column3.setAlignment(Pos.CENTER_LEFT);
            column4.setAlignment(Pos.CENTER_LEFT);

            column1.getChildren().addAll(
                new Label("Sleepiness: " + pet.getSleepiness()),
                new Label("Happiness: " + pet.getHappiness())
            );
            column2.getChildren().addAll(
                new Label("Fullness: " + pet.getFullness()),
                new Label("Stamina: " + pet.getStamina())
            );
            column3.getChildren().addAll(
                new Label("Run Level: " + pet.getRunLevel() + " (XP: " + pet.getRunExperience() + ")"),
                new Label("Swim Level: " + pet.getSwimLevel() + " (XP: " + pet.getSwimExperience() + ")")
            );
            column4.getChildren().addAll(
                new Label("Fly Level: " + pet.getFlyLevel() + " (XP: " + pet.getFlyExperience() + ")"),
                new Label("Coins: " + pet.getCoins())
            );

            // Combine columns into a single HBox
            HBox statsBox = new HBox(20);
            statsBox.getChildren().addAll(column1, column2, column3, column4);

            // Spacer to push the delete button to the right
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Add all elements to the slot HBox
            saveSlot.getChildren().addAll(slotLabel, loadButton, statsBox, spacer, deleteButton);

        } else {
            // If no save file exists
            Label noSaveLabel = new Label("No save in this slot");
            noSaveLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            noSaveLabel.setTextFill(Color.GRAY);
            saveSlot.getChildren().add(noSaveLabel);
        }

        return saveSlot;
    }

    /**
     * Applies a styled look to the given button using a blue gradient background.
     *
     * @param button The Button instance to style.
     * @param paddingHorizontal The horizontal padding in pixels.
     * @param paddingVertical The vertical padding in pixels.
     */
    private void styleButton(Button button, int paddingHorizontal, int paddingVertical) {
        button.setStyle(
            "-fx-background-color: linear-gradient(to right,#4286f4, #1a56c4);\r\n" +
            "    -fx-text-fill: white;\r\n" +
            "    -fx-font-weight: bold;\r\n" +
            "    -fx-padding: " + paddingVertical + " " + paddingHorizontal + " " +
                               paddingVertical + " " + paddingHorizontal + ";\r\n" +
            "    -fx-background-radius: 15;\r\n" +
            "    -fx-border-radius: 15;\r\n" +
            "    -fx-border-color: #1e90ff;\r\n" +
            "    -fx-border-width: 2;\r\n" +
            "    -fx-alignment: center-left;"
        );
    }

    /**
     * Applies a styled look to the given delete button using a red gradient background.
     *
     * @param button The Button instance to style.
     * @param paddingHorizontal The horizontal padding in pixels.
     * @param paddingVertical The vertical padding in pixels.
     */
    private void styleDeleteButton(Button button, int paddingHorizontal, int paddingVertical) {
        button.setStyle(
            "-fx-background-color: linear-gradient(to right,#ff4d4d, #cc0000);\r\n" +
            "    -fx-text-fill: white;\r\n" +
            "    -fx-font-weight: bold;\r\n" +
            "    -fx-padding: " + paddingVertical + " " + paddingHorizontal + " " +
                               paddingVertical + " " + paddingHorizontal + ";\r\n" +
            "    -fx-background-radius: 15;\r\n" +
            "    -fx-border-radius: 15;\r\n" +
            "    -fx-border-color: #cc0000;\r\n" +
            "    -fx-border-width: 2;\r\n" +
            "    -fx-alignment: center-left;"
        );
    }

    /**
     * Loads a pet's parameters into the GameScreen and switches to the game view.
     *
     * @param pet The Pet object to load.
     * @param slot The save slot number associated with this pet.
     */
    private void loadGame(Pet pet, int slot) {
        if (pet != null) {
            isLoadedGame = true;
            loadedSlot = slot;
            // Reinitialize the stat timers for the new pet
            GameScreen.timersInitialized = false;
            GameScreen gameScreen = new GameScreen(pet);
            App.setScreen(gameScreen);
        }
    }

    /**
     * Reads pet data from a CSV file and creates a new Pet object
     * with all the appropriate stats and attributes.
     *
     * @param loadSlot The file path for the save slot CSV file.
     * @return A Pet object if successful, or null if reading fails.
     */
    private Pet loadPetFromFile(String loadSlot) {
        try (BufferedReader reader = new BufferedReader(new FileReader(loadSlot))) {
            String line = reader.readLine();
            if (line != null) {
                String[] attributes = line.split(",");
                String spriteFileNameBase = attributes[0];
                String name = attributes[1];
                int sleepiness = Integer.parseInt(attributes[2]);
                int happiness = Integer.parseInt(attributes[3]);
                int fullness = Integer.parseInt(attributes[4]);
                int health = Integer.parseInt(attributes[5]);
                int stamina = Integer.parseInt(attributes[6]);
                int score = Integer.parseInt(attributes[7]);
                int runLevel = Integer.parseInt(attributes[8]);
                int runExperience = Integer.parseInt(attributes[9]);
                int swimLevel = Integer.parseInt(attributes[10]);
                int swimExperience = Integer.parseInt(attributes[11]);
                int flyLevel = Integer.parseInt(attributes[12]);
                int flyExperience = Integer.parseInt(attributes[13]);
                int state = Integer.parseInt(attributes[14]);
                int coins = Integer.parseInt(attributes[15]);

                return new Pet(spriteFileNameBase, name, sleepiness, happiness, fullness, health,
                               stamina, score, runLevel, runExperience, swimLevel, swimExperience,
                               flyLevel, flyExperience, state, coins);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the user to the main menu by creating a new MainMenu instance
     * and setting it as the current screen.
     */
    private void returnToMainMenu() {
        MainMenu mainMenu = new MainMenu();
        App.setScreen(mainMenu);
    }
}