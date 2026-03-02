package group44;

import javafx.scene.image.Image;

/**
 * Represents a virtual pet with various attributes (health, happiness, fullness, etc.),
 * inventory tracking, and associated images.
 */
public class Pet {

    /**
     * The base name of the sprite file (without extension).
     * For example, "myPet" if the images are "myPet.png" and "myPet_flying.png".
     */
    private String spriteFileNameBase;

    /**
     * The pet's default image.
     */
    private Image petImage;

    /**
     * The pet's flying image.
     */
    private Image petFlyingImage;

    /**
     * The pet's name.
     */
    private String name;

    /**
     * The pet's sleepiness level, from 0 (very sleepy) to 100 (fully rested).
     */
    private int sleepiness;

    /**
     * The pet's happiness level, from 0 (very sad) to 100 (very happy).
     */
    private int happiness;

    /**
     * The pet's fullness level, from 0 (very hungry) to 100 (completely full).
     */
    private int fullness;

    /**
     * The pet's health level, from 0 to 100.
     */
    private int health;

    /**
     * The pet's stamina level, from 0 to 100.
     */
    private int stamina;

    /**
     * The pet's score, representing some form of cumulative achievement or points.
     */
    private int score;

    /**
     * The pet's run skill level (minimum 1).
     */
    private int runLevel;

    /**
     * The pet's run experience points, used for leveling up the run skill.
     */
    private int runExperience;

    /**
     * The pet's swim skill level (minimum 1).
     */
    private int swimLevel;

    /**
     * The pet's swim experience points, used for leveling up the swim skill.
     */
    private int swimExperience;

    /**
     * The pet's fly skill level (minimum 1).
     */
    private int flyLevel;

    /**
     * The pet's fly experience points, used for leveling up the fly skill.
     */
    private int flyExperience;

    /**
     * An integer representing the pet's current state (e.g., awake, sleeping, etc.).
     */
    private int state;

    /**
     * The pet's inventory, containing food and gift items.
     */
    private Inventory inventory;

    /**
     * The number of coins the pet currently has.
     */
    private int coins;

    /**
     * The amount by which sleepiness decreases periodically.
     */
    private final int SLEEP_DECREMENT_AMOUNT = 1;

    /**
     * The amount by which fullness decreases periodically.
     */
    private final int HUNGER_DECREMENT_AMOUNT = 3;

    /**
     * The amount by which happiness decreases periodically.
     */
    private final int HAPPINESS_DECREMENT_AMOUNT = 2;

    /**
     * The maximum allowable value for stats (e.g., hunger, happiness).
     */
    private final int MAX_STAT_VALUE = 100;

    /**
     * Constructs a Pet object with parameterized values for its core stats, images, and coins.
     *
     * @param spriteFileNameBase The base name of the sprite file (without extension).
     * @param name The pet's name.
     * @param sleepiness The initial sleepiness level (0-100).
     * @param happiness The initial happiness level (0-100).
     * @param fullness The initial fullness level (0-100).
     * @param health The initial health level (0-100).
     * @param stamina The initial stamina level (0-100).
     * @param score The initial score.
     * @param runLevel The pet's initial run level (minimum 1).
     * @param runExperience The pet's initial run experience.
     * @param swimLevel The pet's initial swim level (minimum 1).
     * @param swimExperience The pet's initial swim experience.
     * @param flyLevel The pet's initial fly level (minimum 1).
     * @param flyExperience The pet's initial fly experience.
     * @param state The pet's current state.
     * @param coins The initial number of coins the pet has.
     */
    public Pet(String spriteFileNameBase, String name, int sleepiness, int happiness, int fullness,
               int health, int stamina, int score, int runLevel, int runExperience, int swimLevel,
               int swimExperience, int flyLevel, int flyExperience, int state, int coins) {

        this.spriteFileNameBase = spriteFileNameBase;
        this.name = name;

        try {
            this.petImage = new Image(spriteFileNameBase + ".png");
            this.petFlyingImage = new Image(spriteFileNameBase + "_flying.png");
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
            this.petImage = this.petFlyingImage = null;
        }

        this.sleepiness = Math.max(0, Math.min(sleepiness, MAX_STAT_VALUE));
        this.happiness = Math.max(0, Math.min(happiness, MAX_STAT_VALUE));
        this.fullness = Math.max(0, Math.min(fullness, MAX_STAT_VALUE));
        this.health = Math.max(0, Math.min(health, MAX_STAT_VALUE));
        this.stamina = Math.max(0, Math.min(stamina, MAX_STAT_VALUE));
        this.score = Math.max(0, score);
        this.runLevel = Math.max(1, runLevel);
        this.runExperience = Math.max(0, runExperience);
        this.swimLevel = Math.max(1, swimLevel);
        this.swimExperience = Math.max(0, swimExperience);
        this.flyLevel = Math.max(1, flyLevel);
        this.flyExperience = Math.max(0, flyExperience);
        this.state = state;
        this.coins = Math.max(0, coins);
        this.inventory = new Inventory();
    }

