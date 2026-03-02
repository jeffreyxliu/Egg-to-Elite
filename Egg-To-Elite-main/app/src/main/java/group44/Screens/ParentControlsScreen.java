package group44.Screens;

import group44.App;

import java.io.FileReader;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.TextField;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.animation.KeyFrame;

/**
 * The ParentControlsScreen class provides a user interface for setting time restrictions,
 * toggling family-friendly play limits, and reviving pets. Parents can adjust
 * configurations and return to the main menu.
 */
public class ParentControlsScreen extends ScreenClass {
    /**
     * A list of allowed time ranges, where even indices represent start times
     * and odd indices represent corresponding end times.
     */
    private ArrayList<Integer> timesAllowed; 

    /**
     * Indicates whether the time restriction feature is active.
     * If true, children can only play within the allowed time slots.
     */
    private boolean timeLimitEnabled = false; 

    /**
     * The result of checking current time against time restrictions.
     * True if the child is allowed to play at this moment, false otherwise.
     */
    private boolean currentlyAllowedToPlay;

    /**
     * Constructs the parent controls screen, reading previous restrictions
     * from a file, setting up the user interface, and checking if play is permitted.
     */
    public ParentControlsScreen() {
        timesAllowed = new ArrayList<>();
        loadTimesAllowedFromFile(); 
        setupUI();
        currentlyAllowedToPlay = checkIfAllowedToPlay(); 
    }

    /**
     * Sets up the overall user interface, including the background image,
     * top bar, center controls, and bottom section for displaying time slots.
     */
    private void setupUI() {
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
        root.setPadding(new Insets(20 * ratio));

        // Top section: Title and Close Button
        setupTopBar();

        // Center section: Time Limit Toggle and Add Permission Button
        setupCenterControls();

        // Bottom section: Current Times Allowed
        setupAllowedTimesBox();
    }

    /**
     * Adds an HBox at the top with a title, statistics about total
     * and average play time, and a quit button. 
     * The stats box can also be reset.
     */
    private void setupTopBar() {
        // Title Label (Centered at the top)
        Label titleLabel = new Label("Parental Controls");
        titleLabel.setStyle("-fx-font-size: 40 px;" + "-fx-text-fill: #4286f4;");
        titleLabel.setPadding(new Insets(0, 175*ratio, 0, 0)); // Add left padding to shift the title
        titleLabel.setAlignment(Pos.CENTER);
        // Stats Box (Top-left corner)
        long averagePlayTime = App.sessionCount > 0 ? App.totalPlayTime / App.sessionCount : 0;
        long totalPlayTimeMinutes = App.totalPlayTime / 60;
        long averagePlayTimeMinutes = averagePlayTime / 60;

        Label totalPlayTimeLabel = new Label("Total Play Time: " + totalPlayTimeMinutes + " minutes");
        Label averagePlayTimeLabel = new Label("Average Play Time: " + averagePlayTimeMinutes + " minutes");

        Button resetButton = new Button("Reset Statistics");
        resetButton.setOnAction(e -> {
            App.totalPlayTime = 0;
            App.sessionCount = 0;
            App.savePlayTimeStatistics(); // Save reset statistics
            setupTopBar(); // Refresh the UI
        });

        VBox statsBox = new VBox(10, totalPlayTimeLabel, averagePlayTimeLabel, resetButton);
        statsBox.setAlignment(Pos.TOP_LEFT);
        statsBox.setPadding(new Insets(10));
        statsBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-border-color: black; -fx-border-width: 1;");

        // Quit Button (Top-right corner)
        Button quitButton = new Button();
        quitButton = createStyledButton("Quit");
        quitButton.setOnAction(e -> {
            saveTimesAllowedToFile(); // Save the updated times to the file
            App.setScreen(new MainMenu()); // Navigate back to the main menu
        });

        // Layout for the top bar
        BorderPane topBar = new BorderPane();
        topBar.setLeft(statsBox); // Stats box in the top-left corner
        topBar.setCenter(titleLabel); // Title in the center
        topBar.setRight(quitButton); // Quit button in the top-right corner
        BorderPane.setMargin(statsBox, new Insets(10));
        BorderPane.setMargin(quitButton, new Insets(10));

        // Set the top bar in the root layout
        root.setTop(topBar);
    }

