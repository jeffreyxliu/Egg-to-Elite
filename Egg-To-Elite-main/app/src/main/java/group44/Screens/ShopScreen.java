package group44.Screens;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import group44.App;
import group44.Inventory;
import group44.Pet;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;

/**
 * ShopScreen displays the shop interface where the player can purchase items 
 * (which increases their inventory count) and also gift items immediately.
 *
 * Each shop item row shows:
 * - The item icon (if available)
 * - A label displaying the item name, cost, and current quantity.
 * - A "Buy" button: purchases one unit (if the pet has enough coins) and increments quantity.
 * - A "Gift" button: uses one unit (if available) and applies its effect to the pet.
 *
 * Effects:
 * Food Items:
 *   - Apple: +5 fullness
 *   - Orange: +7 fullness
 *   - Banana: +10 fullness
 * Gift Items:
 *   - Ball: +10 happiness
 *   - Teddy Bear: +15 happiness
 *   - Pirate Hat: +30 happiness
 *   - Lollipop: +5 happiness
 *   - Balloon: +12 happiness
 *
 * The pet's current state is also displayed in the middle.
 */
public class ShopScreen extends ScreenClass {

    /** Reference to the pet whose stats and coins are being modified. */
    private Pet pet;

    /** Label displaying how many coins the pet has. */
    private Label coinCountLabel;

    /** Label for the pet's health stat. */
    private Label healthLabel;
    /** Label for the pet's sleepiness stat. */
    private Label sleepinessLabel;
    /** Label for the pet's fullness stat. */
    private Label fullnessLabel;
    /** Label for the pet's happiness stat. */
    private Label happinessLabel;

    /** Food items available in the shop. */
    private static final String[] foodItems = {"Apple", "Banana", "Orange"};
    /** Gift items available in the shop. */
    private static final String[] giftItems = {"Ball", "Teddy Bear", "Pirate Hat", "Lollipop", "Balloon"};

    /** A map of item names to their prices. */
    private static final Map<String, Integer> shopPrices = new HashMap<>();
    static {
        shopPrices.put("Apple", 10);
        shopPrices.put("Banana", 15);
        shopPrices.put("Orange", 12);
        shopPrices.put("Ball", 20);
        shopPrices.put("Teddy Bear", 25);
        shopPrices.put("Pirate Hat", 30);
        shopPrices.put("Lollipop", 5);
        shopPrices.put("Balloon", 7);
    }

    /** A map of item names to resource paths for their icons. */
    private static final Map<String, String> itemIcons = new HashMap<>();
    static {
        itemIcons.put("Apple", "/images/apple.png");
        itemIcons.put("Banana", "/images/banana.png");
        itemIcons.put("Orange", "/images/orange.png");
        itemIcons.put("Ball", "/images/ball.png");
        itemIcons.put("Teddy Bear", "/images/teddybear.png");
        itemIcons.put("Pirate Hat", "/images/piratehat.png");
        itemIcons.put("Lollipop", "/images/lollipop.png");
        itemIcons.put("Balloon", "/images/balloon.png");
    }

    /** A map mapping food items to their fullness increase value. */
    private static final Map<String, Integer> foodEffects = new HashMap<>();
    static {
        foodEffects.put("Apple", 5);
        foodEffects.put("Orange", 7);
        foodEffects.put("Banana", 10);
    }

    /** A map mapping gift items to their happiness increase value. */
    private static final Map<String, Integer> giftEffects = new HashMap<>();
    static {
        giftEffects.put("Ball", 10);
        giftEffects.put("Teddy Bear", 15);
        giftEffects.put("Pirate Hat", 30);
        giftEffects.put("Lollipop", 5);
        giftEffects.put("Balloon", 12);
    }

    /**
     * Constructs a ShopScreen for the given pet, initializing inventory items to ensure
     * each item has at least a quantity of 1, and then building the UI.
     *
     * @param pet the Pet object whose inventory and stats are tied to this shop.
     */
    public ShopScreen(Pet pet) {
        this.pet = pet;
        Inventory inv = pet.getInventory();

        // Initialize each shop item in the pet's inventory to 1 if not present
        for (String food : foodItems) {
            if (!inv.getFoodItems().containsKey(food)) {
                inv.addFood(food, 1);
            }
        }
        for (String gift : giftItems) {
            if (!inv.getGiftItems().containsKey(gift)) {
                inv.addGift(gift, 1);
            }
        }

        setupUI();
    }

    /**
     * Sets up the user interface, including background, top section (title, back button, coins),
     * and a center section displaying both the pet's state and shop items.
     */
    private void setupUI() {
        root.setPadding(new Insets(20));

        Image backgroundImage = new Image("sunny1.jpg");
        BackgroundImage background = new BackgroundImage(
            backgroundImage,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(1.0, 1.0, true, true, false, false)
        );
        root.setBackground(new Background(background));

        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.TOP_LEFT);