    /**
     * Retrieves the Inventory associated with this pet.
     *
     * @return The pet's Inventory object.
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Decrements certain stats by fixed amounts. Typically called every x seconds in the game loop.
     */
    public void decrementStats() {
        sleepiness -= SLEEP_DECREMENT_AMOUNT;
        fullness -= HUNGER_DECREMENT_AMOUNT;
        happiness -= HAPPINESS_DECREMENT_AMOUNT;
    }

    /**
     * Feeds the pet, increasing fullness by some multiple of 20 up to the maximum stat value.
     *
     * @param foodAmount The multiplier for how much fullness should increase.
     */
    public void feed(int foodAmount) {
        fullness = Math.min(foodAmount * 20, MAX_STAT_VALUE);
    }

    /**
     * Makes the pet sleep, restoring sleepiness to its maximum value.
     */
    public void sleep() {
        sleepiness = MAX_STAT_VALUE;
    }

    /**
     * Makes the pet play, restoring happiness to its maximum value.
     */
    public void play() {
        happiness = MAX_STAT_VALUE;
    }

    /**
     * Gets the pet's name.
     * @return The pet's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the pet's name.
     * @param name The new name for the pet.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the pet's current fullness level.
     * @return The pet's fullness level.
     */
    public int getHunger() {
        return fullness;
    }

    /**
     * Sets the pet's hunger level.
     * @param newHunger The new hunger value (0-100).
     */
    public void setHunger(int newHunger) {
        fullness = Math.max(0, Math.min(newHunger, MAX_STAT_VALUE));
    }

    /**
     * Gets the base name of the sprite file (without extension).
     * @return The base name of the sprite file.
     */
    public String getSpriteFileNameBase() {
        return spriteFileNameBase;
    }

    /**
     * Gets the pet's sleepiness level.
     * @return The sleepiness value (0-100).
     */
    public int getSleepiness() {
        return sleepiness;
    }

    /**
     * Sets the pet's sleepiness level.
     * @param sleepiness The new sleepiness value (0-100).
     */
    public void setSleepiness(int sleepiness) {
        this.sleepiness = Math.max(0, Math.min(sleepiness, MAX_STAT_VALUE));
    }

    /**
     * Restores the pet's health to its maximum value.
     */
    public void heal() {
        health = MAX_STAT_VALUE;
    }

    /**
     * Gets the pet's health level.
     * @return The health value (0-100).
     */
    public int getHealth() {
        return health;
    }

    /**
     * Sets the pet's health level.
     * @param newHealth The new health value (0-100).
     */
    public void setHealth(int newHealth) {
        health = Math.max(0, Math.min(newHealth, MAX_STAT_VALUE));
    }

    /**
     * Sets the pet's number of coins.
     * @param coins The new number of coins the pet has.
     */
    public void setCoins(int coins) {
        this.coins = coins;
    }

    /**
     * Gets the pet's number of coins.
     * @return The number of coins the pet has.
     */
    public int getCoins() {
        return coins;
    }

    /**
     * Gets the pet's happiness level.
     * @return The happiness value (0-100).
     */
    public int getHappiness() {
        return happiness;
    }

    /**
     * Sets the pet's happiness level.
     * @param happiness The new happiness value (0-100).
     */
    public void setHappiness(int happiness) {
        this.happiness = Math.max(0, Math.min(happiness, MAX_STAT_VALUE));
    }

    /**
     * Gets the pet's fullness level.
     * @return The fullness value (0-100).
     */
    public int getFullness() {
        return fullness;
    }

    /**
     * Sets the pet's fullness level.
     * @param fullness The new fullness value (0-100).
     */
    public void setFullness(int fullness) {
        this.fullness = Math.max(0, Math.min(fullness, MAX_STAT_VALUE));
    }

    /**
     * Gets the pet's stamina level.
     * @return The stamina value (0-100).
     */
    public int getStamina() {
        return stamina;
    }

    /**
     * Sets the pet's stamina level.
     * @param stamina The new stamina value (0-100).
     */
    public void setStamina(int stamina) {
        this.stamina = Math.max(0, Math.min(stamina, MAX_STAT_VALUE));
    }

    /**
     * Gets the pet's run level.
     * @return The run level.
     */
    public int getRunLevel() {
        return runLevel;
    }

