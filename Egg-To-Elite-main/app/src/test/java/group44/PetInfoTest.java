package group44;

import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PetInfo class.
 * This test suite verifies that the PetInfo class correctly
 * stores and manages pet type information.
 */
public class PetInfoTest {

    private PetInfo petInfo;
    private static final String TEST_NAME = "Mallard";
    private static final String TEST_DESCRIPTION = "A classic duck breed";
    private static final String TEST_SPRITE_FILENAME = "mallard_sprite";
    
    @BeforeEach
    public void setUp() {
        // Initialize a new PetInfo object before each test
        // Using null for the Image since it's difficult to create a real Image in a unit test
        petInfo = new PetInfo(TEST_NAME, null, TEST_DESCRIPTION, TEST_SPRITE_FILENAME);
    }
    
    /**
     * Test that the constructor creates a valid object with all properties set correctly.
     */
    @Test
    public void testConstructor() {
        assertNotNull(petInfo, "PetInfo object should be created successfully");
        assertEquals(TEST_NAME, petInfo.getName(), "Name should be set correctly");
        assertNull(petInfo.getImage(), "Image should be null as we passed null");
        assertEquals(TEST_DESCRIPTION, petInfo.getDescription(), "Description should be set correctly");
        assertEquals(TEST_SPRITE_FILENAME, petInfo.getSpriteFileName(), "Sprite filename should be set correctly");
    }
    
    /**
     * Test setting and getting the pet's name.
     */
    @Test
    public void testSetGetName() {
        String newName = "Pekin";
        petInfo.setName(newName);
        assertEquals(newName, petInfo.getName(), "getName should return the name that was set");
    }
    
    /**
     * Test setting and getting the pet's image.
     */
    @Test
    public void testSetGetImage() {
        // Since we can't easily create a real Image in a unit test environment,
        // we'll just test setting it to null and back
        Image origImage = petInfo.getImage();
        petInfo.setImage(null);
        assertNull(petInfo.getImage(), "Image should be null after setting to null");
        
        // Set back to original value for completeness
        petInfo.setImage(origImage);
    }
    
    /**
     * Test setting and getting the pet's description.
     */
    @Test
    public void testSetGetDescription() {
        String newDescription = "A white domesticated duck breed";
        petInfo.setDescription(newDescription);
        assertEquals(newDescription, petInfo.getDescription(), 
                    "getDescription should return the description that was set");
    }
    
    /**
     * Test setting and getting the pet's sprite filename.
     */
    @Test
    public void testSetGetSpriteFileName() {
        String newFileName = "pekin_sprite";
        petInfo.setSpriteFileName(newFileName);
        assertEquals(newFileName, petInfo.getSpriteFileName(), 
                    "getSpriteFileName should return the sprite filename that was set");
    }
    
    /**
     * Test creating multiple PetInfo objects with different values.
     */
    @Test
    public void testMultiplePetInfoObjects() {
        PetInfo duck1 = new PetInfo("Mallard", null, "Wild duck", "mallard_sprite");
        PetInfo duck2 = new PetInfo("Pekin", null, "Domestic duck", "pekin_sprite");
        
        // Verify each object has its own values
        assertEquals("Mallard", duck1.getName(), "First duck should have correct name");
        assertEquals("Pekin", duck2.getName(), "Second duck should have correct name");
        
        assertEquals("Wild duck", duck1.getDescription(), "First duck should have correct description");
        assertEquals("Domestic duck", duck2.getDescription(), "Second duck should have correct description");
        
        assertEquals("mallard_sprite", duck1.getSpriteFileName(), "First duck should have correct sprite filename");
        assertEquals("pekin_sprite", duck2.getSpriteFileName(), "Second duck should have correct sprite filename");
    }
    
    /**
     * Test that changing one PetInfo object doesn't affect another.
     */
    @Test
    public void testObjectIndependence() {
        PetInfo duck1 = new PetInfo("Mallard", null, "Wild duck", "mallard_sprite");
        PetInfo duck2 = new PetInfo("Pekin", null, "Domestic duck", "pekin_sprite");
        
        // Change values in one object
        duck1.setName("Modified Mallard");
        duck1.setDescription("Modified description");
        
        // Verify the other object is unchanged
        assertEquals("Pekin", duck2.getName(), "Second duck's name should be unchanged");
        assertEquals("Domestic duck", duck2.getDescription(), "Second duck's description should be unchanged");
    }
    
    /**
     * Test with empty string values.
     */
    @Test
    public void testEmptyStringValues() {
        PetInfo emptyPet = new PetInfo("", null, "", "");
        
        assertEquals("", emptyPet.getName(), "Empty name should be allowed");
        assertEquals("", emptyPet.getDescription(), "Empty description should be allowed");
        assertEquals("", emptyPet.getSpriteFileName(), "Empty sprite filename should be allowed");
    }
    
    /**
     * Test with null string values (should handle them gracefully).
     */
    @Test
    public void testNullStringValues() {
        PetInfo nullPet = new PetInfo(null, null, null, null);
        
        assertNull(nullPet.getName(), "Null name should be allowed");
        assertNull(nullPet.getDescription(), "Null description should be allowed");
        assertNull(nullPet.getSpriteFileName(), "Null sprite filename should be allowed");
    }
}