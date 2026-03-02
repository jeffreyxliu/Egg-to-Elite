package group44.Screens;

import group44.App;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * The InstructionsScreen class displays the tutorial and help instructions
 * for the "Egg to Elite" game. It utilizes a scrollable layout with various
 * themed sections to guide players on how to care for and train their virtual pet.
 */
public class InstructionsScreen extends ScreenClass {

    /**
     * Creates a new InstructionsScreen and initializes the layout,
     * including background color, a title label, navigation instructions,
     * and a scrollable container for multiple instruction sections.
     */
    public InstructionsScreen() {
        // Set background color
        root.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #f0f8ff, #e6f0ff);");
        root.setPadding(new Insets(20));

        // Create title
        Label titleLabel = new Label("How to Play Egg to Elite");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.rgb(30, 100, 200));
        titleLabel.setAlignment(Pos.CENTER);

        // Create navigation hint label
        Label navigationHintLabel = new Label("Navigate the instructions by scrolling or using UP/DOWN arrow keys!");
        navigationHintLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        navigationHintLabel.setTextFill(Color.rgb(60, 60, 60));
        navigationHintLabel.setPadding(new Insets(5, 10, 5, 10));
        navigationHintLabel.setBackground(new Background(new BackgroundFill(
            Color.rgb(255, 255, 200, 0.8), // Light yellow with some transparency
            new CornerRadii(5),
            Insets.EMPTY
        )));
        navigationHintLabel.setBorder(new Border(new BorderStroke(
            Color.rgb(200, 200, 100),
            BorderStrokeStyle.SOLID,
            new CornerRadii(5),
            new BorderWidths(1)
        )));

        // Create back button
        Button backButton = createStyledButton("Back to Menu");
        backButton.setOnAction(e -> App.setScreen(new MainMenu()));

        // Create main content box
        VBox contentBox = new VBox(25);
        contentBox.setPadding(new Insets(20));
        contentBox.setAlignment(Pos.TOP_CENTER);

        // Add instruction sections, each centered horizontally
        contentBox.getChildren().addAll(
            createCenteredSection(createWelcomeSection()),
            createCenteredSection(createBasicCareSection()),
            createCenteredSection(createFeedingSection()),
            createCenteredSection(createSleepingSection()),
            createCenteredSection(createHappinessSection()),
            createCenteredSection(createSkillsSection()),
            createCenteredSection(createMoneySection()),
            createCenteredSection(createGameOverSection())
        );

        // Create a scroll pane for the content
        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setPrefViewportHeight(600);

