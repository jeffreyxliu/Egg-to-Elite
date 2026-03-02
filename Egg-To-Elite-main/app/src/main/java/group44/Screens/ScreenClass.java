package group44.Screens;

import group44.Pet;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.AudioClip;
import javafx.application.Platform;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Serves as a base class for all application screens, offering shared fields
 * for layout, screen dimensions, pet data, and static methods for handling audio.
 */
public class ScreenClass {

    /** The primary layout container for a screen. */
    protected BorderPane root;

    /** A Pet reference, if needed by the current screen. */
    protected Pet pet;

    /** Ratio used for scaling UI elements based on the user's screen width. */
    protected double ratio;

    /** The detected width of the primary display. */
    protected double screenWidth;

    /** The detected height of the primary display. */
    protected double screenHeight;

    /** Indicates if the current screen is for a loaded game (true) or a new one (false). */
    static protected boolean isLoadedGame = false;

    /** Holds the number of the currently loaded game slot, if any. */
    static protected int loadedSlot;

    /** A MediaPlayer for background music playback. */
    private static MediaPlayer backgroundMusicPlayer;

    /** Caches audio files already loaded, to avoid reloading them each time. */
    private static Map<String, Media> soundCache = new HashMap<>();

    /** Controls whether audio playback is enabled throughout the application. */
    private static boolean audioEnabled = true;

    /** Determines if the JavaFX Media module is present on the user's system. */
    private static final boolean MEDIA_AVAILABLE;

    // Static block checks for JavaFX media and sets MEDIA_AVAILABLE accordingly
    static {
        boolean mediaAvailable = true;
        try {
            Class.forName("javafx.scene.media.Media");
        } catch (ClassNotFoundException e) {
            System.err.println("JavaFX Media module not found. Audio features will be disabled.");
            mediaAvailable = false;
        }
        MEDIA_AVAILABLE = mediaAvailable;
    }

    /**
     * Constructs a new ScreenClass, setting up a root BorderPane and calculating
     * scaling ratios based on the user's current display.
     */
    public ScreenClass() {
        root = new BorderPane();

        // find the ratio to scale the screen and objects
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        screenWidth = bounds.getWidth();
        screenHeight = bounds.getHeight();
        ratio = screenWidth / 2000;
    }

    /**
     * Returns the root layout pane for this screen.
     *
     * @return A BorderPane containing the screen layout.
     */
    public BorderPane getRoot() {
        return root;
    }

    /**
     * Plays a looping music track. Only works if MEDIA_AVAILABLE and audio is enabled.
     *
     * @param filename The name of the file inside resources/audio/music/.
     */
    public static void playMusic(String filename) {
        if (!MEDIA_AVAILABLE || !audioEnabled) return;
        try {
            stopMusic(); // Stop any currently playing music
            Media music = getAudioMedia("music/" + filename);
            if (music != null) {
                backgroundMusicPlayer = new MediaPlayer(music);
                backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                backgroundMusicPlayer.setVolume(0.1);
                backgroundMusicPlayer.play();
                System.out.println("Now playing: " + filename);
            }
        } catch (Exception e) {
            System.err.println("Error playing music: " + e.getMessage());
        }
    }

