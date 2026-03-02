package group44;

import javafx.scene.image.Image;

/**
 * Represents the basic information about a pet type.
 * Contains pet name, image, description, and sprite file name.
 */
public class PetInfo {

    /**
     * The pet type name.
     */
    private String name;

    /**
     * The image representing the pet type.
     */
    private Image image;

    /**
     * A short description of the pet type.
     */
    private String description;

    /**
     * The base filename for the pet’s sprites (without extension).
     */
    private String spriteFileName;

    /**
     * Constructs a new PetInfo object with the specified parameters.
     *
     * @param name The pet type name
     * @param image The pet's image representation
     * @param description A short description of the pet
     * @param spriteFileName The base filename for the pet's sprites
     */
    public PetInfo(String name, Image image, String description, String spriteFileName) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.spriteFileName = spriteFileName;
    }

    /**
     * Gets the name of the pet type.
     *
     * @return The pet's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the pet type.
     *
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the image representation of the pet.
     *
     * @return The pet's image
     */
    public Image getImage() {
        return image;
    }

    /**
     * Sets the image representation of the pet.
     *
     * @param image The image to set
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * Gets the description of the pet.
     *
     * @return The pet's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the pet.
     *
     * @param description The description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the base filename for the pet's sprites.
     *
     * @return The sprite filename (without extension)
     */
    public String getSpriteFileName() {
        return spriteFileName;
    }

    /**
     * Sets the base filename for the pet's sprites.
     *
     * @param spriteFileName The sprite filename to set
     */
    public void setSpriteFileName(String spriteFileName) {
        this.spriteFileName = spriteFileName;
    }
}