    /**
     * Sets the pet's run level.
     * @param runLevel The new run level (minimum 1).
     */
    public void setRunLevel(int runLevel) {
        // Ensure level is at least 1 and at most 100
        this.runLevel = Math.max(1, Math.min(runLevel, MAX_STAT_VALUE));
    }

    /**
     * Gets the pet's run experience.
     * @return The run experience.
     */
    public int getRunExperience() {
        return runExperience;
    }

    /**
     * Sets the pet's run experience.
     * @param runExperience The new run experience.
     */
    public void setRunExperience(int runExperience) {
        this.runExperience = Math.max(0, runExperience);
    }

    /**
     * Gets the pet's swim level.
     * @return The swim level.
     */
    public int getSwimLevel() {
        return swimLevel;
    }

    /**
     * Sets the pet's swim level.
     * @param swimLevel The new swim level (minimum 1).
     */
    public void setSwimLevel(int swimLevel) {
        // Ensure level is at least 1 and at most 100
        this.swimLevel = Math.max(1, Math.min(swimLevel, MAX_STAT_VALUE));
    }

    /**
     * Gets the pet's swim experience.
     * @return The swim experience.
     */
    public int getSwimExperience() {
        return swimExperience;
    }

    /**
     * Sets the pet's swim experience.
     * @param swimExperience The new swim experience.
     */
    public void setSwimExperience(int swimExperience) {
        this.swimExperience = Math.max(0, swimExperience);
    }

    /**
     * Gets the pet's fly level.
     * @return The fly level.
     */
    public int getFlyLevel() {
        return flyLevel;
    }

    /**
     * Sets the pet's fly level.
     * @param flyLevel The new fly level (minimum 1).
     */
    public void setFlyLevel(int flyLevel) {
        // Ensure level is at least 1 and at most 100
        this.flyLevel = Math.max(1, Math.min(flyLevel, MAX_STAT_VALUE));
    }

    /**
     * Gets the pet's fly experience.
     * @return The fly experience.
     */
    public int getFlyExperience() {
        return flyExperience;
    }

    /**
     * Sets the pet's fly experience.
     * @param flyExperience The new fly experience.
     */
    public void setFlyExperience(int flyExperience) {
        this.flyExperience = Math.max(0, flyExperience);
    }

    /**
     * Gets the pet's current state.
     * @return The state value.
     */
    public int getState() {
        return state;
    }

    /**
     * Sets the pet's current state.
     * @param state The new state value.
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * Gets the pet's current score.
     * @return The score value.
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Sets the pet's score.
     * @param score The new score value (minimum 0).
     */
    public void setScore(int score) {
        this.score = Math.max(0, score);
    }

    /**
     * Increments the pet's score by a specified amount.
     * @param amount The amount to increment the score by.
     */
    public void incrementScore(int amount) {
        this.score += amount;
    }

    /**
     * Gets the maximum value for stats such as hunger, happiness, health, etc.
     * @return The maximum stat value.
     */
    public int getMaxStatValue() {
        return MAX_STAT_VALUE;
    }

    /**
     * Gets the default image for this pet.
     * @return The pet's default image.
     */
    public Image getPetImage() {
        return petImage;
    }

    /**
     * Gets the flying image for this pet.
     * @return The flying pet image.
     */
    public Image getPetFlyingImage() {
        return petFlyingImage;
    }

    /**
     * Adds experience to the flying skill and levels up if enough experience is accumulated.
     * Each level requires currentLevel^2 XP at each step.
     *
     * @param experience The amount of experience to add.
     */
    public void addFlyExperience(int experience) {
        flyExperience += experience;
        // Check for level up (or possibly multiple level ups).
        while (flyExperience >= flyLevel * flyLevel) {
            flyExperience -= flyLevel * flyLevel;
            flyLevel++;
        }
    }

    /**
     * Adds experience to the running skill and levels up if enough experience is accumulated.
     * Each run level requires currentLevel^2 XP at each step.
     *
     * @param experience The amount of experience to add.
     */
    public void addRunExperience(int experience) {
        runExperience += experience;
        while (runExperience >= runLevel * runLevel) {
            runExperience -= runLevel * runLevel;
            runLevel++;
        }
    }

    /**
     * Adds experience to the swimming skill and levels up if enough experience is accumulated.
     * Each swim level requires currentLevel^2 XP at each step.
     *
     * @param experience The amount of experience to add.
     */
    public void addSwimExperience(int experience) {
        swimExperience += experience;
        while (swimExperience >= swimLevel * swimLevel) {
            swimExperience -= swimLevel * swimLevel;
            swimLevel++;
        }
    }
}
