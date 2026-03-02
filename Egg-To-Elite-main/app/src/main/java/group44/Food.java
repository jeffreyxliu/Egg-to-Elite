/**
 * The Food class represents a consumable item that the pet can eat
 * to increase its fullness and happiness, with an associated cost and icon.
 */
package group44;

import javafx.scene.image.Image;

public class Food {
    /**
     * The name of the food item.
     */
    private String name;

    /**
     * Amount of fullness this food restores.
     */
    private int nutrition;

    /**
     * Amount of happiness bonus provided by this food.
     */
    private int tastyBonus;

    /**
     * The cost of this food in coins.
     */
    private int cost;

    /**
     * The icon image representing this food.
     */
    private Image icon;

    /**
     * Constructs a Food object with the given properties.
     *
     * @param name The name of the food.
     * @param nutrition The amount of fullness the food restores.
     * @param tastyBonus The happiness bonus the food provides.
     * @param cost The food's price in coins.
     * @param iconPath The image path for the food's icon.
     */
    public Food(String name, int nutrition, int tastyBonus, int cost, String iconPath) {
        this.name = name;
        this.nutrition = nutrition;
        this.tastyBonus = tastyBonus;
        this.cost = cost;
        try {
            this.icon = new Image(iconPath);
        } catch (Exception e) {
            // Default icon or placeholder
        }
    }

    /**
     * Gets the name of the food item.
     * @return The food's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the food item.
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the nutritional value of the food.
     * @return The food's nutrition value
     */
    public int getNutrition() {
        return nutrition;
    }

    /**
     * Sets the nutritional value of the food.
     * @param nutrition The nutrition value to set
     */
    public void setNutrition(int nutrition) {
        this.nutrition = nutrition;
    }

    /**
     * Gets the happiness bonus provided by the food.
     * @return The happiness bonus
     */
    public int getTastyBonus() {
        return tastyBonus;
    }

    /**
     * Sets the happiness bonus provided by the food.
     * @param tastyBonus The happiness bonus to set
     */
    public void setTastyBonus(int tastyBonus) {
        this.tastyBonus = tastyBonus;
    }

    /**
     * Gets the cost of the food in coins.
     * @return The food's cost
     */
    public int getCost() {
        return cost;
    }

    /**
     * Sets the cost of the food in coins.
     * @param cost The cost to set
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * Gets the icon image for the food.
     * @return The food's icon
     */
    public Image getIcon() {
        return icon;
    }

    /**
     * Sets the icon image for the food.
     * @param icon The icon to set
     */
    public void setIcon(Image icon) {
        this.icon = icon;
    }
}