        Label titleLabel = new Label("Shop");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.rgb(30, 100, 200));

        Button backButton = createStyledButton("Back to Game");
        backButton.setOnAction(e -> App.setScreen(new GameScreen(pet)));

        coinCountLabel = new Label("Coins: " + pet.getCoins());
        coinCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        coinCountLabel.setTextFill(Color.DARKGREEN);

        topBox.getChildren().addAll(titleLabel, backButton, coinCountLabel);
        root.setTop(topBox);

        VBox stateBox = createPetStateBox();
        VBox centerBox = new VBox(30);
        centerBox.setAlignment(Pos.CENTER_LEFT);
        centerBox.setPadding(new Insets(20));
        centerBox.getChildren().add(stateBox);

        Label foodSectionLabel = new Label("Food Items");
        foodSectionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        VBox foodItemsBox = new VBox(10);
        foodItemsBox.setPadding(new Insets(10, 0, 10, 20));
        for (String foodName : foodItems) {
            HBox rowBox = createShopItemRow(foodName, "food");
            foodItemsBox.getChildren().add(rowBox);
        }

        Label giftSectionLabel = new Label("Gift Items");
        giftSectionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        VBox giftItemsBox = new VBox(10);
        giftItemsBox.setPadding(new Insets(10, 0, 10, 20));
        for (String giftName : giftItems) {
            HBox rowBox = createShopItemRow(giftName, "gift");
            giftItemsBox.getChildren().add(rowBox);
        }

        centerBox.getChildren().addAll(foodSectionLabel, foodItemsBox, giftSectionLabel, giftItemsBox);
        root.setCenter(centerBox);
    }

    /**
     * Creates a VBox displaying the pet's current state, such as Health,
     * Sleepiness, Fullness, and Happiness.
     *
     * @return a VBox containing labels for the pet state.
     */
    private VBox createPetStateBox() {
        VBox stateBox = new VBox(10);
        stateBox.setAlignment(Pos.CENTER);
        Label stateTitle = new Label("Pet Current State");
        stateTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        healthLabel = new Label("Health: " + pet.getHealth());
        sleepinessLabel = new Label("Sleepiness: " + pet.getSleepiness());
        fullnessLabel = new Label("Fullness: " + pet.getFullness());
        happinessLabel = new Label("Happiness: " + pet.getHappiness());
        stateBox.getChildren().addAll(stateTitle, healthLabel, sleepinessLabel, fullnessLabel, happinessLabel);
        return stateBox;
    }

    /**
     * Updates the displayed state labels based on the pet's current stats.
     */
    private void updateStateLabels() {
        healthLabel.setText("Health: " + pet.getHealth());
        sleepinessLabel.setText("Sleepiness: " + pet.getSleepiness());
        fullnessLabel.setText("Fullness: " + pet.getFullness());
        happinessLabel.setText("Happiness: " + pet.getHappiness());
    }

    /**
     * Creates a row for a shop item with both "Buy" and "Gift" buttons and an icon (if available).
     * Buying reduces pet coins and increases item quantity. Gifting uses one item, 
     * applying its effect to fullness or happiness.
     *
     * @param itemName the name of the item
     * @param category either "food" or "gift"
     * @return an HBox representing the row for this shop item
     */
    private HBox createShopItemRow(String itemName, String category) {
        HBox rowBox = new HBox(10);
        rowBox.setAlignment(Pos.CENTER_LEFT);

        // Load icon for the item if available
        String iconPath = itemIcons.get(itemName);
        if (iconPath != null) {
            URL resource = getClass().getResource(iconPath);
            if (resource != null) {
                try {
                    ImageView iconView = new ImageView(new Image(resource.toExternalForm()));
                    iconView.setFitWidth(32);
                    iconView.setFitHeight(32);
                    iconView.setPreserveRatio(true);
                    rowBox.getChildren().add(iconView);
                } catch (Exception ex) {
                    System.out.println("Could not load image for " + itemName + ": " + ex.getMessage());
                }
            } else {
                System.out.println("Resource not found for " + itemName + " at " + iconPath);
            }
        }

        int price = shopPrices.getOrDefault(itemName, 0);
        Label itemLabel = new Label();
        itemLabel.setFont(Font.font("Arial", 16));

        Runnable updateLabel = () -> {
            int currentQty;
            if ("food".equals(category)) {
                currentQty = pet.getInventory().getFoodItems().getOrDefault(itemName, 0);
            } else {
                currentQty = pet.getInventory().getGiftItems().getOrDefault(itemName, 0);
            }
            itemLabel.setText(itemName + " - " + price + " coins (Qty: " + currentQty + ")");
        };
        updateLabel.run();

        Button buyButton = createStyledButton("Buy");
        buyButton.setOnAction(e -> {
            if (pet.getCoins() >= price) {
                pet.setCoins(pet.getCoins() - price);
                if ("food".equals(category)) {
                    pet.getInventory().addFood(itemName, 1);
                } else {
                    pet.getInventory().addGift(itemName, 1);
                }
                coinCountLabel.setText("Coins: " + pet.getCoins());
                updateLabel.run();
                System.out.println("Purchased 1 " + itemName);
            } else {
                System.out.println("Not enough coins to buy " + itemName);
            }
        });

        Button giftButton = createStyledButton("Gift");
        giftButton.setOnAction(e -> {
            if ("food".equals(category)) {
                if (pet.getInventory().useFood(itemName, 1)) {
                    int effect = foodEffects.getOrDefault(itemName, 0);
                    pet.setFullness(Math.min(100, pet.getFullness() + effect));
                    System.out.println("Gifted " + itemName + ", + " + effect + " fullness");
                } else {
                    System.out.println("No " + itemName + " available to gift.");
                }
            } else {
                if (pet.getInventory().useGift(itemName, 1)) {
                    int effect = giftEffects.getOrDefault(itemName, 0);
                    pet.setHappiness(Math.min(100, pet.getHappiness() + effect));
                    System.out.println("Gifted " + itemName + ", + " + effect + " happiness");
                } else {
                    System.out.println("No " + itemName + " available to gift.");
                }
            }
            updateLabel.run();
            updateStateLabels();
        });

        rowBox.getChildren().addAll(itemLabel, buyButton, giftButton);
        return rowBox;
    }

    /**
     * Creates a basic styled button specific to ShopScreen
     * with updated font size and color settings.
     *
     * @param text The text for the button.
     * @return A customized Button instance.
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
}
