package group44;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

/**
 * JUnit tests for the Inventory class.
 * Tests functionality for managing food and gift items.
 */
public class InventoryTest {
    
    private Inventory inventory;
    
    @BeforeEach
    public void setUp() {
        inventory = new Inventory();
    }
    
    /**
     * Test that a new inventory is empty.
     */
    @Test
    public void testNewInventoryIsEmpty() {
        assertTrue(inventory.getFoodItems().isEmpty(), "Food items should be empty in a new inventory");
        assertTrue(inventory.getGiftItems().isEmpty(), "Gift items should be empty in a new inventory");
    }
    
    /**
     * Test adding food items.
     */
    @Test
    public void testAddFood() {
        // Add a single food item
        inventory.addFood("Apple", 3);
        
        // Verify it was added correctly
        Map<String, Integer> foodItems = inventory.getFoodItems();
        assertEquals(1, foodItems.size(), "Should have 1 food item type");
        assertTrue(foodItems.containsKey("Apple"), "Should contain 'Apple'");
        assertEquals(3, foodItems.get("Apple"), "Should have 3 Apples");
        
        // Add a different food item
        inventory.addFood("Banana", 2);
        
        // Verify both items exist
        foodItems = inventory.getFoodItems();
        assertEquals(2, foodItems.size(), "Should have 2 food item types");
        assertEquals(3, foodItems.get("Apple"), "Should still have 3 Apples");
        assertEquals(2, foodItems.get("Banana"), "Should have 2 Bananas");
        
        // Add more of an existing item
        inventory.addFood("Apple", 2);
        
        // Verify quantity was increased
        foodItems = inventory.getFoodItems();
        assertEquals(2, foodItems.size(), "Should still have 2 food item types");
        assertEquals(5, foodItems.get("Apple"), "Should now have 5 Apples");
        assertEquals(2, foodItems.get("Banana"), "Should still have 2 Bananas");
    }
    
    /**
     * Test adding gift items.
     */
    @Test
    public void testAddGift() {
        // Add a single gift item
        inventory.addGift("Ball", 1);
        
        // Verify it was added correctly
        Map<String, Integer> giftItems = inventory.getGiftItems();
        assertEquals(1, giftItems.size(), "Should have 1 gift item type");
        assertTrue(giftItems.containsKey("Ball"), "Should contain 'Ball'");
        assertEquals(1, giftItems.get("Ball"), "Should have 1 Ball");
        
        // Add a different gift item
        inventory.addGift("Teddy Bear", 2);
        
        // Verify both items exist
        giftItems = inventory.getGiftItems();
        assertEquals(2, giftItems.size(), "Should have 2 gift item types");
        assertEquals(1, giftItems.get("Ball"), "Should still have 1 Ball");
        assertEquals(2, giftItems.get("Teddy Bear"), "Should have 2 Teddy Bears");
        
        // Add more of an existing item
        inventory.addGift("Ball", 3);
        
        // Verify quantity was increased
        giftItems = inventory.getGiftItems();
        assertEquals(2, giftItems.size(), "Should still have 2 gift item types");
        assertEquals(4, giftItems.get("Ball"), "Should now have 4 Balls");
        assertEquals(2, giftItems.get("Teddy Bear"), "Should still have 2 Teddy Bears");
    }
    
    /**
     * Test using food items successfully.
     */
    @Test
    public void testUseFoodSuccess() {
        // Add food items
        inventory.addFood("Apple", 5);
        
        // Use some food
        boolean result = inventory.useFood("Apple", 2);
        
        // Verify use was successful
        assertTrue(result, "Using 2 Apples should succeed");
        assertEquals(3, inventory.getFoodItems().get("Apple"), "Should have 3 Apples left");
        
        // Use the rest of the food
        result = inventory.useFood("Apple", 3);
        
        // Verify use was successful and item was removed
        assertTrue(result, "Using 3 Apples should succeed");
        assertFalse(inventory.getFoodItems().containsKey("Apple"), "Apple should be removed when quantity reaches 0");
    }
    
