package group44;

import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Pet class.
 * Tests the functionality of the Pet class including stat management,
 * level progression, and inventory operations.
 */
public class PetTest {
    
    private Pet pet;
    private final String TEST_SPRITE_BASE = "testduck";
    private final String TEST_NAME = "Daffy";
    
    /**
     * Set up a test pet before each test.
     * We initialize with values that allow easy testing of various scenarios.
     */
    @BeforeEach
    public void setUp() {
        // Mock the Image creation by using a try-catch
        pet = new Pet(TEST_SPRITE_BASE, TEST_NAME, 50, 50, 50, 50, 50, 
                    100, 2, 0, 2, 0, 2, 0, 0, 50);
    }
    
    /**
     * Test the constructor and basic getters.
     */
    @Test
    public void testConstructorAndGetters() {
        assertEquals(TEST_NAME, pet.getName(), "Name should be initialized correctly");
        assertEquals(TEST_SPRITE_BASE, pet.getSpriteFileNameBase(), "Sprite filename should be initialized correctly");
        
        assertEquals(50, pet.getSleepiness(), "Sleepiness should be initialized to 50");
        assertEquals(50, pet.getHappiness(), "Happiness should be initialized to 50");
        assertEquals(50, pet.getFullness(), "Fullness should be initialized to 50");
        assertEquals(50, pet.getHealth(), "Health should be initialized to 50");
        
        assertEquals(2, pet.getRunLevel(), "Run level should be initialized to 2");
        assertEquals(0, pet.getRunExperience(), "Run experience should be initialized to 0");
        
        assertEquals(2, pet.getSwimLevel(), "Swim level should be initialized to 2");
        assertEquals(0, pet.getSwimExperience(), "Swim experience should be initialized to 0");
        
        assertEquals(2, pet.getFlyLevel(), "Fly level should be initialized to 2");
        assertEquals(0, pet.getFlyExperience(), "Fly experience should be initialized to 0");
        
        assertEquals(50, pet.getCoins(), "Coins should be initialized to 50");
        assertEquals(100, pet.getScore(), "Score should be initialized to 100");
    }
    
    /**
     * Test value clamping in the constructor.
     */
    @Test
    public void testConstructorClamping() {
        // Test with values that should be clamped
        Pet extremePet = new Pet(TEST_SPRITE_BASE, TEST_NAME, 
                               -10, 150, 0, 200, 75, 
                               -5, 0, 10, -3, 5, 101, 15, 0, -20);
        
        // Check that stats are properly clamped
        assertEquals(0, extremePet.getSleepiness(), "Negative sleepiness should be clamped to 0");
        assertEquals(100, extremePet.getHappiness(), "Happiness > 100 should be clamped to 100");
        assertEquals(0, extremePet.getFullness(), "Fullness of 0 should remain 0");
        assertEquals(100, extremePet.getHealth(), "Health > 100 should be clamped to 100");
        
        assertEquals(1, extremePet.getRunLevel(), "Run level should be at least 1");
        assertEquals(10, extremePet.getRunExperience(), "Run experience should be positive");
        
        assertEquals(1, extremePet.getSwimLevel(), "Swim level should be at least 1");
        assertEquals(5, extremePet.getSwimExperience(), "Swim experience should be positive");
        
        assertEquals(15, extremePet.getFlyExperience(), "Fly experience should be positive");
        
        assertEquals(0, extremePet.getCoins(), "Negative coins should be clamped to 0");
        assertEquals(0, extremePet.getScore(), "Negative score should be clamped to 0");
    }
    
    /**
     * Test the decrementStats method.
     */
    @Test
    public void testDecrementStats() {
        // Initial values are 50 for sleepiness, happiness, and fullness
        pet.decrementStats();
        
        // Values should decrease by the amounts specified in the class
        assertEquals(49, pet.getSleepiness(), "Sleepiness should decrease by 1");
        assertEquals(48, pet.getHappiness(), "Happiness should decrease by 2");
        assertEquals(47, pet.getFullness(), "Fullness should decrease by 3");
        
        // Call multiple times and check for proper clamping at 0
        for (int i = 0; i < 20; i++) {
            pet.decrementStats();
        }
    }
    
    /**
     * Test the feed method.
     */
    @Test
    public void testFeed() {
        // Set fullness to a known value
        pet.setFullness(20);
        
        // Feed with amount 2 should set fullness to 40 (2*20)
        pet.feed(2);
        assertEquals(40, pet.getFullness(), "Feeding with 2 should set fullness to 40");
        
        // Feed with amount 10 should set fullness to 100 (max)
        pet.feed(10);
        assertEquals(100, pet.getFullness(), "Feeding with 10 should set fullness to 100 (max)");
    }
    
