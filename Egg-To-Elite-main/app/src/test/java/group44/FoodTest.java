package group44;

import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for the Food class.
 * Tests the creation and functionality of Food objects.
 */
public class FoodTest {
    
    private Food food;
    private final String TEST_FOOD_NAME = "Apple";
    private final int TEST_NUTRITION = 20;
    private final int TEST_TASTY_BONUS = 10;
    private final int TEST_COST = 5;
    private final String TEST_ICON_PATH = "nonexistent_path.png"; // Using a non-existent path to test exception handling
    
    @BeforeEach
    public void setUp() {
        food = new Food(TEST_FOOD_NAME, TEST_NUTRITION, TEST_TASTY_BONUS, TEST_COST, TEST_ICON_PATH);
    }
    
    /**
     * Test the constructor initializes values correctly.
     */
    @Test
    public void testConstructor() {
        assertEquals(TEST_FOOD_NAME, food.getName(), "Food name should be initialized correctly");
        assertEquals(TEST_NUTRITION, food.getNutrition(), "Nutrition value should be initialized correctly");
        assertEquals(TEST_TASTY_BONUS, food.getTastyBonus(), "Tasty bonus should be initialized correctly");
        assertEquals(TEST_COST, food.getCost(), "Cost should be initialized correctly");
        assertNull(food.getIcon(), "Icon should be null for invalid path");
    }
    
    /**
     * Test setting and getting the name.
     */
    @Test
    public void testSetGetName() {
        String newName = "Banana";
        food.setName(newName);
        assertEquals(newName, food.getName(), "getName should return the updated name");
    }
    
    /**
     * Test setting and getting the nutrition value.
     */
    @Test
    public void testSetGetNutrition() {
        int newNutrition = 30;
        food.setNutrition(newNutrition);
        assertEquals(newNutrition, food.getNutrition(), "getNutrition should return the updated nutrition value");
    }
    
    /**
     * Test setting and getting the tasty bonus.
     */
    @Test
    public void testSetGetTastyBonus() {
        int newTastyBonus = 15;
        food.setTastyBonus(newTastyBonus);
        assertEquals(newTastyBonus, food.getTastyBonus(), "getTastyBonus should return the updated tasty bonus");
    }
    
    /**
     * Test setting and getting the cost.
     */
    @Test
    public void testSetGetCost() {
        int newCost = 8;
        food.setCost(newCost);
        assertEquals(newCost, food.getCost(), "getCost should return the updated cost");
    }
    
    /**
     * Test creating a food with zero values.
     */
    @Test
    public void testZeroValues() {
        Food zeroFood = new Food("Free Food", 0, 0, 0, TEST_ICON_PATH);
        
        assertEquals("Free Food", zeroFood.getName(), "Name should be set correctly");
        assertEquals(0, zeroFood.getNutrition(), "Nutrition should be 0");
        assertEquals(0, zeroFood.getTastyBonus(), "Tasty bonus should be 0");
        assertEquals(0, zeroFood.getCost(), "Cost should be 0");
    }
    
    /**
     * Test creating a food with negative values.
     */
    @Test
    public void testNegativeValues() {
        Food negativeFood = new Food("Negative Food", -10, -5, -3, TEST_ICON_PATH);
        
        assertEquals("Negative Food", negativeFood.getName(), "Name should be set correctly");
        assertEquals(-10, negativeFood.getNutrition(), "Nutrition should allow negative values");
        assertEquals(-5, negativeFood.getTastyBonus(), "Tasty bonus should allow negative values");
        assertEquals(-3, negativeFood.getCost(), "Cost should allow negative values");
    }
    
    /**
     * Test creating a food with null name.
     */
    @Test
    public void testNullName() {
        Food nullNameFood = new Food(null, TEST_NUTRITION, TEST_TASTY_BONUS, TEST_COST, TEST_ICON_PATH);
        
        assertNull(nullNameFood.getName(), "Name should be null");
        assertEquals(TEST_NUTRITION, nullNameFood.getNutrition(), "Nutrition should be set correctly");
        assertEquals(TEST_TASTY_BONUS, nullNameFood.getTastyBonus(), "Tasty bonus should be set correctly");
        assertEquals(TEST_COST, nullNameFood.getCost(), "Cost should be set correctly");
    }
    
    /**
     * Test creating multiple food objects.
     */
    @Test
    public void testMultipleFoodObjects() {
        Food food1 = new Food("Apple", 20, 10, 5, TEST_ICON_PATH);
        Food food2 = new Food("Carrot", 15, 5, 3, TEST_ICON_PATH);
        
        assertEquals("Apple", food1.getName(), "First food should have correct name");
        assertEquals("Carrot", food2.getName(), "Second food should have correct name");
        
        assertEquals(20, food1.getNutrition(), "First food should have correct nutrition");
        assertEquals(15, food2.getNutrition(), "Second food should have correct nutrition");
        
        assertEquals(10, food1.getTastyBonus(), "First food should have correct tasty bonus");
        assertEquals(5, food2.getTastyBonus(), "Second food should have correct tasty bonus");
        
        assertEquals(5, food1.getCost(), "First food should have correct cost");
        assertEquals(3, food2.getCost(), "Second food should have correct cost");
    }
    
    /**
     * Helper method to create a mock Image object.
     * @return A mock Image object
     */
    private Image createMockImage() {
        // Return a placeholder 1x1 pixel transparent image
        return new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==");
    }
}