    /**
     * Test using gift items successfully.
     */
    @Test
    public void testUseGiftSuccess() {
        // Add gift items
        inventory.addGift("Ball", 3);
        
        // Use some gift
        boolean result = inventory.useGift("Ball", 1);
        
        // Verify use was successful
        assertTrue(result, "Using 1 Ball should succeed");
        assertEquals(2, inventory.getGiftItems().get("Ball"), "Should have 2 Balls left");
        
        // Use the rest of the gift
        result = inventory.useGift("Ball", 2);
        
        // Verify use was successful and item was removed
        assertTrue(result, "Using 2 Balls should succeed");
        assertFalse(inventory.getGiftItems().containsKey("Ball"), "Ball should be removed when quantity reaches 0");
    }
    
    /**
     * Test using food items when there aren't enough.
     */
    @Test
    public void testUseFoodFailure() {
        // Add food items
        inventory.addFood("Apple", 2);
        
        // Try to use more than available
        boolean result = inventory.useFood("Apple", 3);
        
        // Verify use failed
        assertFalse(result, "Using 3 Apples should fail when only 2 are available");
        assertEquals(2, inventory.getFoodItems().get("Apple"), "Should still have 2 Apples");
        
        // Try to use an item that doesn't exist
        result = inventory.useFood("Banana", 1);
        
        // Verify use failed
        assertFalse(result, "Using Banana should fail when none are available");
    }
    
    /**
     * Test using gift items when there aren't enough.
     */
    @Test
    public void testUseGiftFailure() {
        // Add gift items
        inventory.addGift("Ball", 1);
        
        // Try to use more than available
        boolean result = inventory.useGift("Ball", 2);
        
        // Verify use failed
        assertFalse(result, "Using 2 Balls should fail when only 1 is available");
        assertEquals(1, inventory.getGiftItems().get("Ball"), "Should still have 1 Ball");
        
        // Try to use an item that doesn't exist
        result = inventory.useGift("Teddy Bear", 1);
        
        // Verify use failed
        assertFalse(result, "Using Teddy Bear should fail when none are available");
    }
    
    /**
     * Test serializing inventory to CSV string.
     */
    @Test
    public void testToCSV() {
        // Empty inventory
        String csv = inventory.toCSV();
        assertEquals("food:;gift:", csv, "Empty inventory should serialize correctly");
        
        // Add some items
        inventory.addFood("Apple", 3);
        inventory.addFood("Banana", 2);
        inventory.addGift("Ball", 1);
        inventory.addGift("Teddy Bear", 2);
        
        // Get CSV
        csv = inventory.toCSV();
        
        // Verify CSV format
        assertTrue(csv.startsWith("food:"), "CSV should start with food section");
        assertTrue(csv.contains(";gift:"), "CSV should contain gift section");
        assertTrue(csv.contains("Apple=3"), "CSV should contain Apple quantity");
        assertTrue(csv.contains("Banana=2"), "CSV should contain Banana quantity");
        assertTrue(csv.contains("Ball=1"), "CSV should contain Ball quantity");
        assertTrue(csv.contains("Teddy Bear=2"), "CSV should contain Teddy Bear quantity");
    }
    
    /**
     * Test deserializing inventory from CSV string.
     */
    @Test
    public void testFromCSV() {
        // Valid CSV with food and gift items
        String csv = "food:Apple=3|Banana=2;gift:Ball=1|Teddy Bear=2";
        
        // Load from CSV
        inventory.fromCSV(csv);
        
        // Verify food items
        Map<String, Integer> foodItems = inventory.getFoodItems();
        assertEquals(2, foodItems.size(), "Should have 2 food item types");
        assertEquals(3, foodItems.get("Apple"), "Should have 3 Apples");
        assertEquals(2, foodItems.get("Banana"), "Should have 2 Bananas");
        
        // Verify gift items
        Map<String, Integer> giftItems = inventory.getGiftItems();
        assertEquals(2, giftItems.size(), "Should have 2 gift item types");
        assertEquals(1, giftItems.get("Ball"), "Should have 1 Ball");
        assertEquals(2, giftItems.get("Teddy Bear"), "Should have 2 Teddy Bears");
    }
    