        // Enable keyboard scrolling with UP/DOWN arrows
        final double scrollAmount = 30.0;
        scrollPane.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP:
                    scrollPane.setVvalue(scrollPane.getVvalue()
                        - scrollAmount / scrollPane.getContent().getBoundsInLocal().getHeight());
                    event.consume();
                    break;
                case DOWN:
                    scrollPane.setVvalue(scrollPane.getVvalue()
                        + scrollAmount / scrollPane.getContent().getBoundsInLocal().getHeight());
                    event.consume();
                    break;
                default:
                    break;
            }
        });

        // Make scrollPane focusable
        scrollPane.setFocusTraversable(true);
        // Request focus to enable arrow key scrolling immediately
        Platform.runLater(scrollPane::requestFocus);

        // Use BorderPane for layout structure
        BorderPane mainPane = new BorderPane();

        // Header with title and navigation hint
        VBox headerBox = new VBox(15);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(0, 0, 15, 0));
        headerBox.getChildren().add(titleLabel);

        StackPane navigationHintPane = new StackPane();
        navigationHintPane.setAlignment(Pos.TOP_LEFT);
        navigationHintPane.getChildren().add(navigationHintLabel);
        headerBox.getChildren().add(navigationHintPane);

        mainPane.setTop(headerBox);
        mainPane.setCenter(scrollPane);

        // Footer with back button
        HBox footerBox = new HBox();
        footerBox.setAlignment(Pos.CENTER);
        footerBox.setPadding(new Insets(15, 0, 0, 0));
        footerBox.getChildren().add(backButton);
        mainPane.setBottom(footerBox);

        // Use the center of the root
        root.setCenter(mainPane);
    }

    /**
     * Wraps a given VBox section in an HBox for horizontal centering.
     *
     * @param section The VBox containing an instruction section.
     * @return An HBox that is centered and contains the given section.
     */
    private HBox createCenteredSection(VBox section) {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);
        container.getChildren().add(section);
        return container;
    }

    /**
     * Creates a colored section containing a title label and returns it as a VBox.
     * The section is styled with a background color, rounded corners, and a drop shadow.
     *
     * @param color A hex color string for the background (e.g., "#2E74B5").
     * @param title The title to display at the top of the section.
     * @param textColor The color used for the section's title text.
     * @return A styled VBox containing a title label and space for content.
     */
    private VBox createColoredSection(String color, String title, Color textColor) {
        VBox section = new VBox(15);
        section.setAlignment(Pos.TOP_LEFT);
        section.setPadding(new Insets(20));
        section.setMaxWidth(750);
        section.setPrefWidth(750);
        section.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.0, 0.0, 4);"
        );

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(textColor);
        titleLabel.setTextAlignment(TextAlignment.LEFT);

        section.getChildren().add(titleLabel);
        return section;
    }

    /**
     * Creates a welcome section describing the basic premise of the game.
     *
     * @return A VBox with a title background, descriptive text, and an image.
     */
    private VBox createWelcomeSection() {
        VBox section = createColoredSection("#2E74B5", "Welcome to Egg to Elite!", Color.WHITE);

        Label infoLabel = new Label(
            "This is your very own pet duck game! In Egg to Elite, you will:\n\n" +
            "• Take care of your own pet duck\n" +
            "• Feed it when it's hungry\n" +
            "• Help it sleep when it's tired\n" +
            "• Train it to become stronger and better at skills"
        );
        infoLabel.setWrapText(true);
        infoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        infoLabel.setTextFill(Color.WHITE);

        HBox imageBox = new HBox();
        imageBox.setAlignment(Pos.CENTER);
        ImageView duckImage = createInstructionImage("yellow.png", 120);
        imageBox.getChildren().add(duckImage);

        section.getChildren().addAll(infoLabel, imageBox);
        return section;
    }

    /**
     * Creates a section explaining basic care for the duck, including health,
     * sleep, happiness, and fullness stats.
     *
     * @return A VBox describing the importance of keeping the duck healthy.
     */
    private VBox createBasicCareSection() {
        VBox section = createColoredSection("#1E8449", "Taking Care of Your Duck", Color.WHITE);

        Label infoLabel = new Label(
            "Your duck needs your help to stay healthy and happy! Watch these important things:"
        );
        infoLabel.setWrapText(true);
        infoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        infoLabel.setTextFill(Color.WHITE);

        VBox careList = new VBox(12);
        careList.getChildren().addAll(
            createBulletPoint("Health: This shows how healthy your duck is. If it reaches zero, your duck will die!", Color.WHITE),
            createBulletPoint("Sleep: Your duck needs to rest. When this gets too low, your duck will fall asleep.", Color.WHITE),
            createBulletPoint("Happiness: Keep your duck happy by training it and giving it gifts.", Color.WHITE),
            createBulletPoint("Fullness: Feed your duck when it's hungry. A hungry duck gets sad and tired.", Color.WHITE)
        );

        HBox imageBox = new HBox();
        imageBox.setAlignment(Pos.CENTER);
        ImageView statsImage = createInstructionImage("stats_example.png", 300);
        imageBox.getChildren().add(statsImage);

        section.getChildren().addAll(infoLabel, careList, imageBox);
        return section;
    }

    /**
     * Creates a section describing how to feed the duck using items from a shop.
     *
     * @return A VBox explaining hunger mechanics and feeding strategies.
     */
    private VBox createFeedingSection() {
        VBox section = createColoredSection("#117A65", "Feeding Your Duck", Color.WHITE);

        Label infoLabel = new Label(
            "Your duck gets hungry over time. When its fullness bar gets low:\n\n" +
            "• Buy food from the shop\n" +
            "• Different foods help your duck in different ways\n" +
            "• A well-fed duck is a happy duck!\n" +
            "• If your duck is too hungry for too long, it will lose health\n" +
            "• Traininig your duck will make it hungry!"
        );
        infoLabel.setWrapText(true);
        infoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        infoLabel.setTextFill(Color.WHITE);

        section.getChildren().add(infoLabel);
        return section;
    }

    /**
     * Creates a section explaining how the duck sleeps and why rest is essential.
     *
     * @return A VBox describing rest mechanics and the Sleep button.
     */
    private VBox createSleepingSection() {
        VBox section = createColoredSection("#1A5276", "Sleep Time", Color.WHITE);

        Label infoLabel = new Label(
            "Your duck needs to sleep to stay healthy:\n\n" +
            "• The sleep bar shows how tired your duck is\n" +
            "• When the sleep bar gets too low, your duck will fall asleep on its own\n" +
            "• You can also put your duck to sleep using the Sleep button\n" +
            "• When your duck is sleeping, its sleep will slowly fill up again\n" +
            "• Training your duck while it tired!"
        );
        infoLabel.setWrapText(true);
        infoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        infoLabel.setTextFill(Color.WHITE);

        section.getChildren().add(infoLabel);
        return section;
    }

    /**
     * Creates a section covering ways to maintain the duck's happiness level,
     * such as giving gifts and playing together.
     *
     * @return A VBox discussing happiness mechanics.
     */
    private VBox createHappinessSection() {
        VBox section = createColoredSection("#8E44AD", "Keeping Your Duck Happy", Color.WHITE);

        Label infoLabel = new Label(
            "A happy duck is a healthy duck!\n\n" +
            "• Give your duck gifts to make it happy\n" +
            "• Play with your duck by training its skills\n" +
            "• Keep your duck well-fed and well-rested"
        );
        infoLabel.setWrapText(true);
        infoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        infoLabel.setTextFill(Color.WHITE);

        section.getChildren().add(infoLabel);
        return section;
    }

    /**
     * Creates a section explaining the duck's three main skills and how players can
     * train them to earn coins and improve their duck.
     *
     * @return A VBox describing running, swimming, and flying skills.
     */
    private VBox createSkillsSection() {
        VBox section = createColoredSection("#D35400", "Training Your Duck's Skills", Color.WHITE);

        Label infoLabel = new Label(
            "Your duck can learn three amazing skills. Training will give you coins to care for your pet!"
        );
        infoLabel.setWrapText(true);
        infoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        infoLabel.setTextFill(Color.WHITE);

        VBox skillsList = new VBox(15);

        // Running skill
        Label runningTitle = new Label("Running");
        runningTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        runningTitle.setTextFill(Color.WHITE);

        Label runningInfo = new Label(
            "• Train your duck to run super fast!\n" +
            "• Play the running game to earn running experience"
        );
        runningInfo.setWrapText(true);
        runningInfo.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        runningInfo.setTextFill(Color.WHITE);

        // Swimming skill
        Label swimmingTitle = new Label("Swimming");
        swimmingTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        swimmingTitle.setTextFill(Color.WHITE);

        Label swimmingInfo = new Label(
            "• Ducks love water! Help yours become a champion swimmer\n" +
            "• Play the swimming game to earn swimming experience"
        );
        swimmingInfo.setWrapText(true);
        swimmingInfo.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        swimmingInfo.setTextFill(Color.WHITE);

        // Flying skill
        Label flyingTitle = new Label("Flying");
        flyingTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        flyingTitle.setTextFill(Color.WHITE);

        Label flyingInfo = new Label(
            "• Help your duck learn to fly high in the sky\n" +
            "• Play the flying game to earn flying experience"
        );
        flyingInfo.setWrapText(true);
        flyingInfo.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        flyingInfo.setTextFill(Color.WHITE);

        HBox imageBox = new HBox();
        imageBox.setAlignment(Pos.CENTER);
        ImageView statsImage = createInstructionImage("training_example.png", 300);
        imageBox.getChildren().add(statsImage);

        skillsList.getChildren().addAll(
            runningTitle, runningInfo,
            new Label(""), // Spacer
            swimmingTitle, swimmingInfo,
            new Label(""), // Spacer
            flyingTitle, flyingInfo
        );

        section.getChildren().addAll(infoLabel, skillsList, imageBox);
        return section;
    }

    /**
     * Creates a section covering how to earn coins (through training) and how they
     * can be spent on food and items to care for the duck.
     *
     * @return A VBox describing coin usage and rewards.
     */
    private VBox createMoneySection() {
        VBox section = createColoredSection("#B7950B", "Coins and Rewards", Color.WHITE);

        Label infoLabel = new Label(
            "Your duck can earn coins by training and playing games!\n\n" +
            "• Use coins to buy food for your duck\n" +
            "• Buy special gifts to make your duck extra happy\n" +
            "• Save your coins for special items in the shop"
        );
        infoLabel.setWrapText(true);
        infoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        infoLabel.setTextFill(Color.WHITE);

        section.getChildren().add(infoLabel);
        return section;
    }

    /**
     * Creates a section explaining the conditions under which the duck can die,
     * encouraging the player to maintain health, happiness, and other stats.
     *
     * @return A VBox describing game-over conditions.
     */
    private VBox createGameOverSection() {
        VBox section = createColoredSection("#922B21", "Taking Good Care", Color.WHITE);

        Label warningLabel = new Label(
            "If you don't take care of your duck, it will pass away and the game will end.\n\n" +
            "To keep your duck healthy:\n\n" +
            "• Never let your duck stay hungry for too long\n" +
            "• Make sure your duck gets plenty of sleep\n" +
            "• Keep your duck happy with gifts and activities\n" +
            "• Save your game often so you don't lose progress\n" +
            "• Taking your pet to the vet will increase its health\n" +
            "• Parental controls can be used to revive dead pets"
        );
        warningLabel.setWrapText(true);
        warningLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        warningLabel.setTextFill(Color.WHITE);

        section.getChildren().add(warningLabel);
        return section;
    }

    /**
     * Creates a bullet point item containing an icon and text, used for lists
     * within various instruction sections.
     *
     * @param text The text describing a particular point or tip.
     * @param textColor The color for the bullet icon and text.
     * @return A horizontally arranged bullet point item.
     */
    private HBox createBulletPoint(String text, Color textColor) {
        HBox bulletPoint = new HBox(10);
        bulletPoint.setAlignment(Pos.TOP_LEFT);

        Label bullet = new Label("•");
        bullet.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        bullet.setTextFill(textColor);

        Label content = new Label(text);
        content.setWrapText(true);
        content.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        content.setTextFill(textColor);

        bulletPoint.getChildren().addAll(bullet, content);
        return bulletPoint;
    }

    /**
     * Attempts to load an image by filename and creates an ImageView with a
     * specified height. If the image fails to load, returns an empty ImageView.
     *
     * @param imageName The filename of the image (e.g., "yellow.png").
     * @param size The desired height of the displayed image (width is scaled).
     * @return An ImageView containing the loaded image or empty if loading fails.
     */
    private ImageView createInstructionImage(String imageName, double size) {
        try {
            Image image = new Image(imageName);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(size);
            imageView.setPreserveRatio(true);

            // Add a white border around images for better visibility
            imageView.setStyle(
                "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.8), 10, 0.0, 0.0, 0.0);"
            );

            return imageView;
        } catch (Exception e) {
            System.out.println("Couldn't load image: " + imageName);
            return new ImageView();
        }
    }
}