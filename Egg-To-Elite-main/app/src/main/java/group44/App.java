/**
 * The main application class for Egg to Elite. Initializes and manages
 * the primary application window, including play-time statistics and screen transitions.
 */
package group44;

import javafx.scene.control.ScrollPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import group44.Screens.MainMenu;
import group44.Screens.ScreenClass;

/**
 * App is the entry point of the application. It sets up the main menu and handles
 * tracking overall play time, transitioning between screens, and persisting user data.
 */
public class App extends Application {
    /**
     * The JavaFX scene that manages displayed content on the Stage.
     */
    private static Scene scene;
    /**
     * The JavaFX primary stage that renders the application window.
     */
    private static Stage primaryStage;
    /**
     * The name of the pet to be displayed on the GameScreen.
     */
    private static String gameScreenPetName; 
    /**
     * Reference to the currently active ScreenClass.
     */
    private static ScreenClass currentScreen;
    /**
     * A timer incremented every second while the application runs.
     */
    private static int timerValue = 0;
    /**
     * Timeline used to increment timerValue.
     */
    private Timeline timeline;
    /**
     * The total accumulated play time for all sessions.
     */
    public static long totalPlayTime = 0;
    /**
     * The number of sessions played.
     */
    public static int sessionCount = 0;

    /**
     * Application entry point. Sets up the main menu, stage, and play-time counters.
     *
     * @param stage The primary stage of the JavaFX application.
     */
    @Override
    public void start(Stage stage) {
        // Load play time statistics
        loadPlayTimeStatistics();

        // Create a Timeline that increments the timer every second
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timerValue++;
            totalPlayTime++;
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Initialize audio system
        ScreenClass.initializeAudioSystem();

        setPrimaryStage(stage);
        primaryStage.setTitle("Egg to Elite");
        
        // Create main menu
        MainMenu mainMenu = new MainMenu();

        // Set application icon
        Image icon = new Image("icon.jpg");
        primaryStage.getIcons().add(icon);

        Rectangle2D bounds = Screen.getPrimary().getBounds();
        // Create scene and set it in the stage
        scene = new Scene(mainMenu.getRoot(), bounds.getWidth() * 0.8, bounds.getHeight() * 0.8);
        setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    /**
     * Displays the specified ScreenClass. Replaces the current root node in the Scene with that of the new screen.
     *
     * @param theScreen The new ScreenClass to display.
     */
    public static void setScreen(ScreenClass theScreen) {
        scene.setRoot(theScreen.getRoot());
        currentScreen = theScreen;
    }

    /**
     * Returns the ScreenClass currently in use.
     *
     * @return The active ScreenClass.
     */
    public static ScreenClass getCurrentScreen() {
        return currentScreen;
    }

    /**
     * Stores the primary stage reference.
     *
     * @param stage The primary stage used by the application.
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Changes the existing Scene in the primary stage to the specified one.
     *
     * @param scene The new Scene to set.
     */
    public static void setScene(Scene scene) {
        if (primaryStage != null) {
            primaryStage.setScene(scene);
        } else {
            throw new IllegalStateException("Primary stage is not set.");
        }
    }

    /**
     * Retrieves the current Scene displayed by the primary stage.
     *
     * @return The current Scene.
     */
    public static Scene getScene() {
        return scene;
    }

    /**
     * Returns the primary stage of this application.
     *
     * @return The primary Stage object.
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Sets the name of the pet to be used in the GameScreen.
     *
     * @param petName The pet's display name.
     */
    public static void setGameScreenPetName(String petName) {
        gameScreenPetName = petName;
    }

    /**
     * Retrieves the currently stored pet name for the GameScreen.
     *
     * @return The name of the pet, or "Pet" if undefined.
     */
    public static String getGameScreenPetName() {
        return gameScreenPetName != null ? gameScreenPetName : "Pet";
    }

    /**
     * Returns the current timer count (in seconds) for this session.
     *
     * @return An integer representing time elapsed since session start.
     */
    public static int getTimerValue() {
        return timerValue;
    }

    /**
     * Saves total play time and session count to a CSV file.
     */
    public static void savePlayTimeStatistics() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("TimeStatistics/playTimeStats.csv"))) {
            writer.write(Long.toString(totalPlayTime));
            writer.newLine();
            writer.write(Integer.toString(sessionCount));
        } catch (IOException e) {
            System.out.println("Error saving play time statistics: " + e.getMessage());
        }
    }

    /**
     * Loads total play time and session count from a CSV file if it exists.
     */
    public static void loadPlayTimeStatistics() {
        try (BufferedReader reader = new BufferedReader(new FileReader("TimeStatistics/playTimeStats.csv"))) {
            String totalPlayTimeLine = reader.readLine();
            String sessionCountLine = reader.readLine();
            if (totalPlayTimeLine != null && sessionCountLine != null) {
                totalPlayTime = Long.parseLong(totalPlayTimeLine);
                sessionCount = Integer.parseInt(sessionCountLine);
            }
        } catch (FileNotFoundException e) {
            System.out.println("No saved play time statistics found. Starting fresh.");
            totalPlayTime = 0;
            sessionCount = 0;
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading play time statistics: " + e.getMessage());
        }
    }

    /**
     * Called when the application stops. Increments the session count,
     * saves updated play time to disk, and cleans up resources as necessary.
     */
    @Override
    public void stop() {
        // Add the session time to the total play time
        totalPlayTime += getTimerValue();
        sessionCount++;

        // Save the updated statistics
        savePlayTimeStatistics();
    }

    /**
     * The main method that launches the JavaFX application.
     *
     * @param args The CLI arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}