    /**
     * Plays a short sound effect once. Only works if MEDIA_AVAILABLE and audio is enabled.
     *
     * @param filename The name of the file inside resources/audio/sfx/.
     */
    public static void playSound(String filename) {
        if (!MEDIA_AVAILABLE || !audioEnabled) {
            return;
        }
        try {
            URL resource = ScreenClass.class.getClassLoader().getResource("audio/sfx/" + filename);
            if (resource == null) {
                System.err.println("Could not find sound file: " + filename);
                return;
            }
            AudioClip clip = new AudioClip(resource.toString());
            clip.setVolume(0.7);
            clip.play();
            System.out.println("Playing sound with AudioClip: " + filename);
        } catch (Exception e) {
            System.err.println("Error playing sound with AudioClip: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Stops and disposes of any currently playing background music.
     */
    public static void stopMusic() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
            backgroundMusicPlayer.dispose();
            backgroundMusicPlayer = null;
        }
    }

    /**
     * Enables or disables all audio in the application. If disabled, pauses playback;
     * if re-enabled, continues any paused track.
     *
     * @param enabled True to allow audio, false to silence it.
     */
    public static void setAudioEnabled(boolean enabled) {
        audioEnabled = enabled;
        if (!enabled) {
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.pause();
            }
        } else if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.play();
        }
    }

    /**
     * Retrieves a Media object for the specified audio file, caching it for later use.
     *
     * @param path Relative path to the audio file inside resources/audio/.
     * @return A Media object if found, otherwise null.
     */
    private static Media getAudioMedia(String path) {
        if (!MEDIA_AVAILABLE) return null;
        if (soundCache.containsKey(path)) {
            return soundCache.get(path);
        }
        try {
            URL resource = ScreenClass.class.getClassLoader().getResource("audio/" + path);
            if (resource == null) {
                File audioFile = new File("src/main/resources/audio/" + path);
                if (audioFile.exists()) {
                    resource = audioFile.toURI().toURL();
                }
            }
            if (resource == null) {
                System.err.println("Could not find audio file: " + path);
                return null;
            }
            Media media = new Media(resource.toString());
            soundCache.put(path, media);
            return media;
        } catch (Exception e) {
            System.err.println("Error loading audio file: " + path + " - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Initializes the audio system and verifies if JavaFX Media is available.
     * If not, disables audio-related features.
     */
    public static void initializeAudioSystem() {
        if (!MEDIA_AVAILABLE) {
            System.out.println("JavaFX Media module not available - audio features disabled");
            return;
        }
        System.out.println("Audio system initialized successfully");
    }

    /**
     * Creates a large, styled button with a gradient background and hover effects.
     *
     * @param text The button label.
     * @return A styled Button object.
     */
    protected Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(280);
        button.setPrefHeight(80);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        button.setStyle(
            "-fx-background-color: linear-gradient(#4286f4, #1a56c4);" +
            "-fx-background-radius: 35;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 15;" +
            "-fx-border-color: #0a3b8c;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 35;"
        );
        button.setOnMouseEntered(e ->
            button.setStyle(
                "-fx-background-color: linear-gradient(#5296ff, #2666d4);" +
                "-fx-background-radius: 35;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 15;" +
                "-fx-border-color: #0a3b8c;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 35;"
            )
        );
        button.setOnMouseExited(e ->
            button.setStyle(
                "-fx-background-color: linear-gradient(#4286f4, #1a56c4);" +
                "-fx-background-radius: 35;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 15;" +
                "-fx-border-color: #0a3b8c;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 35;"
            )
        );
        return button;
    }

    /**
     * Creates a circular arrow button (either left or right) for pet navigation.
     * 
     * @param isLeftArrow True for a left arrow, false for a right arrow.
     * @return A styled Button object containing an arrow shape.
     */
    protected Button createArrowButton(boolean isLeftArrow) {
        Button button = new Button();
        javafx.scene.shape.Polygon arrow = new javafx.scene.shape.Polygon();
        if (isLeftArrow) {
            arrow.getPoints().addAll(20.0, 10.0, 10.0, 20.0, 20.0, 30.0);
        } else {
            arrow.getPoints().addAll(10.0, 10.0, 20.0, 20.0, 10.0, 30.0);
        }
        arrow.setFill(Color.WHITE);
        button.setGraphic(arrow);
        button.setPrefSize(80, 80);
        button.setStyle(
            "-fx-background-radius: 40;" +
            "-fx-background-color: linear-gradient(#4286f4, #1a56c4);" +
            "-fx-border-color: #0a3b8c;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 40;"
        );
        button.setOnMouseEntered(e ->
            button.setStyle(
                "-fx-background-radius: 40;" +
                "-fx-background-color: linear-gradient(#5296ff, #2666d4);" +
                "-fx-border-color: #0a3b8c;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 40;"
            )
        );
        button.setOnMouseExited(e ->
            button.setStyle(
                "-fx-background-radius: 40;" +
                "-fx-background-color: linear-gradient(#4286f4, #1a56c4);" +
                "-fx-border-color: #0a3b8c;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 40;"
            )
        );
        return button;
    }
}