    /**
     * Test sleep and play methods.
     */
    @Test
    public void testSleepAndPlay() {
        // Set to low values
        pet.setSleepiness(10);
        pet.setHappiness(10);
        
        // Sleep should set sleepiness to max (100)
        pet.sleep();
        assertEquals(100, pet.getSleepiness(), "Sleep should set sleepiness to maximum");
        
        // Play should set happiness to max (100)
        pet.play();
        assertEquals(100, pet.getHappiness(), "Play should set happiness to maximum");
    }
    
    /**
     * Test experience and leveling for running skill.
     */
    @Test
    public void testRunExperienceAndLeveling() {
        // Initial run level is 2, experience is 0
        
        // Add experience less than needed to level up
        pet.addRunExperience(3);
        assertEquals(3, pet.getRunExperience(), "Run experience should increase");
        assertEquals(2, pet.getRunLevel(), "Run level should not change");
        
        // Add enough experience to level up (need 4 more to reach 2^2 = 4)
        pet.addRunExperience(1);
        assertEquals(0, pet.getRunExperience(), "Run experience should reset after level up");
        assertEquals(3, pet.getRunLevel(), "Run level should increase to 3");
        
        // Add enough for multiple level ups
        pet.addRunExperience(15); // 9 (level 3 req) + 6 overflow
        assertEquals(6, pet.getRunExperience(), "Excess experience should carry over");
        assertEquals(4, pet.getRunLevel(), "Run level should increase to 4");
    }
    
    /**
     * Test experience and leveling for swimming skill.
     */
    @Test
    public void testSwimExperienceAndLeveling() {
        // Similar to run test
        pet.addSwimExperience(3);
        assertEquals(3, pet.getSwimExperience(), "Swim experience should increase");
        assertEquals(2, pet.getSwimLevel(), "Swim level should not change");
        
        pet.addSwimExperience(1);
        assertEquals(0, pet.getSwimExperience(), "Swim experience should reset after level up");
        assertEquals(3, pet.getSwimLevel(), "Swim level should increase to 3");
    }
    
    /**
     * Test experience and leveling for flying skill.
     */
    @Test
    public void testFlyExperienceAndLeveling() {
        // Similar to run test
        pet.addFlyExperience(3);
        assertEquals(3, pet.getFlyExperience(), "Fly experience should increase");
        assertEquals(2, pet.getFlyLevel(), "Fly level should not change");
        
        pet.addFlyExperience(1);
        assertEquals(0, pet.getFlyExperience(), "Fly experience should reset after level up");
        assertEquals(3, pet.getFlyLevel(), "Fly level should increase to 3");
    }
    
    /**
     * Test score management.
     */
    @Test
    public void testScoreManagement() {
        // Initial score is 100
        
        // Set a new score
        pet.setScore(200);
        assertEquals(200, pet.getScore(), "Score should be updated");
        
        // Increment score
        pet.incrementScore(50);
        assertEquals(250, pet.getScore(), "Score should be incremented correctly");
        
        // Test negative clamping
        pet.setScore(-10);
        assertEquals(0, pet.getScore(), "Negative scores should be clamped to 0");
    }
    
    /**
     * Test setter methods with boundary values.
     */
    @Test
    public void testSetterBoundaries() {
        // Test setting values beyond boundaries
        pet.setSleepiness(120);
        assertEquals(100, pet.getSleepiness(), "Sleepiness > 100 should be clamped to 100");
        
        pet.setSleepiness(-10);
        assertEquals(0, pet.getSleepiness(), "Sleepiness < 0 should be clamped to 0");
        
        // Similar tests for other stats
        pet.setHappiness(150);
        assertEquals(100, pet.getHappiness(), "Happiness > 100 should be clamped to 100");
        
        pet.setFullness(-5);
        assertEquals(0, pet.getFullness(), "Fullness < 0 should be clamped to 0");
        
        // Test level boundaries
        pet.setRunLevel(0);
        assertEquals(1, pet.getRunLevel(), "Run level should be at least 1");
        
        pet.setRunLevel(101);
        assertEquals(100, pet.getRunLevel(), "Run level should be at most 100");
    }
    
    /**
     * Test the inventory functionality.
     */
    @Test
    public void testInventory() {
        Inventory inventory = pet.getInventory();
        assertNotNull(inventory, "Pet should have an inventory");
    }
    
    /**
     * Test the coins management.
     */
    @Test
    public void testCoinsManagement() {
        // Set coins value
        pet.setCoins(100);
        assertEquals(100, pet.getCoins(), "Coins should be updated correctly");
    }
    
    /**
     * Test the healing method.
     */
    @Test
    public void testHealing() {
        // Set health to a low value
        pet.setHealth(20);
        
        // Heal pet
        pet.heal();
        assertEquals(100, pet.getHealth(), "Health should be restored to maximum");
    }
}