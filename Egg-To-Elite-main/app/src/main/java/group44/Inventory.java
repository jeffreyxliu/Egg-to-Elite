/**
 * The Inventory class manages collections of food and gift items. It supports
 * adding, using, and serializing/deserializing items to and from a CSV-like string.
 */
package group44;

import java.util.HashMap;
import java.util.Map;

public class Inventory {

    /**
     * A map of food item names to their quantities.
     */
    private Map<String, Integer> foodItems;

    /**
     * A map of gift item names to their quantities.
     */
    private Map<String, Integer> giftItems;

    /**
     * Constructs an empty Inventory with no food or gift items.
     */
    public Inventory() {
        foodItems = new HashMap<>();
        giftItems = new HashMap<>();
    }

    /**
     * Returns the map of food items stored in the inventory.
     *
     * @return A Map where keys are food item names and values are item quantities.
     */
    public Map<String, Integer> getFoodItems() {
        return foodItems;
    }

    /**
     * Returns the map of gift items stored in the inventory.
     *
     * @return A Map where keys are gift item names and values are item quantities.
     */
    public Map<String, Integer> getGiftItems() {
        return giftItems;
    }

    /**
     * Adds or increases the quantity of a specified food item in the inventory.
     *
     * @param foodName The name of the food item.
     * @param quantity How many units of the food are added.
     */
    public void addFood(String foodName, int quantity) {
        foodItems.put(foodName, foodItems.getOrDefault(foodName, 0) + quantity);
    }

    /**
     * Attempts to use a specified quantity of a food item from the inventory.
     *
     * @param foodName The name of the food item to use.
     * @param quantity How many units of the food to use.
     * @return True if the food was successfully used; false if insufficient quantity.
     */
    public boolean useFood(String foodName, int quantity) {
        if (foodItems.containsKey(foodName) && foodItems.get(foodName) >= quantity) {
            foodItems.put(foodName, foodItems.get(foodName) - quantity);
            if (foodItems.get(foodName) <= 0) {
                foodItems.remove(foodName);
            }
            return true;
        }
        return false;
    }

    /**
     * Adds or increases the quantity of a specified gift item in the inventory.
     *
     * @param giftName The name of the gift item.
     * @param quantity How many units of the gift are added.
     */
    public void addGift(String giftName, int quantity) {
        giftItems.put(giftName, giftItems.getOrDefault(giftName, 0) + quantity);
    }

    /**
     * Attempts to use a specified quantity of a gift item from the inventory.
     *
     * @param giftName The name of the gift item to use.
     * @param quantity How many units of the gift to use.
     * @return True if the gift was successfully used; false if insufficient quantity.
     */
    public boolean useGift(String giftName, int quantity) {
        if (giftItems.containsKey(giftName) && giftItems.get(giftName) >= quantity) {
            giftItems.put(giftName, giftItems.get(giftName) - quantity);
            if (giftItems.get(giftName) <= 0) {
                giftItems.remove(giftName);
            }
            return true;
        }
        return false;
    }

    /**
     * Serializes the inventory contents into a CSV-like string.
     * Format example: "food:Apple=3|Banana=2;gift:Ball=1|Teddy Bear=2".
     *
     * @return A String representation of the inventory.
     */
    public String toCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append("food:");
        boolean first = true;
        for (Map.Entry<String, Integer> entry : foodItems.entrySet()) {
            if (!first) {
                sb.append("|");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }
        sb.append(";");
        sb.append("gift:");
        first = true;
        for (Map.Entry<String, Integer> entry : giftItems.entrySet()) {
            if (!first) {
                sb.append("|");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }
        return sb.toString();
    }

    /**
     * Clears current items and loads inventory data from a CSV-like string.
     * Expected format: "food:Apple=3|Banana=2;gift:Ball=1|Teddy Bear=2".
     *
     * @param csv The CSV-like string to parse.
     */
    public void fromCSV(String csv) {
        foodItems.clear();
        giftItems.clear();
        if (csv == null || csv.isEmpty()) {
            return;
        }
        // Expected format: food:Apple=3|Banana=2;gift:Ball=1|Teddy Bear=2
        String[] parts = csv.split(";");
        for (String part : parts) {
            if (part.startsWith("food:")) {
                String foods = part.substring(5);
                if (!foods.isEmpty()) {
                    String[] items = foods.split("\\|");
                    for (String item : items) {
                        String[] pair = item.split("=");
                        if (pair.length == 2) {
                            String name = pair[0];
                            int qty = Integer.parseInt(pair[1]);
                            foodItems.put(name, qty);
                        }
                    }
                }
            } else if (part.startsWith("gift:")) {
                String gifts = part.substring(5);
                if (!gifts.isEmpty()) {
                    String[] items = gifts.split("\\|");
                    for (String item : items) {
                        String[] pair = item.split("=");
                        if (pair.length == 2) {
                            String name = pair[0];
                            int qty = Integer.parseInt(pair[1]);
                            giftItems.put(name, qty);
                        }
                    }
                }
            }
        }
    }
}