    /**
     * Test deserializing inventory from CSV with empty sections.
     */
    @Test
    public void testFromCSVWithEmptySections() {
        // CSV with empty food section
        String csvEmptyFood = "food:;gift:Ball=1|Teddy Bear=2";
        
        // Load from CSV
        inventory.fromCSV(csvEmptyFood);
        
        // Verify food items are empty
        assertTrue(inventory.getFoodItems().isEmpty(), "Food items should be empty");
        
        // Verify gift items
        Map<String, Integer> giftItems = inventory.getGiftItems();
        assertEquals(2, giftItems.size(), "Should have 2 gift item types");
        assertEquals(1, giftItems.get("Ball"), "Should have 1 Ball");
        assertEquals(2, giftItems.get("Teddy Bear"), "Should have 2 Teddy Bears");
        
        // CSV with empty gift section
        String csvEmptyGift = "food:Apple=3|Banana=2;gift:";
        
        // Load from CSV
        inventory.fromCSV(csvEmptyGift);
        
        // Verify food items
        Map<String, Integer> foodItems = inventory.getFoodItems();
        assertEquals(2, foodItems.size(), "Should have 2 food item types");
        assertEquals(3, foodItems.get("Apple"), "Should have 3 Apples");
        assertEquals(2, foodItems.get("Banana"), "Should have 2 Bananas");
        
        // Verify gift items are empty
        assertTrue(inventory.getGiftItems().isEmpty(), "Gift items should be empty");
    }
    
    /**
     * Test deserializing inventory from null or empty CSV.
     */
    @Test
    public void testFromCSVWithNullOrEmpty() {
        // Add some items first
        inventory.addFood("Apple", 3);
        inventory.addGift("Ball", 1);
        
        // Load from null CSV
        inventory.fromCSV(null);
        
        // Verify inventory is cleared
        assertTrue(inventory.getFoodItems().isEmpty(), "Food items should be cleared when loading from null");
        assertTrue(inventory.getGiftItems().isEmpty(), "Gift items should be cleared when loading from null");
        
        // Add some items again
        inventory.addFood("Apple", 3);
        inventory.addGift("Ball", 1);
        
        // Load from empty CSV
        inventory.fromCSV("");
        
        // Verify inventory is cleared
        assertTrue(inventory.getFoodItems().isEmpty(), "Food items should be cleared when loading from empty string");
        assertTrue(inventory.getGiftItems().isEmpty(), "Gift items should be cleared when loading from empty string");
    }
    
    /**
     * Test roundtrip: toCSV followed by fromCSV.
     */
    @Test
    public void testRoundTrip() {
        // Add some items
        inventory.addFood("Apple", 3);
        inventory.addFood("Banana", 2);
        inventory.addGift("Ball", 1);
        inventory.addGift("Teddy Bear", 2);
        
        // Convert to CSV
        String csv = inventory.toCSV();
        
        // Create a new inventory and load from CSV
        Inventory newInventory = new Inventory();
        newInventory.fromCSV(csv);
        
        // Verify both inventories have the same items
        assertEquals(inventory.getFoodItems().size(), newInventory.getFoodItems().size(), 
                    "Both inventories should have the same number of food items");
        assertEquals(inventory.getGiftItems().size(), newInventory.getGiftItems().size(), 
                    "Both inventories should have the same number of gift items");
        
        for (Map.Entry<String, Integer> entry : inventory.getFoodItems().entrySet()) {
            assertEquals(entry.getValue(), newInventory.getFoodItems().get(entry.getKey()), 
                        "Food item " + entry.getKey() + " should have the same quantity in both inventories");
        }
        
        for (Map.Entry<String, Integer> entry : inventory.getGiftItems().entrySet()) {
            assertEquals(entry.getValue(), newInventory.getGiftItems().get(entry.getKey()), 
                        "Gift item " + entry.getKey() + " should have the same quantity in both inventories");
        }
    }
    
    /**
     * Test deserializing from malformed CSV.
     */
    @Test
    public void testFromMalformedCSV() {
        // Add some items first to ensure they're cleared
        inventory.addFood("Apple", 3);
        inventory.addGift("Ball", 1);
        
        try {
            // CSV with malformed quantity
            inventory.fromCSV("food:Apple=NotANumber;gift:");
            
            // This should cause a NumberFormatException, so if we get here, the test should fail
            fail("Should throw NumberFormatException for malformed quantity");
        } catch (NumberFormatException e) {
            // Expected, test passes
        }
        
        // Verify the inventory was cleared even though parsing failed
        assertTrue(inventory.getFoodItems().isEmpty(), "Food items should be cleared when loading from malformed CSV");
        assertTrue(inventory.getGiftItems().isEmpty(), "Gift items should be cleared when loading from malformed CSV");
    }
}