    /**
     * Creates a center pane containing a toggle for enabling/disabling time limits,
     * a section to add new allowed times, and a "Revive Pet" button.
     */
    private void setupCenterControls() {
        BorderPane centerControlsPane = new BorderPane();

        // Time Limit Toggle
        HBox timeLimitBox = new HBox();
        timeLimitBox.setAlignment(Pos.CENTER);
        timeLimitBox.setSpacing(10 * ratio);

        Label timeLimitLabel = new Label("Time Limit");
        timeLimitLabel.setStyle("-fx-font-size: " + (50 * ratio) + "px;");

        Image spriteOn = new Image("switch_on.png");
        Image spriteOff = new Image("switch_off.png");

        ImageView timeLimitSprite = new ImageView(timeLimitEnabled ? spriteOn : spriteOff);
        timeLimitSprite.setFitWidth(100 * ratio); // Set the width of the sprite
        timeLimitSprite.setFitHeight(100 * ratio); // Set the height of the sprite

        timeLimitSprite.setOnMouseClicked(e -> {
            toggleTimeLimit();
            timeLimitSprite.setImage(timeLimitEnabled ? spriteOn : spriteOff); // Update the sprite
        });

        timeLimitBox.getChildren().addAll(timeLimitLabel, timeLimitSprite);

        // Add Permission Section
        VBox addPermissionBox = new VBox();
        addPermissionBox.setAlignment(Pos.CENTER);
        addPermissionBox.setSpacing(10 * ratio);

        Label addPermissionLabel = new Label("Add a Time Permission");
        addPermissionLabel.setStyle("-fx-font-size: " + (30 * ratio) + "px;");

        // Input fields for start and end times
        TextField startTimeField = new TextField();
        startTimeField.setPromptText("Start Time (0-24)");
        startTimeField.setPrefWidth(150 * ratio);

        TextField endTimeField = new TextField();
        endTimeField.setPromptText("End Time (0-24)");
        endTimeField.setPrefWidth(150 * ratio);

        // Button to add the time permission
        Button addPermissionButton = new Button("Add");
        addPermissionButton.setStyle(
            "-fx-font-size: " + (25 * ratio) + "px;" +
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;"
        );

        addPermissionButton.setOnAction(e -> {
            try {
                int startTime = Integer.parseInt(startTimeField.getText());
                int endTime = Integer.parseInt(endTimeField.getText());

                // Validate the input
                if (startTime < 0 || startTime > 24 || endTime < 0 || endTime > 24 || startTime >= endTime) {
                    throw new IllegalArgumentException("Invalid time range. Ensure 0 <= start < end <= 24.");
                }

                // Add the time permission
                addTimeAllowed(startTime, endTime);

                // Clear the input fields
                startTimeField.clear();
                endTimeField.clear();

                // Refresh the allowed times box
                setupAllowedTimesBox();
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input. Please enter numeric values.");
            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
            }
        });

        addPermissionBox.getChildren().addAll(addPermissionLabel, startTimeField, endTimeField, addPermissionButton);

        // Revive Pet Button
        VBox revivePetBox = new VBox();
        revivePetBox.setAlignment(Pos.CENTER);
        revivePetBox.setSpacing(10 * ratio);

        Button revivePetButton = new Button("Revive Pet");
        revivePetButton.setStyle(
            "-fx-font-size: " + (30 * ratio) + "px;" +
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #000000;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 15;"
        );
        revivePetButton.setPrefWidth(200 * ratio);
        revivePetButton.setPrefHeight(50 * ratio);
        
        Label revivePetLabel = new Label("");
        revivePetLabel.setStyle("-fx-font-size: " + (30 * ratio) + "px; -fx-text-fill: #000000;");
        revivePetLabel.setVisible(false); // Initially hidden

        revivePetButton.setOnAction(e -> {
            boolean petRevived = false;
            int i;
            for (i=1;i<=3;i++) {
                if (getPetHealth("GameSaves/LoadGameSlot" + String.valueOf(i) + ".csv") == 0) {
                    petRevived = true;
                    revivePet(i); // Call the revivePet method with the slot number
                }
            }
            if (petRevived == true) {
                // Show the save message
                revivePetLabel.setText("All Pets Revived");
                revivePetLabel.setVisible(true);

                // Create a Timeline to hide the message after 3 seconds
                Timeline hideMessageTimeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
                    revivePetLabel.setVisible(false); // Hide the message
                }));

                hideMessageTimeline.setCycleCount(1); // Run only once
                hideMessageTimeline.play(); // Start the timeline
            }
        });



        revivePetBox.getChildren().addAll(revivePetLabel, revivePetButton);

        // Add components to the BorderPane
        centerControlsPane.setCenter(timeLimitBox);
        centerControlsPane.setLeft(revivePetBox);
        centerControlsPane.setRight(addPermissionBox);

        BorderPane.setMargin(timeLimitBox, new Insets(10 * ratio));
        BorderPane.setMargin(addPermissionBox, new Insets(50 * ratio));
        BorderPane.setMargin(revivePetBox, new Insets(50 * ratio));

        root.setCenter(centerControlsPane);
    }

    /**
     * Displays a list of current allowed play times, providing a "Delete" button
     * for each range. Uses a VBox at the bottom of the screen.
     */
    private void setupAllowedTimesBox() {
        VBox allowedTimesBox = new VBox();
        allowedTimesBox.setAlignment(Pos.TOP_LEFT);
        allowedTimesBox.setSpacing(10 * ratio);
        allowedTimesBox.setPadding(new Insets(10 * ratio));
        allowedTimesBox.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-background-color:rgba(255, 255, 255, 0.8)");

        // Set fixed size for the box
        allowedTimesBox.setPrefWidth(600 * ratio); // Adjust width as needed
        allowedTimesBox.setPrefHeight(500 * ratio); // Adjust height as needed

        Label allowedTimesTitle = new Label("Current Times Allowed:");
        allowedTimesTitle.setStyle("-fx-font-size: " + (30 * ratio) + "px; -fx-font-weight: bold;");

        allowedTimesBox.getChildren().add(allowedTimesTitle);

        // Add existing times to the box with a "Delete" button
        for (int i = 0; i < timesAllowed.size(); i += 2) {
            int startTime = timesAllowed.get(i);
            int endTime = timesAllowed.get(i + 1);
            String timeRange = formatTime(startTime) + " - " + formatTime(endTime);

            HBox timeEntry = new HBox();
            timeEntry.setAlignment(Pos.CENTER_LEFT);
            timeEntry.setSpacing(10 * ratio);

            Label timeLabel = new Label(timeRange);
            timeLabel.setStyle("-fx-font-size: " + (25 * ratio) + "px;");
            Button deleteButton = new Button("Delete");
            deleteButton.setStyle(
                "-fx-font-size: " + (20 * ratio) + "px;" +
                "-fx-background-color: #FF4C4C;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;"
            );

            // Handle deletion of the time range
            int index = i; // Capture the current index for the lambda
            deleteButton.setOnAction(e -> {
                // Remove the time range from the list
                timesAllowed.remove(index); // Remove start time
                timesAllowed.remove(index); // Remove end time (shifts after removing start time)

                // Save the updated list to the file
                saveTimesAllowedToFile();

                // Refresh the allowed times box
                setupAllowedTimesBox();
            });

            timeEntry.getChildren().addAll(timeLabel, deleteButton);
            allowedTimesBox.getChildren().add(timeEntry);
        }

        root.setBottom(allowedTimesBox);
    }

    /**
     * Formats an integer hour (0-24) into a 12-hour-based string including am/pm.
     *
     * @param hour The hour in 0-24 format.
     * @return Formatted time, e.g. "7:00 am".
     */
    private String formatTime(int hour) {
        String period = hour < 12 ? "am" : "pm";
        int formattedHour = hour % 12 == 0 ? 12 : hour % 12;
        return formattedHour + ":00 " + period;
    }

    /**
     * Creates and returns a styled button with hover effects. 
     * Overrides the base createStyledButton method from ScreenClass.
     *
     * @param text The button text label.
     * @return A styled Button instance.
     */
    @Override
    public Button createStyledButton(String text) {
        Button button = new Button(text);
        // Scale button font size
        double scaledFontSize = 60 * ratio;

        button.setPrefWidth(80);
        button.setPrefHeight(50); 

        button.setStyle(
            "-fx-background-color: linear-gradient(#4286f4, #1a56c4);" +
            "-fx-background-radius: 35;" + // Increased roundness for larger buttons
            "-fx-text-fill: white;" +
            "-fx-padding: 10;" + // Increased padding
            "-fx-border-color: #0a3b8c;" +
            "-fx-border-width: 2;" + // Increased border width
            "-fx-border-radius: 35;" // Increased roundness to match background
        );

        // Hover effect
        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: linear-gradient(#5296ff, #2666d4);" +
                "-fx-background-radius: 35;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 10;" +
                "-fx-border-color: #0a3b8c;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 35;"
            )
        );

        // Return to normal when mouse exits
        button.setOnMouseExited(e -> 
            button.setStyle(
                "-fx-background-color: linear-gradient(#4286f4, #1a56c4);" +
                "-fx-background-radius: 35;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 10;" +
                "-fx-border-color: #0a3b8c;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 35;"
            )
        );

        return button;
    }

    /**
     * Adds a new time range for allowed play, throwing an
     * IllegalArgumentException if invalid times are provided.
     *
     * @param startTime The start of the play window (0-24).
     * @param endTime The end of the play window (0-24).
     */
    public void addTimeAllowed(int startTime, int endTime) throws IllegalArgumentException {
        if (startTime < 0 || startTime > 24 || endTime < 0 || endTime > 24) {
            throw new IllegalArgumentException("Start and end times must be between 0 and 24.");
        }
        else {
            timesAllowed.add(startTime);
            timesAllowed.add(endTime);
        }
    }

    /**
     * Retrieves the current list of allowed time ranges.
     *
     * @return The ArrayList of hour-based time ranges.
     */
    public ArrayList<Integer> getTimesAllowed() {
        return timesAllowed;
    }

    /**
     * Toggles timeLimitEnabled, enabling or disabling the time restriction feature.
     */
    private void toggleTimeLimit() {
        timeLimitEnabled = !timeLimitEnabled;
    }

    /**
     * Writes the current parent controls settings (time limit toggle and allowed times)
     * to a file for persistence across sessions.
     */
    private void saveTimesAllowedToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("TimeRestrictions/timesAllowed.txt"))) {
            // Save the timeLimitEnabled state as the first line
            writer.write(Boolean.toString(timeLimitEnabled));
            writer.newLine();
    
            // Save the allowed times
            for (int i = 0; i < timesAllowed.size(); i += 2) {
                writer.write(timesAllowed.get(i) + "," + timesAllowed.get(i + 1));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving timesAllowed: " + e.getMessage());
        }
    }

    /**
     * Loads previously saved time settings from a file, including the toggle state
     * and any stored allowed time ranges.
     */
    private void loadTimesAllowedFromFile() {
        timesAllowed.clear(); // Clear the list before loading
        try (BufferedReader reader = new BufferedReader(new FileReader("TimeRestrictions/timesAllowed.txt"))) {
            // Read the timeLimitEnabled state from the first line
            String timeLimitLine = reader.readLine();
            if (timeLimitLine != null) {
                timeLimitEnabled = Boolean.parseBoolean(timeLimitLine);
            }
    
            // Read the allowed times
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    int startTime = Integer.parseInt(parts[0]);
                    int endTime = Integer.parseInt(parts[1]);
                    timesAllowed.add(startTime);
                    timesAllowed.add(endTime);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No saved timesAllowed file found. Starting fresh.");
            timeLimitEnabled = false; // Default to false if the file doesn't exist
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading timesAllowed: " + e.getMessage());
        }
    }

    /**
     * Checks if the current local hour is within any of the allowed time ranges.
     * If timeLimitEnabled is false, children are always allowed to play.
     *
     * @return True if the child can play now, false otherwise.
     */
    private boolean checkIfAllowedToPlay() {
        if (!timeLimitEnabled) {
            return true; // If time limit is not enabled, allow play at any time
        }
        if (timesAllowed.isEmpty()) {
            return false; // If no time ranges are set, deny play
        }
        // Get the current hour
        int currentHour = LocalTime.now().getHour();

        // Iterate through the allowed time ranges
        for (int i = 0; i < timesAllowed.size(); i += 2) {
            int startTime = timesAllowed.get(i);
            int endTime = timesAllowed.get(i + 1);

            // Check if the current hour is within the allowed range
            if (currentHour >= startTime && currentHour < endTime) {
                return true; // User is allowed to play
            }
        }

        return false; // User is not allowed to play
    }

    /**
     * Gets whether the child is currently allowed to play based on time constraints.
     *
     * @return True if playing is permitted at this time, false otherwise.
     */
    public boolean getCurrentlyAllowedToPlay() {
        return currentlyAllowedToPlay;
    }

    /**
     * Retrieves the health value from a save file. If no data is found or the file
     * is missing, returns -1.
     *
     * @param loadSlot The file path for retrieving the pet data.
     * @return The pet's health value, or -1 if not found.
     */
    private int getPetHealth(String loadSlot) {
        int health = -1; // Default value if not found
        try (BufferedReader reader = new BufferedReader(new FileReader(loadSlot))) {
            String line = reader.readLine(); // Read the data line
            if (line != null) {
                String[] attributes = line.split(",");
                if (attributes.length > 5) { // Ensure there are enough fields
                    health = Integer.parseInt(attributes[5]);
                } else {
                    System.out.println("Invalid file format: " + loadSlot);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + loadSlot);
        } catch (IOException e) {
            System.out.println("Error reading file: " + loadSlot);
        } catch (NumberFormatException e) {
            System.out.println("Invalid health value in file: " + loadSlot);
        }
        return health;
    }

    /**
     * Revives a pet in the specified save slot by resetting
     * several stats to 100, including health, sleepiness, and fullness.
     *
     * @param slotNum The save slot number (1-3) to revive.
     */
    private void revivePet(int slotNum) {
        String fileName = "GameSaves/LoadGameSlot" + slotNum + ".csv"; // Construct the file name
        try {
            // Read the file contents
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }

            // Modify the health value
            if (!lines.isEmpty()) {
                String[] attributes = lines.get(0).split(","); // Assuming the first line contains pet data
                attributes[2] = "100"; // Set sleepiness to 100 
                attributes[3] = "100"; // Set happiness to 100 
                attributes[4] = "100"; // Set fullness to 100 
                attributes[5] = "100"; // Set health to 100 
                attributes[6] = "100"; // Set stamina to 100
                lines.set(0, String.join(",", attributes)); // Update the first line
            }

            // Write the updated content back to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            System.out.println("Pet health updated successfully in " + fileName);
        } catch (IOException ex) {
            System.out.println("Error updating pet health: " + ex.getMessage());
        }        
    }
}

